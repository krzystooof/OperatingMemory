package memory.virtual;

import java.util.Queue;;
import java.util.LinkedList;;;
import java.util.HashMap;
import java.util.stream.IntStream;

import memory.SegmentTable;
import memory.physical.PhysicalMemoryManager;


public class VirtualMemory {
    private HashMap<Integer, Integer[]> processMap = new HashMap<>();
    private SegmentTable segments = new SegmentTable();
    private PhysicalMemoryManager RAM;

    private Queue<Integer> segmentQueue = new LinkedList<>();
    private Integer segmentCounter = 0;
    private int writePointer = 0;
    private int SWAP_SIZE;
    private byte[] swapFile = new byte[SWAP_SIZE];
    private int swapLeft = SWAP_SIZE;

    public VirtualMemory(int virtualSize, int physicalSize) {
        this.SWAP_SIZE = virtualSize;
        this.RAM = new PhysicalMemoryManager(physicalSize);
    }

    /**
     * Allocates process in memory
     *
     * @param assemblyCode block of assembly instructions
     * @param PID          process unique ID
     * @param textSize     text section size
     * @param dataSize     data section size
     * @throws IllegalStateException when no memory left
     */
    public void loadProcess(int PID, int textSize, int dataSize, byte[] assemblyCode) {
        writePointer = SWAP_SIZE - swapLeft;

        if (swapLeft >= textSize) {
            loadSegment(textSize, assemblyCode);
            processMap.put(PID, new Integer[]{segmentCounter, -1});
            segmentQueue.add(segmentCounter);
        } else {
            throw new IllegalStateException("OUT OF SPACE");
        }

        if (dataSize > 0) {
            if (swapLeft >= dataSize) {
                loadSegment(textSize, assemblyCode);
                loadSegment(dataSize, assemblyCode);
                processMap.put(PID, new Integer[]{segmentCounter, segmentCounter - 1});
                segmentQueue.add(segmentCounter);
            } else {
                throw new IllegalStateException("OUT OF SPACE");
            }
        }
    }

    /**
     * Removes program from memory
     *
     * @param PID process unique ID
     */
    public void flushProcess(int PID) {

        int textSegmentId = processMap.get(PID)[0];
        int dataSegmentId = processMap.get(PID)[1];

        if (segments.inSwapFile(textSegmentId)) {
            int base = segments.getBase(textSegmentId);
            int limit = segments.getLimit(textSegmentId);
            wipeSegment(base, limit, textSegmentId);
            segmentQueue.remove(textSegmentId);
        } else {
            RAM.wipe(textSegmentId);
        }

        if (dataSegmentId > 0) {
            if (segments.inSwapFile(dataSegmentId)) {
                int base = segments.getBase(dataSegmentId);
                int limit = segments.getLimit(dataSegmentId);
                wipeSegment(base, limit, dataSegmentId);
                segmentQueue.remove(dataSegmentId);
            } else {
                RAM.wipe(dataSegmentId);
            }
        }
    }

    /**
     * Reads memory cell
     *
     * @param PID    process unique ID
     * @param OFFSET demanded data index
     * @return byte from RAM
     */
    public byte read(int PID, int OFFSET) {
        int textSegmentId = processMap.get(PID)[0];
        int dataSegmentId = processMap.get(PID)[1];

        if (OFFSET <= segments.getLimit(textSegmentId)) {
            if (segments.inSwapFile(textSegmentId) == Boolean.FALSE) {
                try {
                    return RAM.read(textSegmentId, OFFSET);
                } catch (IllegalArgumentException error) {
                    System.out.println(error.getMessage());
                }
            } else {
                swapToRam(textSegmentId);
                read(PID, OFFSET);
            }
        } else {
            if (segments.inSwapFile(dataSegmentId) == Boolean.FALSE) {
                try {
                    int index = OFFSET - segments.getLimit(textSegmentId);
                    return RAM.read(textSegmentId, index);
                } catch (IllegalArgumentException error) {
                    System.out.println(error.getMessage());
                }
            } else {
                swapToRam(dataSegmentId);
                read(PID, OFFSET);
            }
        }
        return 0;
    }

    /**
     * * Overwrites memory cell
     *
     * @param PID    segment's unique ID
     * @param OFFSET index to write
     * @param data   data to writewa
     * @throws IllegalArgumentException when calling data outside assigned block
     */
    public void write(int PID, int OFFSET, byte data) {
        int textSegmentId = processMap.get(PID)[0];
        int dataSegmentId = processMap.get(PID)[1];

        if (OFFSET <= segments.getLimit(textSegmentId)) {
            if (segments.inSwapFile(textSegmentId) == Boolean.FALSE) {
                try {
                    RAM.write(textSegmentId, OFFSET, data);
                } catch (IllegalArgumentException error) {
                    System.out.println(error.getMessage());
                }
            } else {
                swapToFile(textSegmentId);
                write(PID, OFFSET, data);
            }
        } else {
            throw new IllegalArgumentException("SEGMENTATION FAULT");
        }
    }

    /**
     * Returns specified process memory
     *
     * @param PID process unique id
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
     * Returns size of free memory
     *
     * @param virtual specifies if show swap or RAM left
     * @return int
     */
    public int showMemoryLeft(boolean virtual) {
        if (!virtual) {
            return RAM.showMemoryLeft();
        }
        return swapLeft;
    }

    /**
     * Moves segment from swap file to RAM
     */
    private void swapToRam(int ID) {
        int base = segments.getBase(ID);
        int limit = segments.getLimit(ID);
        byte[] data = new byte[limit];
        System.arraycopy(swapFile, base, data, 0, limit);
        try {
            RAM.write(data, ID);
        } catch (IllegalArgumentException error) {
            swapToFile(segmentQueue.peek());
            swapToRam(ID);
        }
        swapLeft += limit;
        writePointer -= limit;
        segments.swapToRam(ID);
    }

    /**
     * Moves segment from RAM to swap file
     */
    private void swapToFile(int ID) {
        try {
            byte[] data = readSegment(ID);
            swapLeft -= segments.getLimit(ID);
            writePointer += data.length;
            segments.swapToFile(ID);
        } catch (IllegalArgumentException error) {
            System.out.println(error.getMessage());
        }
    }

    /**
     * Loads segment to swap file
     */
    private void loadSegment(int size, byte[] code) {
        segments.addSegment(segmentCounter, writePointer, size);
        System.arraycopy(code, 0, swapFile, writePointer, size);
        swapLeft -= size;
        writePointer += size;
        segmentCounter++;
    }

    /**
     * Reads segment's data from RAM or swap file
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

    /**
     * Removes segment from swap file
     */
    private void wipeSegment(int base, int limit, int ID) {
        IntStream.range(base, limit).forEach(counter -> swapFile[counter] = 0);
        swapLeft += limit;
        writePointer -= limit;
        segments.flushSegment(ID);
    }
}




