/**
 * <h1>KnotOS PhysicalMemory</h1>
 * @author Krzysztof Greczka
 * @since 12.2019
 * This code is a project for Operating Systems 2019 subject.
 * <p>
 *  Table of segments' info. Used to determine where to write and protect memory
 */
package memory.physical;

import java.util.ArrayList;
import java.util.Collections;

public class SegmentsTable {
    ArrayList<SegmentInfo> segmentsInfos;
    private int ramSize = 128;

    public SegmentsTable() {
        segmentsInfos = new ArrayList<>();
    }

    /**
     * Initialize with given ramSize
     * @param ramSize size of memory
     */
    public SegmentsTable(int ramSize) {
        this.ramSize = ramSize;
        segmentsInfos = new ArrayList<>();
    }

    /**
     * Add segment to table
     * @param segmentID ID of given segment
     * @param startByte index of first segment byte in memory
     * @param stopByte index of last segment byte in memory
     */
    protected void addSegment(int segmentID, int startByte, int stopByte) {
        segmentsInfos.add(new SegmentInfo(segmentID, startByte, stopByte));
    }

    /**
     * Get segment first and last index in memory
     * @param segmentID ID of wanted segment
     * @return int table where first is startIndex, second - stopIndex of wanted segment
     */
    protected int[] getSegment(int segmentID) {
        for (SegmentInfo segment : segmentsInfos) {
            if (segment.getSegmentID() == segmentID) {
                int[] result = new int[]{segment.getStartIndex(), segment.getStopIndex()};
                return result;
            }
        }
        return new int[]{0, 0};
    }

    protected boolean isEmpty() {
        return segmentsInfos.isEmpty();
    }

    /**
     * Get ID of segment with highest ID
     * @return int ID of segment
     */
    protected int getLastID() {
        int lastID = 0;
        for (SegmentInfo segment : segmentsInfos) {
            if (segment.getSegmentID() > lastID) lastID = segment.getSegmentID();
        }
        return lastID;
    }

    /**
     * Determine where to allocate new segment.
     * Bestfit chooses smallest available part of memory, that can store given data.
     * @param requestedSize size of segment to write
     * @return -1 if there is no enough space, int startIndex - first index of new segment in memory
     */
    protected int bestfit(int requestedSize) {
        if (requestedSize > ramSize) return -1;
        else if (segmentsInfos.size() > 1) {
            Collections.sort(segmentsInfos);
            boolean first = true;
            boolean found = false;
            int startIndex = ramSize; //beggining of second segment
            int stopIndex = 0; //end of first segment
            //check beginning of ram
            int startOfFirstSegment = segmentsInfos.get(0).getStartIndex();
            if (startOfFirstSegment > requestedSize) {
                first = false;
                startIndex = startOfFirstSegment;
                stopIndex = -1;
            }
            //check space between every two segments
            for (int i = 0; i < segmentsInfos.size() - 1; i++) {
                int difference = segmentsInfos.get(i + 1).getStartIndex() - segmentsInfos.get(i).getStopIndex();
                if (difference >= requestedSize) {
                    found = true;
                    if (first) {
                        first = false;
                        startIndex = segmentsInfos.get(i + 1).getStartIndex();
                        stopIndex = segmentsInfos.get(i).getStopIndex();
                    } else if (difference < startIndex - stopIndex) {
                        startIndex = segmentsInfos.get(i + 1).getStartIndex();
                        stopIndex = segmentsInfos.get(i).getStopIndex();
                    }
                }
                //if no available space found
                else if (i == segmentsInfos.size() - 2 && !found) stopIndex = -2;
            }
            //checking space after last segment
            int spaceAtEndofRam = ramSize - 1 - segmentsInfos.get(segmentsInfos.size() - 1).getStopIndex();
            if (spaceAtEndofRam >= requestedSize && spaceAtEndofRam < startIndex - stopIndex)
                stopIndex = segmentsInfos.get(segmentsInfos.size() - 1).getStopIndex();
            //returns startIndex for the new segment
            return stopIndex + 1;
        }
        //if no segments return 0
        else if (segmentsInfos.size() == 0) return 0;
            //if one segment return space after it
        else {
            int nextAvailable = segmentsInfos.get(0).getStopIndex() + 1;

            //if required bigger than available
            if (nextAvailable + requestedSize > ramSize) nextAvailable = -1;

            return nextAvailable;
        }
    }

    /**
     * Remove info about given segment
     * @param segmentID ID of given segment
     */
    protected void deleteEntry(int segmentID) {
        for (int i = 0; i < segmentsInfos.size(); i++) {
            if (segmentsInfos.get(i).getSegmentID() == segmentID) {
                segmentsInfos.remove(i);
            }
        }
    }
}
