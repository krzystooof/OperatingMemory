package memory.virtual;

import java.util.HashMap;

import memory.SegmentTable;
import memory.Segment;
import memory.physical.PhysicalMemoryManager;


public class VirtualMemory {

    private HashMap<Integer, Integer[]> processMap = new HashMap<Integer, Integer[]>();
    private PhysicalMemoryManager RAM = new PhysicalMemoryManager(128);
    private SegmentTable segments = new SegmentTable();

    private int SWAP_SIZE = 1024;
    private int swapLeft = 1024;
    private Integer segmentCounter = 0;
    private byte[] swapFile = new byte[1024];


    /**
     * Allocates process in memory.
     *
     * @param assemblyCode Block of assembly instructions.
     */
    public void loadProcess(int pid, int textLength, int dataLength, byte[] assemblyCode) {

        if (dataLength > 0) {
            swapLeft -= textLength + dataLength; //
            if (swapLeft > 0) {
                Integer[] segs = new Integer[2];
                int start = SWAP_SIZE - swapLeft;
                // load segment begin
                segments.addSegment(segmentCounter, start, textLength);
                for (int counter = start; counter < textLength; counter++) {
                    swapFile[counter] = assemblyCode[counter];
                }
                segs[0] = segmentCounter;
                segmentCounter++;
                start += textLength;
                // load segment end
                segments.addSegment(segmentCounter, start, dataLength);
                for (int counter = start; counter < dataLength; counter++) {
                    swapFile[counter] = assemblyCode[counter];
                }
                segs[1] = segmentCounter;
                segmentCounter++;

                processMap.put(pid, segs);
            } else {
                // brak pamieci
            }
        } else {
            swapLeft -= textLength; //
            if (swapLeft > 0) {
                Integer[] segs = new Integer[1];
                int start = SWAP_SIZE - swapLeft;
                // load segment begin
                segments.addSegment(segmentCounter, start, textLength);
                for (int counter = start; counter < textLength; counter++) {
                    swapFile[counter] = assemblyCode[counter];
                }
                segs[0] = segmentCounter;
                segmentCounter++;

                // load segment end
                processMap.put(pid, segs);
            } else {
                // brak pamieci
            }
        }

    }

    /**
     * Removes program from memory.
     */
    public void flushProcess(int pid) {
        Integer[] toFlush = processMap.get(pid);
        if (toFlush.length > 1) {
            if (segments.inSwap(toFlush[0])) {
                int base = segments.getData(toFlush[0])[0];
                int limit = segments.getData(toFlush[0])[1];
                for (int counter = base; counter < limit; counter++) {
                    swapFile[counter] = 0;
                }
                swapLeft += limit;
            } else {
                RAM.wipe(toFlush[0]);
            }
            segments.flushSegment(toFlush[0]);
            segmentCounter--;
            if (segments.inSwap(toFlush[1])) {
                int base = segments.getData(toFlush[1])[0];
                int limit = segments.getData(toFlush[1])[1];
                for (int counter = base; counter < limit; counter++) {
                    swapFile[counter] = 0;
                }
                swapLeft += limit;
            } else {
                RAM.wipe(toFlush[1]);
            }
            segments.flushSegment(toFlush[1]);
            segmentCounter--;
        } else {
            if (segments.inSwap(toFlush[0])) {
                int base = segments.getData(toFlush[0])[0];
                int limit = segments.getData(toFlush[0])[1];
                for (int counter = base; counter < limit; counter++) {
                    swapFile[counter] = 0;
                }
                swapLeft += limit;
            } else {
                RAM.wipe(toFlush[0]);
            }
            segments.flushSegment(toFlush[0]);
            segmentCounter--;
        }
    }

