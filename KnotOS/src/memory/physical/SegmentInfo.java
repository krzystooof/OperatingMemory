/**
 * <h1>KnotOS PhysicalMemory</h1>
 * @author Krzysztof Greczka
 * @since 12.2019
 * This code is a project for Operating Systems 2019 subject.
 * <p>
 *  Record of info needed to read and write data. Used in SegmentsTable class.
 */
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

    protected int getStopIndex() {
        return stopIndex;
    }

    protected void setStopIndex(int stopIndex) {
        this.stopIndex = stopIndex;
    }

    protected int getSegmentID() {
        return segmentID;
    }

    protected void setSegmentID(int segmentID) {
        this.segmentID = segmentID;
    }

    protected int getStartIndex() {
        return startIndex;
    }

    protected void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    /**
     * Created to allow sorting. Ascending sorting by startIndex (0-n)
     * @param segmentInfo other instance of same class
     * @return
     */
    @Override
    public int compareTo(SegmentInfo segmentInfo) {
        return Integer.compare(this.startIndex, segmentInfo.startIndex);
    }
}
