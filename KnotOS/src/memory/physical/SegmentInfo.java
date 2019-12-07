package memory.physical;

public class SegmentInfo implements Comparable<SegmentInfo> {
    private int segmentID;
    private int startIndex;
    private int stopIndex;

    public SegmentInfo() {
    }

    public SegmentInfo(int segmentID, int startIndex, int stopIndex) {
        this.segmentID = segmentID;
        this.startIndex = startIndex;
        this.stopIndex = stopIndex;
    }

    public int getStopIndex() {
        return stopIndex;
    }

    public void setStopIndex(int stopIndex) {
        this.stopIndex = stopIndex;
    }

    public int getSegmentID() {
        return segmentID;
    }

    public void setSegmentID(int segmentID) {
        this.segmentID = segmentID;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    @Override
    public int compareTo(SegmentInfo segmentInfo) {
        return Integer.compare(this.startIndex, segmentInfo.startIndex);
    }
}
