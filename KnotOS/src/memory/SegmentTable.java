/**
 * <h1>KnotOS PhysicalMemory</h1>
 * @author Krzysztof Greczka
 * @since 12.2019
 * This code is a project for Operating Systems 2019 subject.
 * <p>
 *  Table of segments' info. Used to determine where to write and protect memory
 */
package memory;

import java.util.ArrayList;
import java.util.Collections;

public class SegmentTable {
    public ArrayList<Segment> segmentsInfos;
    private int ramSize = 128;

    public SegmentTable() {
        segmentsInfos = new ArrayList<>();
    }

    /**
     * Initialize with given ramSize
     *
     * @param ramSize size of memory
     */
    public SegmentTable(int ramSize) {
        this.ramSize = ramSize;
        segmentsInfos = new ArrayList<>();
    }

    /**
     * Add segment to table
     *
     * @param segmentID ID of given segment
     * @param startByte index of first segment byte in memory
     * @param stopByte  index of last segment byte in memory
     */
    public void addSegment(int segmentID, int startByte, int stopByte) {
        segmentsInfos.add(new Segment(segmentID, startByte, stopByte));
    }

    public int getLimit(int id) {
        return getSegment(id)[0];
    }

    /**
     * Get segment first and last index in memory
     *
     * @param segmentID ID of wanted segment
     * @return int table where first is startIndex, second - stopIndex of wanted segment
     */
    public int[] getSegment(int segmentID) {
        for (Segment segment : segmentsInfos) {
            if (segment.getId() == segmentID) {
                int[] result = new int[]{segment.getBase(), segment.getLimit()};
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
     *
     * @return int ID of segment
     */
    public int getLastID() {
        int lastID = 0;
        for (Segment segment : segmentsInfos) {
            if (segment.getId() > lastID) lastID = segment.getId();
        }
        return lastID;
    }

    /**
     * Determine where to allocate new segment.
     * Bestfit chooses smallest available part of memory, that can store given data.
     *
     * @param requestedSize size of segment to write
     * @return -1 if there is no enough space, int startIndex - first index of new segment in memory
     */
    public int bestfit(int requestedSize) {
        if (requestedSize > ramSize) return -1;
        else if (segmentsInfos.size() > 1) {
            Collections.sort(segmentsInfos);
            boolean first = true;
            boolean found = false;
            int startIndex = ramSize; //beggining of second segment
            int stopIndex = 0; //end of first segment
            //check beginning of ram
            int startOfFirstSegment = segmentsInfos.get(0).getBase();
            if (startOfFirstSegment > requestedSize) {
                first = false;
                startIndex = startOfFirstSegment;
                stopIndex = -1;
            }
            //check space between every two segments
            for (int i = 0; i < segmentsInfos.size() - 1; i++) {
                int difference = segmentsInfos.get(i + 1).getBase() - segmentsInfos.get(i).getLimit();
                if (difference >= requestedSize) {
                    found = true;
                    if (first) {
                        first = false;
                        startIndex = segmentsInfos.get(i + 1).getBase();
                        stopIndex = segmentsInfos.get(i).getLimit();
                    } else if (difference < startIndex - stopIndex) {
                        startIndex = segmentsInfos.get(i + 1).getBase();
                        stopIndex = segmentsInfos.get(i).getLimit();
                    }
                }
                //if no available space found
                else if (i == segmentsInfos.size() - 2 && !found) stopIndex = -2;
            }
            //checking space after last segment
            int spaceAtEndofRam = ramSize - 1 - segmentsInfos.get(segmentsInfos.size() - 1).getLimit();
            if (spaceAtEndofRam >= requestedSize && spaceAtEndofRam < startIndex - stopIndex)
                stopIndex = segmentsInfos.get(segmentsInfos.size() - 1).getLimit();
            //returns startIndex for the new segment
            return stopIndex + 1;
        }
        //if no segments return 0
        else if (segmentsInfos.size() == 0) return 0;
            //if one segment return space after it
        else {
            int nextAvailable = segmentsInfos.get(0).getLimit() + 1;

            //if required bigger than available
            if (nextAvailable + requestedSize > ramSize) nextAvailable = -1;

            return nextAvailable;
        }
    }

    /**
     * Remove info about given segment
     *
     * @param segmentID ID of given segment
     */
    public void deleteEntry(int segmentID) {
        for (int i = 0; i < segmentsInfos.size(); i++) {
            if (segmentsInfos.get(i).getId() == segmentID) {
                segmentsInfos.remove(i);
            }
        }
    }

    /**
     * Checks available space. Determined by Segments Table
     *
     * @return available space
     */
    public int checkAvailableSpace() {
        if (segmentsInfos.size() > 1) {
            Collections.sort(segmentsInfos);
            int free = 0;
            //check beginning of ram
            int startOfFirstSegment = segmentsInfos.get(0).getBase();
            free += startOfFirstSegment;
            //check space between every two segments
            for (int i = 0; i < segmentsInfos.size() - 1; i++) {
                int difference = segmentsInfos.get(i + 1).getBase() - 1 - segmentsInfos.get(i).getLimit();
                free += difference;
            }
            //checking space after last segment
            int spaceAtEndofRam = ramSize - 1 - segmentsInfos.get(segmentsInfos.size() - 1).getLimit();
            free += spaceAtEndofRam;
            return free;
        } else if (segmentsInfos.size() == 0) return ramSize;
            //if 1 segment
        else return ramSize - 1 - segmentsInfos.get(0).getLimit();
    }

}



