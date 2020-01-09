package memory.virtual;

import java.util.*;

import memory.physical.PhysicalMemoryManager;

/**
 * VirtualMemory is responsible for KnotOs's memory management.
 * Implemented by memory segmentation.
 *
 * @author Roland
 * @since 2019.12.10
 */
public class VirtualMemory {
    private HashMap<Integer, Integer[]> processMap = new HashMap<>();
    private SegmentTable segments = new SegmentTable();
    private Queue<Integer> segmentQueue = new LinkedList<>();
    private PhysicalMemoryManager RAM;
    private Integer segmentCounter = 0;
    private int SWAP_SIZE;
    private byte[] swapFile;
    private int swapLeft;

    /**
     * Initialise VirtualMemory with specified parameters
     *
     * @param virtualSize  size of swap file
     * @param physicalSize size of RAM
     */
    public VirtualMemory(int virtualSize, int physicalSize) {
        this.SWAP_SIZE = virtualSize;
        this.swapLeft = SWAP_SIZE;
        this.swapFile = new byte[SWAP_SIZE];
        this.RAM = new PhysicalMemoryManager(physicalSize, segments);
    }

    /**
     * Read memory cell
     *
     * @param PID    process unique id
     * @param OFFSET demanded cell's index
     */
    public byte read(int PID, int OFFSET) {
        return accessRam(PID, OFFSET, false, (byte) 0);
    }

    /**
     * Write to memory cell
     *
     * @param PID    process unique id
     * @param OFFSET demanded cell's index
     * @param DATA   byte to save
     */
    public void write(int PID, int OFFSET, byte DATA) {
        accessRam(PID, OFFSET, true, DATA);
    }

    /**
     * Allocate process in memory
     *
     * @param assemblyCode block of assembly instructions
     * @param PID          process unique ID
     * @param textSize     text section size
     * @param dataSize     data section size
     * @throws IllegalStateException when no memory left
     */
    public void load(int PID, int textSize, int dataSize, byte[] assemblyCode) {

        if (dataSize > 0) {
            if (swapLeft >= dataSize + textSize) {
                processMap.put(PID, new Integer[]{segmentCounter, segmentCounter + 1});
                loadSegment(Arrays.copyOfRange(assemblyCode, 0, textSize));
                loadSegment(Arrays.copyOfRange(assemblyCode, textSize, textSize + dataSize));
            } else {
                throw new IllegalStateException("VIRTUAL MEMORY ERROR: SWAP FILE SHORTAGE");
            }
        } else if (textSize <= swapLeft) {
            processMap.put(PID, new Integer[]{segmentCounter, -1});
            loadSegment(Arrays.copyOfRange(assemblyCode, 0, textSize));
        } else {
            throw new IllegalStateException("VIRTUAL MEMORY ERROR: SWAP FILE SHORTAGE");
        }
    }

    /**
     * Remove program from memory
     *
     * @param PID process unique ID
     */
    public void delete(int PID) {
        int textSegmentId = processMap.get(PID)[0];
        int dataSegmentId = processMap.get(PID)[1];

        if (dataSegmentId > 0) {
            if (segments.hasHighestBase(dataSegmentId)) {
                swapLeft += segments.getLimit(dataSegmentId);
            }
            segments.delete(dataSegmentId);
            segmentQueue.remove(dataSegmentId);
        }
        if (segments.hasHighestBase(textSegmentId)) {
            swapLeft += segments.getLimit(textSegmentId);
        }
        segments.delete(textSegmentId);
        segmentQueue.remove(textSegmentId);
    }

    /**
     * Return specified process memory
     *
     * @param PID process id
     * @return array of bytes
     * @JCB
     */
    public byte[] getProcessMemory(int PID) {
        int textSegmentID = processMap.get(PID)[0];
        int dataSegmentID = processMap.get(PID)[1];

        if (dataSegmentID > 0) {
            byte[] textSeg = readSegment(textSegmentID);
            byte[] dataSeg = readSegment(dataSegmentID);
            System.arraycopy(dataSeg, 0, textSeg, textSeg.length, dataSeg.length);
            return dataSeg;
        } else {
            return readSegment(textSegmentID);
        }
    }

    /**
     * Return size of free memory
     *
     * @param virtual specifies if show swap or RAM left
     * @JCB
     */
    public int getSpaceLeft(boolean physical, boolean virtual) {
        if (virtual && physical) {
            return swapLeft + RAM.checkAvailableSpace();
        } else if (virtual) {
            return swapLeft;
        } else if (physical) {
            return RAM.checkAvailableSpace();
        }
        throw new IllegalStateException("VIRTUAL MEMORY ERROR: INVALID OPERATION");
    }

