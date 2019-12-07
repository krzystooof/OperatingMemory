package memory.physical;

public class SegmentInfo implements Comparable<SegmentInfo> {
    private int segmentID;
    private int startByte;
    private int stopByte;

    public SegmentInfo() {
    }

    public SegmentInfo(int segmentID, int startByte, int stopByte) {
        this.segmentID = segmentID;
        this.startByte = stopByte;
        this.stopByte = stopByte;
    }

    public int getStopByte() {
        return stopByte;
    }

    public void setStopByte(int stopByte) {
        this.stopByte = stopByte;
    }

    public int getSegmentID() {
        return segmentID;
    }

    public void setSegmentID(int segmentID) {
        this.segmentID = segmentID;
    }

    public int getStartByte() {
        return startByte;
    }

    public void setStartByte(int startByte) {
        this.startByte = startByte;
    }

    @Override
    public int compareTo(SegmentInfo segmentInfo) {
        return Integer.compare(this.startByte, segmentInfo.startByte);
    }
}
