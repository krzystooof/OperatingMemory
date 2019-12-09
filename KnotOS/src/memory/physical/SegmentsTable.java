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

    public int[] getSegment(int segmentID) {
        for (SegmentInfo segment : segmentsInfos) {
            if (segment.getSegmentID() == segmentID) {
                int[] result = new int[]{segment.getStartIndex(), segment.getStopIndex()};
                return result;
            }
        }
        return new int[]{0,0};
    }

    public boolean isEmpty() {
        return segmentsInfos.isEmpty();
    }
    public int getLastID(){
        int lastID=0;
        for(SegmentInfo segment: segmentsInfos){
            if(segment.getSegmentID()>lastID)lastID=segment.getSegmentID();
        }
        return lastID;
    }

    public int bestfit(int requestedSize) {
        if(requestedSize>ramSize) return -1;
        else if(segmentsInfos.size()>1) {
            Collections.sort(segmentsInfos);
            boolean first = true;
            boolean found = false;
            int startIndex = 0; //beggining of second segment
            int stopIndex = 0; //end of first segment
            //check beginning of ram
            int startOfFirstSegment = segmentsInfos.get(0).getStartIndex();
            if(startOfFirstSegment>requestedSize) {
                first=false;
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
                        startIndex = segmentsInfos.get(i+1).getStartIndex();
                        stopIndex = segmentsInfos.get(i).getStopIndex();
                    }
                    else if (difference > startIndex - stopIndex) {
                        startIndex = segmentsInfos.get(i+1).getStartIndex();
                        stopIndex = segmentsInfos.get(i).getStopIndex();
                    }
                }
                //if no available space found
                else if(i==segmentsInfos.size()-2 && !found) stopIndex = -2;
            }
            //checking space after last segment
            int spaceAtEndofRam= ramSize-1 - segmentsInfos.get(segmentsInfos.size()-1).getStopIndex();
            if(spaceAtEndofRam > requestedSize && spaceAtEndofRam > startIndex-stopIndex)  stopIndex = segmentsInfos.get(segmentsInfos.size()-1).getStopIndex();
            return stopIndex+1;
        }
        //if no segments return 0
        else if(segmentsInfos.size()==0) return 0;
        //if one segment return space after it
        else {
            int nextAvailable = segmentsInfos.get(0).getStopIndex()+1;

            //if required bigger than available
            if(nextAvailable+requestedSize>ramSize) nextAvailable=-1;

            return nextAvailable;
        }
    }
    public void deleteEntry(int segmentID){
        for (int i=0;i<segmentsInfos.size();i++) {
            if (segmentsInfos.get(i).getSegmentID() == segmentID) {
                segmentsInfos.remove(i);
            }
        }
    }
}