    /**
     * Return memory content
     *
     * @param virtual choose RAM or swap file
     * @JCB
     */
    public byte[] getMemory(boolean physical, boolean virtual) {
        if (virtual) {
            return swapFile;
        } else if (physical) {
            return RAM.read();
        }
        throw new IllegalStateException("VIRTUAL MEMORY ERROR: INVALID OPERATION");
    }

    /**
     * Print segment table's records for specified process
     *
     * @JCB
     */
    public void showSegmentTable() {
        processMap.forEach((PID, SEGMENTS) -> {
            System.out.println("PROCESS: " + PID);
            System.out.println("TEXT SEGMENT");
            printSegment(SEGMENTS[0]);
            if (SEGMENTS[1] != -1) {
                System.out.println("DATA SEGMENT");
                printSegment(SEGMENTS[1]);
            }

        });
    }

    /**
     * Move segment from swap file to RAM
     *
     * @JCB
     */
    public void swapToRam(int ID) {
        int BASE = segments.getBase(ID);
        int LIMIT = segments.getLimit(ID);
        byte[] data = new byte[LIMIT];

        System.arraycopy(swapFile, BASE, data, 0, LIMIT);
        try {
            RAM.write(data, ID);
            segmentQueue.add(ID);
        } catch (IllegalArgumentException page_fault) {
            int idToRemove = segmentQueue.remove();
            swapToFile(idToRemove);
            swapToRam(ID);
        }

    }

    /**
     * Move segment from RAM to swap file
     *
     * @JCB
     */
    public void swapToFile(int ID) {
        try {
            byte[] data = readSegment(ID);
            int BASE = segments.getBase(ID);
            int LIMIT = segments.getLimit(ID);
            segments.updateToSwap(ID);
            System.arraycopy(data, 0, swapFile, BASE, LIMIT);
        } catch (IllegalArgumentException ramError) {
            System.out.println("VIRTUAL MEMORY ERROR: INVALID OPERATION");
            throw ramError;
        }
    }

    /**
     * Read memory cell
     *
     * @param PID    process unique ID
     * @param OFFSET demanded data's index
     * @return byte from RAM
     */
    private byte accessRam(int PID, int OFFSET, boolean write, byte data) {
        int textID = processMap.get(PID)[0];
        int dataID = processMap.get(PID)[1];
        int textLimit = segments.getLimit(textID);
        int dataLimit = segments.getLimit(dataID);

        if (OFFSET >= 0 && OFFSET < textLimit) {
            if (segments.inSwapFile(textID) == Boolean.FALSE) {
                if (write) {
                    RAM.write(textID, OFFSET, data);
                } else {
                    return RAM.read(textID, OFFSET);
                }
            } else {
                swapToRam(textID);
                // POSSIBLE BUG
                segments.updateToRam(textID);
                return accessRam(PID, OFFSET, write, data);
            }
        } else if (OFFSET >= textLimit && OFFSET < textLimit + dataLimit) {
            if (segments.inSwapFile(dataID) == Boolean.FALSE) {
                // HERE POSSIBLE BUG
                OFFSET = OFFSET - segments.getLimit(textID);
                if (write) {
                    RAM.write(dataID, OFFSET, data);
                } else {
                    return RAM.read(dataID, OFFSET);
                }
            } else {
                swapToRam(dataID);
                // POSSIBLE BUG
                segments.updateToRam(dataID);
                return accessRam(PID, OFFSET, write, data);
            }
        }
        throw new IllegalArgumentException("VIRTUAL MEMORY ERROR: SEGMENTATION FAULT");
    }

    /**
     * Print segment table's records for specified segment
     */
    private void printSegment(int ID) {
        System.out.println("SEGMENT ID: " + ID);
        System.out.println("BASE / RELOCATION: " + segments.getBase(ID));
        System.out.println("LIMIT: " + segments.getLimit(ID));
        System.out.println("IN SWAP FILE: " + segments.inSwapFile(ID));
    }

    /**
     * Load segment to swap file
     */
    private void loadSegment(byte[] code) {
        int writePointer = SWAP_SIZE - swapLeft;
        segments.addSegment(segmentCounter, writePointer, code.length);
        System.arraycopy(code, 0, swapFile, writePointer, code.length);
        swapLeft -= segments.getLimit(segmentCounter);
        segmentCounter++;
    }

    /**
     * Read segment's data from RAM or swap file
     *
     * @param ID segment's ID
     * @return array of bytes
     */
    private byte[] readSegment(int ID) {
        int BASE = segments.getBase(ID);
        int LIMIT = segments.getLimit(ID);
        byte[] data = new byte[LIMIT];
        if (segments.inSwapFile(ID)) {
            System.arraycopy(swapFile, BASE, data, 0, LIMIT);
        } else {
            try {
                data = RAM.read(ID);
            } catch (IllegalArgumentException ramError) {
                System.out.println("VIRTUAL MEMORY ERROR: INVALID OPERATION");
                throw ramError;
            }
        }
        return data;
    }

}




