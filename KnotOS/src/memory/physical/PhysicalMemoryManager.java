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
     * @param ram as imitation of physical memory
     * @param segmentsTable to store info about written segments
     * @param ramSize to determine physical memory size
     */
    private SegmentTable segmentTable;
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
     * Initialize segmentsTable and ram with given  ram size and segmentTable
     */
    public PhysicalMemoryManager(int ramSize, SegmentTable segmentTable) {
        this.segmentTable = segmentTable;
        ram = new RAM(ramSize);
    }

    /**
     * Write to ram
     * @param data table of bytes to be saved in memory
     * @param segmentID unique ID of segment
     * @throws IllegalArgumentException RAM_OVERFLOW, when there is no enough space for data
     * @return ID of segment storing data
     */
    public int write(byte[] data, int segmentID) {
        int startIndex = bestfit(data.length);
        int address = startIndex;
        if (address == -1) {
            if (checkAvailableSpace() < data.length) throw new IllegalArgumentException("RAM_OVERFLOW");
            else {
                compacificate();
                return write(data,segmentID);
            }
        }
        else {
            for (byte b : data) {
                ram.saveByte(address, b);
                address++;
            }
            segmentTable.addSegment(segmentID, startIndex, address - 1);
            return segmentID;
        }
    }

    /**
     * Write one byte to existing segment
     * @param segmentID ID of wanted segment
     * @param offset index in segment of wanted byte
     * @param data byte to save
     */
    public void write(int segmentID, int offset, byte data) {
        int base = segmentTable.getSegment(segmentID).BASE;
        int limit = segmentTable.getSegment(segmentID).LIMIT;
        if (limit - base < offset) throw new IllegalArgumentException("SEGMENT_OVERFLOW");
        ram.saveByte(base + offset, data);
    }

    /**
     * Read whole ram
     * @return ram in table of bytes
     */
    public byte[] read() {
        int base = 0;
        int limit = ramSize-1;
        return ram.getByte(base,limit);
    }

    /**
     * Read a byte from ram
     * @param segmentID ID of wanted segment
     * @param offset index in segment of wanted byte
     * @return wanted byte
     */
    public byte read(int segmentID, int offset) {
        int base = segmentTable.getSegment(segmentID).BASE;
        int limit = segmentTable.getSegment(segmentID).LIMIT;
        if (limit - base < offset) throw new IllegalArgumentException("SEGMENT_OVERFLOW");
        return ram.getByte(base + offset);
    }

    /**
     * Read whole segment from ram
     * @param segmentID ID of wanted segment
     * @return whole wanted segment in table of bytes
     */
    public byte[] read(int segmentID) {
        int base = segmentTable.getSegment(segmentID).BASE;
        int limit = segmentTable.getSegment(segmentID).LIMIT;
        return ram.getByte(base, limit);
    }

    /**
     * Read a part of segment from ram
     * @param segmentID ID of wanted segment
     * @param startOffset index of first wanted byte in segment
     * @param stopOffset index of last wanted byte in segment
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
     * Delete info about given segment in SegmentTable
     * NOTE: ram cells of given segment are not changed
     * @param segmentID ID of wanted segment
     */
    public void wipe(int segmentID) {
        segmentTable.flushSegment(segmentID);
    }

    /**
     * Delete unused space between segments
     */
    private void compacificate(){
        for(int i=0;i<segmentTable.segments.size();i++){
            segmentTable.sort();
            if (segmentTable.inSwapFile.get(i)==false) {
                Segment segment = segmentTable.segments.get(i);
                int startByte = 0;
                if (i != 0) {
                    //find previous ram segment
                    boolean notFound = true;
                    int j=1;
                    while (notFound){
                        if(i-j<0){
                            notFound =false;
                            j=-1;
                        }
                        if(segmentTable.inSwapFile.get(i-j)==false) notFound=false;
                        j++;
                    }
                    if(j>-1) startByte = segmentTable.segments.get(i - j).BASE + 1;
                }
                byte[] data = ram.getByte(segment.BASE, segment.LIMIT);
                segmentTable.flushSegment(segment.ID);
                segmentTable.addSegment(segment.ID, startByte, startByte + data.length);
                ram.saveByte(startByte, data);
            }
        }
    }
    /**
     * Get only ram segments form segmentsTable
     * @return Array of only ram segments
     */
    public ArrayList<Segment> getRamSegments() {
        ArrayList<Segment> RAMsegments = new ArrayList<>();
        for (int i =0; i<segmentTable.segments.size();i++){
            if(segmentTable.inSwapFile.get(i)==false) RAMsegments.add(segmentTable.segments.get(i));
        }
        return RAMsegments;
    }

    /**
     * Checks RAM available space. Determined by Segments Table
     *
     * @return available space
     */
    public int checkAvailableSpace() {
        ArrayList<Segment> segmentsInfos = getRamSegments();
        if (segmentsInfos.size() > 1) {
            Collections.sort(segmentsInfos);
            int free = 0;
            //check beginning of ram
            int startOfFirstSegment = segmentsInfos.get(0).BASE;
            free += startOfFirstSegment;
            //check space between every two segments
            for (int i = 0; i < segmentsInfos.size() - 1; i++) {
                int difference = segmentsInfos.get(i + 1).BASE - 1 - segmentsInfos.get(i).LIMIT;
                free += difference;
            }
            //checking space after last segment
            int spaceAtEndofRam = ramSize - 1 - segmentsInfos.get(segmentsInfos.size() - 1).LIMIT;
            free += spaceAtEndofRam;
            return free;
        } else if (segmentsInfos.size() == 0) return ramSize;
            //if 1 segment
        else return ramSize - 1 - segmentsInfos.get(0).LIMIT;
    }


    /**
     * Determine where to allocate new segment.
     * Bestfit chooses smallest available part of memory, that can store given data.
     *
     * @param requestedSize size of segment to write
     * @return -1 if there is no enough space, int startIndex - first index of new segment in memory
     */
    public int bestfit(int requestedSize) {
        ArrayList<Segment> segmentsInfos = getRamSegments();
        if (requestedSize > ramSize) return -1;
        else if (segmentsInfos.size() > 1) {
            Collections.sort(segmentsInfos);
            boolean first = true;
            boolean found = false;
            int startIndex = ramSize; //beggining of second segment
            int stopIndex = 0; //end of first segment
            //check beginning of ram
            int startOfFirstSegment = segmentsInfos.get(0).BASE;
            if (startOfFirstSegment > requestedSize) {
                first = false;
                startIndex = startOfFirstSegment;
                stopIndex = -1;
            }
            //check space between every two segments
            for (int i = 0; i < segmentsInfos.size() - 1; i++) {
                int difference = segmentsInfos.get(i + 1).BASE - segmentsInfos.get(i).LIMIT;
                if (difference >= requestedSize) {
                    found = true;
                    if (first) {
                        first = false;
                        startIndex = segmentsInfos.get(i + 1).BASE;
                        stopIndex = segmentsInfos.get(i).LIMIT;
                    } else if (difference < startIndex - stopIndex) {
                        startIndex = segmentsInfos.get(i + 1).BASE;
                        stopIndex = segmentsInfos.get(i).LIMIT;
                    }
                }
                //if no available space found
                else if (i == segmentsInfos.size() - 2 && !found) stopIndex = -2;
            }
            //checking space after last segment
            int spaceAtEndofRam = ramSize - 1 - segmentsInfos.get(segmentsInfos.size() - 1).LIMIT;
            if (spaceAtEndofRam >= requestedSize && spaceAtEndofRam < startIndex - stopIndex)
                stopIndex = segmentsInfos.get(segmentsInfos.size() - 1).LIMIT;
            //returns startIndex for the new segment
            return stopIndex + 1;
        }
        //if no segments return 0
        else if (segmentsInfos.size() == 0) return 0;
            //if one segment return space after it
        else {
            int nextAvailable = segmentsInfos.get(0).LIMIT + 1;

            //if required bigger than available
            if (nextAvailable + requestedSize > ramSize) nextAvailable = -1;

            return nextAvailable;
        }
    }


}




