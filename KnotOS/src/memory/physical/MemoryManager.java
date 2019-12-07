package memory.physical;

public class MemoryManager {
    private SegmentsTable segmentsTable;
    private RAM ram;

    public MemoryManager() {
    }

    private void saveBytes(int segmentID, int startByte, int stopByte, byte[] data) {
        segmentsTable.addSegment(segmentID, startByte, stopByte);
        for (int i = startByte; i < stopByte; i++) {
            ram.saveByte(i, data[i]);
        }
    }

    public int write(byte[] data) {
        if (segmentsTable.isEmpty()) {
            saveBytes(0, 0, data.length, data);
            return 0;
        } else {
            //use bestfit method from segmentsTable and save data in returned bytes
        }
    }

    public byte read(int segment, int offset) {
        /*
        get segment addresses from segmentstable, check if offset not too big, if too big throw RuntimeException, return byte
         */
        return byte
    }

    public byte read(int segment, int startOffset, int stopOffset) {
        return new byte[];
    }

}
