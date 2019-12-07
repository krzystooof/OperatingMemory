package memory.physical;

public class MemoryManager {
    private SegmentsTable segmentsTable;
    private RAM ram;
    private int ramSize = 128;
    public MemoryManager(){
        segmentsTable = new SegmentsTable(ramSize);
        ram = new RAM(ramSize);
    }

    public MemoryManager(int ramSize) {
        segmentsTable = new SegmentsTable(ramSize);
        ram = new RAM(ramSize);
    }

    public int write(byte[] data) {
        int startIndex = segmentsTable.bestfit(data.length);
        int address = startIndex;
        if (address==-1)throw new IllegalArgumentException("RAM_OVERFLOW");
        else {
            for (byte b:data){
                ram.saveByte(address,b);
                address++;
            }
            int segmentID = segmentsTable.getLastID()+1;
            segmentsTable.addSegment(segmentID,startIndex,address-1);
            return segmentID;
        }
    }

    public byte read(int segment, int offset) {
        int[] address = segmentsTable.getSegment(segment);
        if(address[0]==0&&address[1]==0) throw new IllegalArgumentException("WRONG_SEGMENT");
        if(address[1]-address[0]<offset) throw new IllegalArgumentException("SEGMENT_OVERFLOW");
        return ram.getByte(address[0]+offset);
    }
    public byte[] read(int segment) {
        int[] address = segmentsTable.getSegment(segment);
        if(address[0]==0&&address[1]==0) throw new IllegalArgumentException("WRONG_SEGMENT");
        byte[] result = new byte[address[1]-address[0]];
        for (int i = 0; i<result.length;i++){
            result[i] = ram.getByte(address[0]);
            address[0]++;
        }
        return result;
    }
    public byte[] read(int segment,int startOffset, int stopOffset) {
        int[] address = segmentsTable.getSegment(segment);
        if(address[0]==0&&address[1]==0) throw new IllegalArgumentException("WRONG_SEGMENT");
        if(address[1]-address[0]<stopOffset) throw new IllegalArgumentException("SEGMENT_OVERFLOW");
        address[1]=stopOffset+address[0];
        address[0]+=startOffset;
        byte[] result = new byte[address[1]-address[0]];
        for (int i = 0; i<result.length;i++){
            result[i] = ram.getByte(address[0]);
            address[0]++;
        }
        return result;
    }
    public void wipe(int segmentID){
        segmentsTable.deleteEntry(segmentID);
    }













}
