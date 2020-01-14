/**
 * <h1>KnotOS PhysicalMemory</h1>
 *
 * @author Krzysztof Greczka
 * @since 12.2019
 * This code is a project for Operating Systems 2019 subject.
 * <p>
 * PhysicalMemoryManager is main class for physical memory module. It is the one, which communicates with other modules.
 * Only PMM have public methods.
 */
package memory;

import java.util.*;

public class PhysicalMemoryManager {
    /**
     * PMM uses:
     *
     * @param ram as imitation of physical memory
     * @param segmentsTable to store info about written segments
     * @param ramSize to determine physical memory size
     */
    private ramSegments ramSegments;
    private RAM ram;
    private int ramSize = 128;

    /**
     * Initialize segmentsTable and ram with default (128) ram size and own segmentTable
     */
    public PhysicalMemoryManager() {
        ramSegments = new ramSegments();
        ram = new RAM(ramSize);
    }

    /**
     * Initialize segmentsTable and ram with given ram size and segmentTable
     */
    public PhysicalMemoryManager(int ramSize) {
        ramSegments = new ramSegments();
        this.ramSize = ramSize;
        ram = new RAM(ramSize);
    }

    public PhysicalMemoryManager(int ramSize, ramSegments ramSegments) {
        this.ramSegments = ramSegments;
        this.ramSize = ramSize;
        ram = new RAM(ramSize);
    }

    public void printInfo() {
        System.out.println("__________________________________________________");
        System.out.println("RAM:");
        int i = 0;
        for (byte b : read()) {
            System.out.print(i + ": " + b + "\t");
            i++;
        }
        System.out.println("\nSegments:");
        for (Segment b : ramSegments.segments) {
            System.out.print(b.ID + "->" + b.BASE + ":" + b.LIMIT + "\t");
            i++;
        }
        System.out.println("\n__________________________________________________");
    }

    /**
     * Write to ram
     *
     * @param data      table of bytes to be saved in memory
     * @param segmentID unique ID of segment
     * @return ID of segment storing data
     * @throws IllegalArgumentException RAM_OVERFLOW, when there is no enough space for data
     */
    public void write(byte[] data, int segmentID, int firstToMerge) {
        if (ramSegments.findSegment(segmentID)) throw new IllegalArgumentException("ID used before");
        else {
            int startIndex = bestfit(data.length);

            if (startIndex == -1) {
                if (checkAvailableSpace() < data.length) throw new IllegalArgumentException("RAM_OVERFLOW");
                else {
                    mergeSegment(firstToMerge);
                    write(data, segmentID, firstToMerge + 1);
                }
            } else {
                ram.saveByte(startIndex, data);
                ramSegments.addSegment(segmentID, startIndex, data.length);
            }
        }
    }

    /**
     * Write one byte to existing segment
     *
     * @param segmentID ID of wanted segment
     * @param offset    index in segment of wanted byte
     * @param data      byte to save
     */
    public void write(int segmentID, int offset, byte data) {
        int base = ramSegments.getSegment(segmentID).BASE;
        int limit = ramSegments.getSegment(segmentID).LIMIT;
        if (limit < offset) throw new IllegalArgumentException("SEGMENT_OVERFLOW");
        ram.saveByte(base + offset, data);
    }

    /**
     * Read whole ram
     *
     * @return ram in table of bytes
     */
    private byte[] read() {
        int base = 0;
        int limit = ramSize - 1;
        return ram.getByte(base, limit);
    }

    /**
     * Read a byte from ram
     *
     * @param segmentID ID of wanted segment
     * @param offset    index in segment of wanted byte
     * @return wanted byte
     */
    public byte read(int segmentID, int offset) {
        int base = ramSegments.getSegment(segmentID).BASE;
        int limit = ramSegments.getSegment(segmentID).LIMIT;
        if (offset >= limit || offset < 0) throw new IllegalArgumentException("SEGMENTATION ERROR");
        return ram.getByte(base + offset);
    }

    /**
     * Read whole segment from ram
     *
     * @param segmentID ID of wanted segment
     * @return whole wanted segment in table of bytes
     */
    public byte[] read(int segmentID) {
        int base = ramSegments.getSegment(segmentID).BASE;
        int offset = ramSegments.getSegment(segmentID).LIMIT + base - 1;
        return ram.getByte(base, offset);
    }

