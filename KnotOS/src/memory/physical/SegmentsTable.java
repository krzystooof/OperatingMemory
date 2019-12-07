package memory.physical;

import java.util.ArrayList;
import java.util.Collections;

public class SegmentsTable {
    ArrayList<SegmentInfo> segmentsInfos;
    private int ramSize = 128;

    public SegmentsTable() {
        segmentsInfos = new ArrayList<>();
    }
    public SegmentsTable(int ramSize){
        this.ramSize = ramSize;
        segmentsInfos = new ArrayList<>();
    }

    public void addSegment(int segmentID, int startByte, int stopByte) {
        segmentsInfos.add(new SegmentInfo(segmentID, startByte, stopByte));
    }

    public int[] getSegment(int id) {
        for (SegmentInfo segment : segmentsInfos) {
            if (segment.getSegmentID() == id) {
                int[] result = new int[]{segment.getStartIndex(), segment.getStopIndex()};
                return result;
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return segmentsInfos.isEmpty();
    }

    public int bestfit(int requestedSize) {
        if(requestedSize>ramSize) return -1;
        else if(segmentsInfos.size()>1) {
            Collections.sort(segmentsInfos);
            boolean first = true;
            int startIndex = ramSize-1; //beggining of second segment
            int stopIndex = 0; //end of first segment
            for (int i = 0; i < segmentsInfos.size() - 1; i++) {
                int difference = segmentsInfos.get(i + 1).getStartIndex() - segmentsInfos.get(i).getStopIndex();
                if (difference >= requestedSize) {
                    if (first) {
                        first = false;
                        startIndex = segmentsInfos.get(i+1).getStartIndex();
                        stopIndex = segmentsInfos.get(i).getStopIndex();
                    }
                    else if (difference > startIndex - stopIndex) {
                        startIndex = segmentsInfos.get(i+1).getStartIndex();
                        stopIndex = segmentsInfos.get(i).getStopIndex();
                    }
                }
            }
            if(ramSize-1 - segmentsInfos.get(segmentsInfos.size()-1).getStopIndex()>startIndex - stopIndex)  stopIndex = segmentsInfos.get(segmentsInfos.size()-1).getStopIndex();
            return stopIndex+1;
        }
        else if(segmentsInfos.size()==0) return 0;
        else return segmentsInfos.get(0).getStopIndex()+1;
    }
}
