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
     * Initialize segmentsTable and ram with default (128) ram size
     */
    public PhysicalMemoryManager() {
        segmentTable = new SegmentTable(ramSize);
        ram = new RAM(ramSize);
    }

    /**
     * Initialize segmentsTable and ram with given  ram size
     */
    public PhysicalMemoryManager(int ramSize) {
        segmentTable = new SegmentTable(ramSize);
        ram = new RAM(ramSize);
    }

    /**
     * Write to ram
     * @param data table of bytes to be saved in memory
     * @throws IllegalArgumentException RAM_OVERFLOW, when there is no enough space for data
     * @return ID of segment storing data
     */
    public int write(byte[] data) {
        int startIndex = segmentTable.bestfit(data.length);
        int address = startIndex;
        write:
        if (address == -1) {
            if (segmentTable.checkAvailableSpace() < data.length) throw new IllegalArgumentException("RAM_OVERFLOW");
            else {
                compacificate();
                return write(data);
            }
        }
        else {
            for (byte b : data) {
                ram.saveByte(address, b);
                address++;
            }
            int segmentID = segmentTable.getLastID() + 1;
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
        int[] address = segmentTable.getSegment(segmentID);
        if (address[0] == 0 && address[1] == 0) throw new IllegalArgumentException("NOT_EXISTING_SEGMENT");
        if (address[1] - address[0] < offset) throw new IllegalArgumentException("SEGMENT_OVERFLOW");
        ram.saveByte(address[0] + offset, data);
    }

    /**
     * Read a byte from ram
     * @param segmentID ID of wanted segment
     * @param offset index in segment of wanted byte
     * @return wanted byte
     */
    public byte read(int segmentID, int offset) {
        int[] address = segmentTable.getSegment(segmentID);
        if (address[0] == 0 && address[1] == 0) throw new IllegalArgumentException("NOT_EXISTING_SEGMENT");
        if (address[1] - address[0] < offset) throw new IllegalArgumentException("SEGMENT_OVERFLOW");
        return ram.getByte(address[0] + offset);
    }

    /**
     * Read whole segment from ram
     * @param segmentID ID of wanted segment
     * @return whole wanted segment in table of bytes
     */
    public byte[] read(int segmentID) {
        int[] address = segmentTable.getSegment(segmentID);
        if (address[0] == 0 && address[1] == 0) throw new IllegalArgumentException("NOT_EXISTING_SEGMENT");
        return ram.getBytes(address[0], address[1]);
    }

    /**
     * Read a part of segment from ram
     * @param segmentID ID of wanted segment
     * @param startOffset index of first wanted byte in segment
     * @param stopOffset index of last wanted byte in segment
     * @return wanted part of segment in table of bytes
     */
    public byte[] read(int segmentID, int startOffset, int stopOffset) {
        int[] address = segmentTable.getSegment(segmentID);
        if (address[0] == 0 && address[1] == 0) throw new IllegalArgumentException("NOT_EXISTING_SEGMENT");
        if (address[1] - address[0] < stopOffset) throw new IllegalArgumentException("SEGMENT_OVERFLOW");
        address[1] = address[0] + stopOffset;
        address[0] += startOffset;
        return ram.getBytes(address[0], address[1]);
    }

    /**
     * Delete info about given segment in SegmentTable
     * NOTE: ram cells of given segment are not changed
     * @param segmentID ID of wanted segment
     */
    public void wipe(int segmentID) {
        segmentTable.deleteEntry(segmentID);
    }

    /**
     * Delete unused space between segments
     */
    private void compacificate(){
        for(int i=0;i<segmentTable.segmentsInfos.size();i++){
            Collections.sort(segmentTable.segmentsInfos);
            Segment segment = segmentTable.segmentsInfos.get(i);
            int startByte=0;
            if(i!=0) {
                startByte=segmentTable.segmentsInfos.get(i-1).getBase()+1;
            }
            byte[] data = ram.getBytes(segment.getBase(),segment.getLimit());
            segmentTable.deleteEntry(segment.getId());
            segmentTable.addSegment(segment.getId(),startByte,startByte+data.length);
            ram.saveByte(startByte,data);
        }
    }


}
