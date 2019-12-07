package memory.physical;

import java.util.ArrayList;
import java.util.Collections;

public class SegmentsTable {
    ArrayList<SegmentInfo> segmentsInfos;

    public SegmentsTable() {
        segmentsInfos = new ArrayList<>();
    }

    public void addSegment(int segmentID, int startByte, int stopByte) {
        segmentsInfos.add(new SegmentInfo(segmentID, startByte, stopByte));
    }

    public int[] getSegment(int id) {
        for (SegmentInfo segment : segmentsInfos) {
            if (segment.getSegmentID() == id) {
                int[] result = new int[]{segment.getStartByte(), segment.getStopByte()};
                return result;
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return segmentsInfos.isEmpty();
    }

    public int bestfit(int requestedSize) {
        Collections.sort(segmentsInfos);
        for (int i = 0; i < segmentsInfos.size() - 1; i++) {
            //if i+1-element's startByte minus i-element's stopByte less or equal to requestedSize return these bytes

        }
        return 0;
    }
}