    /**
     * Reads memory cell.
     *
     * @return byte from RAM.
     */
    public byte read(int PID, int OFFSET) {
        Integer[] segs = processMap.get(PID);
        if (segs.length > 1) {
            if (OFFSET > segments.getLen(segs[0])) {
                if (segments.inSwap(segs[1]) == Boolean.FALSE) {
                    int[] data = segments.getData(segs[1]);
                    if (OFFSET < data[1]) {
                        return RAM.read(segs[1], OFFSET - segments.getLen(segs[0]));
                    } else {
                        // seg err
                    }
                } else {
                    // swap to ram
                    // read again
                }
            } else {
                if (segments.inSwap(segs[0]) == Boolean.FALSE) {
                    int[] data = segments.getData(segs[0]);
                    if (OFFSET < data[1]) {
                        return RAM.read(segs[0], OFFSET);
                    } else {
                        // seg err
                    }
                } else {
                    // swap to ram
                    // read again
                }
            }

        } else {
            if (OFFSET > segments.getLen(segs[0])) {

                if (segments.inSwap(segs[1]) == Boolean.FALSE) {
                    int[] data = segments.getData(segs[1]);
                    if (OFFSET < data[1]) {
                        return RAM.read(segs[1], OFFSET - segments.getLen(segs[0]));
                    } else {
                        // seg err
                    }
                } else {
                    // swap to ram
                    // read again
                }
            } else {
                if (segments.inSwap(segs[0]) == Boolean.FALSE) {
                    int[] data = segments.getData(segs[0]);
                    if (OFFSET < data[1]) {
                        return RAM.read(segs[0], OFFSET);
                    } else {
                        // seg err
                    }
                } else {
                    // swap to ram
                    // read again
                }
            }
        }
        return 0;
    }



    /**
     * Overwrites memory cell.
     */
    public void write(int PID, int OFFSET, byte data) {
        Integer[] segs = processMap.get(PID);
        if (segs.length > 1) {
            if (OFFSET > segments.getLen(segs[0])) {
                if (segments.inSwap(segs[1]) == Boolean.FALSE) {
                    int[] Data = segments.getData(segs[1]);
                    if (OFFSET < Data[1]) {
                        RAM.write(segs[1], OFFSET - segments.getLen(segs[0]), data);
                    } else {
                        // seg err
                    }
                } else {
                    // swap to ram
                    // read again
                }
            } else {
                if (segments.inSwap(segs[0]) == Boolean.FALSE) {
                    int[] Data = segments.getData(segs[1]);
                    if (OFFSET < Data[1]) {
                        RAM.write(segs[0], OFFSET, data);
                    } else {
                        // seg err
                    }
                } else {
                    // swap to ram
                    // read again
                }
            }

        } else {
            if (OFFSET > segments.getLen(segs[0])) {

                if (segments.inSwap(segs[1]) == Boolean.FALSE) {
                    int[] Data = segments.getData(segs[1]);
                    if (OFFSET < Data[1]) {
                        RAM.write(segs[1], OFFSET - segments.getLen(segs[0]), data);
                    } else {
                        // seg err
                    }
                } else {
                    // swap to ram
                    // read again
                }
            } else {
                if (segments.inSwap(segs[0]) == Boolean.FALSE) {
                    int[] Data = segments.getData(segs[0]);
                    if (OFFSET < Data[1]) {
                        RAM.write(segs[0], OFFSET, data);
                    } else {
                        // seg err
                    }
                } else {
                    // swap to ram
                    // read again
                }
            }
        }
    }

    /**
     * Swaps segment from swap file to RAM.
     */
    private void swapToRam(int ID) {
        Segment seg = segments.get(ID);
        byte[] segData = new byte[seg.limit];
        int dataCounter = 0;
        for (int counter = seg.base; counter < seg.limit; counter ++){
            segData[dataCounter] = swapFile[counter];
            dataCounter++;
        }
        RAM.write(segData);
        swapLeft += seg.limit;
    }

    /**
     * Swaps segment from RAM to swap file.
     */
    private void swapToFile(int ID) {
        byte[] seg = RAM.read(ID);
        swapLeft -= segments.getLen(ID);
        int dataCounter = 0;
        for (int counter = SWAP_SIZE - swapLeft; counter < segments.getLen(ID); counter ++){
            swapFile[counter] = seg[dataCounter];
            dataCounter++;
        }
    }
}




