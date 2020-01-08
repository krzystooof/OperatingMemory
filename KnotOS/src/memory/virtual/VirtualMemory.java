package memory.virtual;

import java.util.*;

import memory.SegmentTable;
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
    private PhysicalMemoryManager RAM;

    private Queue<Integer> segmentQueue = new LinkedList<>();
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
                throw new IllegalStateException("OUT OF VIRTUAL MEMORY");
            }
        } else if (textSize <= swapLeft) {
            processMap.put(PID, new Integer[]{segmentCounter, -1});
            loadSegment(Arrays.copyOfRange(assemblyCode, 0, textSize));
        } else {
            throw new IllegalStateException("OUT OF VIRTUAL MEMORY");
        }
    }

    /**
     * Remove program from memory
     *
     * @param PID process unique ID
     */
    public boolean delete(int PID) {
        int textSegmentId = processMap.get(PID)[0];
        int dataSegmentId = processMap.get(PID)[1];
        boolean released = false;

        if (dataSegmentId > 0) {
            if (segments.hasHighestBase(dataSegmentId)) {
                swapLeft += segments.getLimit(dataSegmentId);
                released=true;
            }
            segments.delete(dataSegmentId);
            segmentQueue.remove(dataSegmentId);
        }
        if (segments.hasHighestBase(textSegmentId)) {
            swapLeft += segments.getLimit(textSegmentId);
        }
        segments.delete(textSegmentId);
        segmentQueue.remove(textSegmentId);
        return released;
    }

    /**
     * Read memory cell
     *
     * @param PID    process unique ID
     * @param OFFSET demanded data's index
     * @return byte from RAM
     */
    public byte read(int PID, int OFFSET) {
        int textSegmentId = processMap.get(PID)[0];
        int dataSegmentId = processMap.get(PID)[1];
        int textLimit = segments.getLimit(textSegmentId);
        int dataLimit = 0;
        if (dataSegmentId > 0) {
            dataLimit = segments.getLimit(dataSegmentId);
        }

        if (OFFSET >= 0 && OFFSET < textLimit) {
            if (segments.inSwapFile(textSegmentId) == Boolean.FALSE) {
                try {
                    return RAM.read(textSegmentId, OFFSET);
                } catch (IllegalArgumentException error) {
                    System.out.println(error.getMessage());
                }
            } else {
                swapToRam(textSegmentId);
                return read(PID, OFFSET);
            }
        } else if (OFFSET >= textLimit && OFFSET < textLimit + dataLimit) {
            if (segments.inSwapFile(dataSegmentId) == Boolean.FALSE) {
                int index = OFFSET - segments.getLimit(textSegmentId);
                return RAM.read(dataSegmentId, index);
            } else {
                swapToRam(dataSegmentId);
                return read(PID, OFFSET);
            }
        }
        throw new IllegalArgumentException("SEGMENTATION ERROR");
    }

    /**
     * Edit memory cell
     *
     * @param PID    process id
     * @param OFFSET index to write
     * @param data   data to write
     * @throws IllegalArgumentException when calling data outside assigned block
     */
    public void write(int PID, int OFFSET, byte data) {
        int textSegmentId = processMap.get(PID)[0];
        int dataSegmentId = processMap.get(PID)[1];
        int textLimit = segments.getLimit(textSegmentId);
        int dataLimit = 0;
        if (dataSegmentId > 0) {
            dataLimit = segments.getLimit(dataSegmentId);
        }

        if (OFFSET >= 0 && OFFSET < textLimit) {
            if (segments.inSwapFile(textSegmentId) == Boolean.FALSE) {
                try {
                    RAM.write(textSegmentId, OFFSET, data);
                } catch (IllegalArgumentException error) {
                    System.out.println(error.getMessage());
                }
            } else {
                swapToRam(textSegmentId);
                write(PID, OFFSET, data);
            }
        } else if (OFFSET >= textLimit && OFFSET < textLimit + dataLimit) {
            int dataOffset = OFFSET - textLimit;
            if (segments.inSwapFile(dataSegmentId) == Boolean.FALSE) {
                try {
                    RAM.write(dataSegmentId, dataOffset, data);
                } catch (IllegalArgumentException error) {
                    System.out.println(error.getMessage());
                }
            } else {
                swapToRam(dataSegmentId);
                write(PID, OFFSET, data);
            }
        }
    }

    /**
     * Return specified process memory
     *
     * @param PID process id
     * @return array of bytes
     */
    public byte[] showProcessData(int PID) {
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
     */
    public int getSpaceLeft(boolean physical, boolean virtual) {
        if (virtual) {
            return swapLeft;
        }
        if (physical) {
            return RAM.checkAvailableSpace();
        }
        if (virtual && physical) {
            return swapLeft + RAM.checkAvailableSpace();
        }
        System.out.println("What the heck ?");
        return -1;
    }

    /**
     * Get segment's limit.
     *
     * @param PID         process ID
     * @param textSegment to choose text or data segment
     * @return segment's limit
     */
    public int getLimit(int PID, boolean textSegment) {
        int ID;
        if (textSegment) {
            ID = processMap.get(PID)[0];
        } else {
            ID = processMap.get(PID)[1];
        }
        return segments.getLimit(ID);
    }

    /**
     * Move segment from swap file to RAM
     */
    private void swapToRam(int ID) {
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
     */
    private void swapToFile(int ID) {
        try {
            byte[] data = readSegment(ID);
            int LIMIT = segments.getLimit(ID);
            swapLeft -= LIMIT;
            int writePointer = SWAP_SIZE - swapLeft;
            if (swapLeft < LIMIT) {
                throw new IllegalStateException("OUT OF VIRTUAL MEMORY");
            }
            segments.updateToSwap(ID);
            segments.setBase(ID, writePointer);
            System.arraycopy(data, 0, swapFile, writePointer, LIMIT);
        } catch (IllegalArgumentException error) {
            System.out.println(error.getMessage());
        }
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
            } catch (IllegalArgumentException error) {
                System.out.println(error.getMessage());
            }
        }
        return data;
    }

}




