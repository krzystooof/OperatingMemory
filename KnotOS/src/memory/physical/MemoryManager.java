package memory.physical;

public class MemoryManager {
    private SegmentsTable segmentsTable;
    private RAM ram;
    private int ramSize;

    public MemoryManager(int ramSize) {
        segmentsTable = new SegmentsTable(ramSize);
        ram = new RAM(ramSize);
    }

    private void saveBytes(int segmentID, int startByte, int stopByte, byte[] data) {
        segmentsTable.addSegment(segmentID, startByte, stopByte);
        for (int i = startByte; i < stopByte; i++) {
            ram.saveByte(i, data[i]);
        }
    }

    public int write(byte[] data) {
        int startIndex = segmentsTable.bestfit(data.length);
        int address = startIndex;
        if (address==-1)return -1;
        else {
            for (byte b:data){
                ram.saveByte(address,b);
                address++;
            }
            segmentsTable.addSegment(segmentsTable.getLastID()+1,startIndex,address-1);
            return segmentsTable.getLastID()+1;
        }
    }

    public byte read(int segment, int offset) {
        //get segment addresses from segmentstable, check if offset not too big, if too big throw RuntimeException, return byte


    }

    public byte read(int segment, int startOffset, int stopOffset) {
        return new byte[];
    }


}
