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
package memory.physical;

import memory.Segment;
import memory.SegmentTable;

import java.util.ArrayList;
import java.util.Collections;

public class PhysicalMemoryManager {
    /**
     * PMM uses:
     *
     * @param ram as imitation of physical memory
     * @param segmentsTable to store info about written segments
     * @param ramSize to determine physical memory size
     */
    public SegmentTable segmentTable;
    private RAM ram;
    private int ramSize = 128;

    /**
     * Initialize segmentsTable and ram with default (128) ram size and own segmentTable
     */
    public PhysicalMemoryManager() {
        segmentTable = new SegmentTable();
        ram = new RAM(ramSize);
    }

    /**
     * Initialize segmentsTable and ram with given ram size and segmentTable
     */
    public PhysicalMemoryManager(int ramSize, SegmentTable segmentTable) {
        this.segmentTable = segmentTable;
        this.ramSize = ramSize;
        ram = new RAM(ramSize);
    }

    /**
     * Write to ram
     *
     * @param data      table of bytes to be saved in memory
     * @param segmentID unique ID of segment
     * @return ID of segment storing data
     * @throws IllegalArgumentException RAM_OVERFLOW, when there is no enough space for data
     */
    public boolean write(byte[] data, int segmentID) {

        int startIndex = bestfit(data.length);
        int address = startIndex;
        if (address == -1) {
            if (checkAvailableSpace() < data.length) throw new IllegalArgumentException("RAM_OVERFLOW");
            else {
                mergeSegments();
                return write(data, segmentID);
            }
        } else {
            ram.saveByte(address, data);
            segmentTable.updateToRam(segmentID);
            segmentTable.setBase(segmentID, startIndex);
            return true;
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
        int base = segmentTable.getSegment(segmentID).BASE;
        int limit = segmentTable.getSegment(segmentID).LIMIT;
        if (limit - base < offset) throw new IllegalArgumentException("SEGMENT_OVERFLOW");
        ram.saveByte(base + offset, data);
    }

    /**
     * Read whole ram
     *
     * @return ram in table of bytes
     */
    public byte[] read() {
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
        int base = segmentTable.getSegment(segmentID).BASE;
        int limit = segmentTable.getSegment(segmentID).LIMIT;
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
        int base = segmentTable.getSegment(segmentID).BASE;
        int offset = segmentTable.getLimit(segmentID) + base -1;
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
        int base = segmentTable.getSegment(segmentID).BASE;
        int limit = segmentTable.getSegment(segmentID).LIMIT;
        if (limit - base < stopOffset) throw new IllegalArgumentException("SEGMENT_OVERFLOW");
        limit = base + stopOffset;
        base += startOffset;
        return ram.getByte(base, limit);
    }

    /**
     * Delete unused space between segments
     */
    private void mergeSegments() {
        ArrayList<Segment> segmentsInfos = getRamSegments();
        //move first to beginning of ram
        Segment firstSegment = segmentsInfos.get(0);
        segmentTable.setBase(firstSegment.ID, 0);
        //move others, no free space between
        for (int i = 0; i < segmentsInfos.size() - 1; i++) {
            Collections.sort(segmentsInfos);
            int nextFreeByte = segmentsInfos.get(i).BASE + segmentsInfos.get(i).LIMIT;
            Segment nextSegment = segmentsInfos.get(i + 1);
            byte[] backup = ram.getByte(nextSegment.BASE, nextSegment.BASE + nextSegment.LIMIT - 1);
            if (nextFreeByte + backup.length - 1 < ramSize) {
                segmentTable.setBase(nextSegment.ID, nextFreeByte);
                ram.saveByte(nextFreeByte, backup);
            }
        }

    }

    /**
     * Get only ram segments form segmentsTable
     *
     * @return Array of only ram segments
     */
    public ArrayList<Segment> getRamSegments() {
        ArrayList<Segment> RAMsegments = new ArrayList<>();
        segmentTable.inSwapFile.forEach((k, v) -> {
            if (v == false) RAMsegments.add(segmentTable.getSegment(k));
        });
        return RAMsegments;
    }

    /**
     * Checks RAM available space. Determined by Segments Table
     *
     * @return available space
     */
    public int checkAvailableSpace() {
        ArrayList<Segment> segmentsInfos = getRamSegments();
        int free = ramSize;
        for (Segment s : segmentsInfos) {
            free -= s.LIMIT;
        }
        return free;
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
        ArrayList<Segment> segmentsInfos = getRamSegments();
        if (requestedSize > ramSize) return -1;
        else {
            if (segmentsInfos.size() > 0) {
                Collections.sort(segmentsInfos);
                boolean first = true;
                boolean found = false;
                int startIndex = ramSize; //beginning of second segment
                int stopIndex = 0; //beginning of first segment
                //check beginning of ram
                int startOfFirstSegment = segmentsInfos.get(0).BASE;
                if (startOfFirstSegment >= requestedSize) {
                    first = false;
                    found = true;
                    startIndex = startOfFirstSegment;
                    stopIndex = -1;
                }
                //check space between every two segments
                for (int i = 0; i < segmentsInfos.size() - 1; i++) {
                    int difference = segmentsInfos.get(i + 1).BASE - (segmentsInfos.get(i).BASE + segmentsInfos.get(i).LIMIT);
                    if (difference >= requestedSize) {
                        found = true;
                        if (first) {
                            first = false;
                            startIndex = segmentsInfos.get(i + 1).BASE;
                            stopIndex = segmentsInfos.get(i).BASE + segmentsInfos.get(i).LIMIT - 1;
                        } else if (difference < startIndex - stopIndex) {
                            startIndex = segmentsInfos.get(i + 1).BASE;
                            stopIndex = segmentsInfos.get(i).BASE + segmentsInfos.get(i).LIMIT - 1;
                        }
                    }
                    //if no available space found
                    else if (i == segmentsInfos.size() - 2 && !found) stopIndex = -2;
                }
                //checking space after last segment
                int endOfLastSegment = segmentsInfos.get(segmentsInfos.size() - 1).BASE + segmentsInfos.get(segmentsInfos.size() - 1).LIMIT - 1;
                int spaceAtEndofRam = ramSize - 1 - endOfLastSegment;
                if (spaceAtEndofRam >= requestedSize && spaceAtEndofRam < startIndex - stopIndex)
                    stopIndex = endOfLastSegment;
                //returns startIndex for the new segment
                return stopIndex + 1;
            }
            //if no segments return 0
            else return 0;
        }
    }


}




