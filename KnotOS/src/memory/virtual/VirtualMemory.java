package memory.virtual;

import java.util.HashMap;

import memory.SegmentTable;
import memory.Segment;
import memory.physical.PhysicalMemoryManager;


public class VirtualMemory {
    private HashMap<Integer, Integer[]> processMap = new HashMap<Integer, Integer[]>();
    private PhysicalMemoryManager RAM = new PhysicalMemoryManager(128);
    private SegmentTable segments = new SegmentTable();

    private Integer segmentCounter = 0;
    private byte[] swapFile = new byte[1024];
    private int SWAP_SIZE = 1024;
    private int swapLeft = SWAP_SIZE;
    private int writePointer = 0;

    // TODO: FIFO queue


    /**
     * Allocates process in swap file.
     *
     * @param assemblyCode Block of assembly instructions.
     * @param PID          process unique ID
     * @param textSize     text section size
     * @param dataSize     data section size
     */
    public void loadProcess(int PID, int textSize, int dataSize, byte[] assemblyCode) {

        writePointer = SWAP_SIZE - swapLeft;

        if (swapLeft < textSize) {
            loadSegment(textSize, assemblyCode);
            processMap.put(PID, new Integer[]{segmentCounter, -1});
        } else {
            throw new IllegalArgumentException("OUT_OF_MEMORY");
        }

        if (dataSize > 0) {
            if (swapLeft < dataSize) {
                loadSegment(textSize, assemblyCode);
                loadSegment(dataSize, assemblyCode);
                processMap.put(PID, new Integer[]{segmentCounter, segmentCounter - 1});
            } else {
                throw new IllegalArgumentException("OUT_OF_MEMORY");
            }
        }

    }

    /**
     * Removes program from memory.
     *
     * @param PID process unique ID
     */
    public void flushProcess(int PID) {

        int textSegment = processMap.get(PID)[0];
        int dataSegment = processMap.get(PID)[1];

        if (segments.inSwapFile(textSegment)) {
            int base = segments.getSegmentData(textSegment)[0];
            int limit = segments.getSegmentData(textSegment)[1];
            wipeSegment(base, limit, textSegment);
        } else {
            RAM.wipe(textSegment);
        }

        if (dataSegment > 0) {
            if (segments.inSwapFile(dataSegment)) {
                int base = segments.getSegmentData(dataSegment)[0];
                int limit = segments.getSegmentData(dataSegment)[1];
                wipeSegment(base, limit, dataSegment);
            } else {
                RAM.wipe(dataSegment);
            }
        }
    }

    /**
     * Reads memory cell.
     *
     * @return byte from RAM.
     */
    public byte read(int PID, int OFFSET) {
        int textSegmentId = processMap.get(PID)[0];
        int dataSegmentId = processMap.get(PID)[1];

        if (OFFSET <= segments.getLen(textSegmentId)) {
            if (segments.inSwapFile(textSegmentId) == Boolean.FALSE) {
                try {
                    return RAM.read(textSegmentId, OFFSET);
                } catch (IllegalArgumentException error) {
                    // Handle not existing segment
                }
            } else {
                swapToRam(textSegmentId);
                read(PID, OFFSET);
            }
        } else {
            if (segments.inSwapFile(dataSegmentId) == Boolean.FALSE) {
                try {
                    return RAM.read(textSegmentId, OFFSET - segments.getLen(textSegmentId));
                } catch (IllegalArgumentException error) {
                    // Handle not existing segment
                }
            } else {
                swapToRam(dataSegmentId);
                read(PID, OFFSET);
            }
        }
        return 0;
    }


    /**
     * Overwrites memory cell.
     */
    public void write(int PID, int OFFSET, byte data) {
        int textSegmentId = processMap.get(PID)[0];
        int dataSegmentId = processMap.get(PID)[1];

        if (OFFSET <= segments.getLen(textSegmentId)) {
            if (segments.inSwapFile(textSegmentId) == Boolean.FALSE) {
                try {
                    RAM.write(textSegmentId, OFFSET, data);
                } catch (IllegalArgumentException error) {
                    // Handle not existing segment
                }
            } else {
                swapToFile(textSegmentId);
                write(PID, OFFSET, data);
            }
        } else {
            if (segments.inSwapFile(dataSegmentId) == Boolean.FALSE) {
                try {
                    RAM.write(textSegmentId, OFFSET, data);
                } catch (IllegalArgumentException error) {
                    // Handle not existing segment
                }
            } else {
                swapToFile(dataSegmentId);
                write(PID, OFFSET, data);
            }
        }
    }

    /**
     * Swaps segment from swap file to RAM.
     */
    private void swapToRam(int ID) {
        int base = segments.getSegmentData(ID)[0];
        int limit = segments.getSegmentData(ID)[1];
        byte[] data = new byte[limit];
        int dataCounter = 0;
        for (int counter = base; counter < limit; counter++) {
            data[dataCounter] = swapFile[counter];
            dataCounter++;
        }
        try {
            RAM.write(data);
        } catch (IllegalArgumentException error) {
            // Handle not existing segment
        }
        swapLeft += limit;
        segments.swapToRam(ID);
    }

    /**
     * Swaps segment from RAM to swap file.
     */
    private void swapToFile(int ID) {
        try {
            byte[] data = RAM.read(ID);
            int dataCounter = 0;
            for (int counter = SWAP_SIZE - swapLeft; counter < segments.getLen(ID); counter++) {
                swapFile[counter] = data[dataCounter];
                dataCounter++;
            }
            swapLeft -= segments.getLen(ID);
            segments.swapToFile(ID);
        } catch (IllegalArgumentException error) {
            // Handle not existing segment
        }
    }

    private void loadSegment(int size, byte[] code) {
        segments.addSegment(segmentCounter, writePointer, size);
        if (size - writePointer >= 0) {
            System.arraycopy(code, writePointer, swapFile, writePointer, size - writePointer);
        }
        swapLeft -= size;
        writePointer += size;
        segmentCounter++;
    }


    private void wipeSegment(int base, int limit, int ID) {
        for (int counter = base; counter < limit; counter++) {
            swapFile[counter] = 0;
        }
        swapLeft += limit;
        segments.flushSegment(ID);
    }
}