    /**
     * Read a part of segment from ram
     *
     * @param segmentID   ID of wanted segment
     * @param startOffset index of first wanted byte in segment
     * @param stopOffset  index of last wanted byte in segment
     * @return wanted part of segment in table of bytes
     */
    public byte[] read(int segmentID, int startOffset, int stopOffset) {
        int base = ramSegments.getSegment(segmentID).BASE;
        int limit = ramSegments.getSegment(segmentID).LIMIT;
        if (limit - base < stopOffset) throw new IllegalArgumentException("SEGMENT_OVERFLOW");
        limit = base + stopOffset;
        base += startOffset;
        return ram.getByte(base, limit);
    }

    public void remove(int segmentID) {
        ramSegments.delete(segmentID);
    }

    /**
     * Delete unused space between segments
     */
    public void mergeSegment(int segmentNumber) {
        ArrayList<Segment> segmentsInfos = ramSegments.segments;
        //move first to beginning of ram
        if (segmentNumber == 0) {
            Segment firstSegment = segmentsInfos.get(0);
            byte[] backup = ram.getByte(firstSegment.BASE, firstSegment.BASE + firstSegment.LIMIT - 1);
            ramSegments.setBase(firstSegment.ID, 0);
            ram.saveByte(0, backup);
        }
        //move others
        else {
            segmentNumber--;
            Collections.sort(segmentsInfos);
            int nextFreeByte = segmentsInfos.get(segmentNumber).BASE + segmentsInfos.get(segmentNumber).LIMIT;
            Segment nextSegment = segmentsInfos.get(segmentNumber + 1);
            byte[] backup = ram.getByte(nextSegment.BASE, nextSegment.BASE + nextSegment.LIMIT - 1);
            if (nextFreeByte + backup.length - 1 < ramSize) {
                ramSegments.setBase(nextSegment.ID, nextFreeByte);
                ram.saveByte(nextFreeByte, backup);
            }
        }
    }


    /**
     * Checks RAM available space. Determined by Segments Table
     *
     * @return available space
     */
    public int checkAvailableSpace() {
        ArrayList<Segment> segmentsInfos = ramSegments.segments;
        int free = ramSize;
        for (Segment s : segmentsInfos) {
            free -= s.LIMIT;
        }
        return free;
    }

    public HashMap<Integer, Integer> getFreeSpace() {
        Collections.sort(ramSegments.segments);
        HashMap<Integer, Integer> freeSpace = new HashMap<>(); //adress of first cell, available cells
        //space between 0 ram cell and first segment
        Segment firstSegment = ramSegments.segments.get(0);
        if (firstSegment.BASE != 0) {
            freeSpace.put(0, firstSegment.BASE);
        }
        //space between segments
        for (int i = 0; i < ramSegments.segments.size() - 1; i++) {
            Segment thisSegment = ramSegments.segments.get(i);
            Segment nextSegment = ramSegments.segments.get(i + 1);
            int endOfThisSegment = thisSegment.BASE + thisSegment.LIMIT - 1;
            if (endOfThisSegment + 1 != nextSegment.BASE) {
                //there are available cells between segments
                int spaceBetweenSegments = nextSegment.BASE - endOfThisSegment - 1;
                freeSpace.put(endOfThisSegment + 1, spaceBetweenSegments);
            }
        }
        //space between last segment last ram cell
        int endOfLastSegment = ramSegments.segments.get(ramSegments.segments.size() - 1).BASE + ramSegments.segments.get(ramSegments.segments.size() - 1).LIMIT - 1;
        int spaceAtEndofRam = ramSize - 1 - endOfLastSegment;
        freeSpace.put(endOfLastSegment + 1, spaceAtEndofRam);
        return freeSpace;
    }

    /**
     * Determine where to allocate new segment.
     * Bestfit chooses smallest available part of memory, that can store given data.
     *
     * @param requestedSize size of segment to write
     * @return -1 if there is no enough space, int startIndex - first index of new segment in memory
     */
    public int bestfit(int requestedSize) {
        //if return -1, no available space
        if (requestedSize > ramSize) return -1;
        else {
            if (ramSegments.segments.size() > 0) {
                HashMap<Integer, Integer> freeSpace = getFreeSpace();
                //delete entries that are smaller than required space
                freeSpace.entrySet().removeIf(entry -> entry.getValue() < requestedSize);
                if (freeSpace.isEmpty()) return -1;
                else
                    return Collections.min(freeSpace.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
            }
            //if no segments return 0
            else return 0;
        }
    }


}




