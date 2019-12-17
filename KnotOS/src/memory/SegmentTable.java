package memory;

import java.util.ArrayList;
import java.util.HashMap;


public class SegmentTable {
    private ArrayList<Segment> segments = new ArrayList<Segment>();
    private HashMap<Integer, Boolean> inSwapFile = new HashMap<Integer, Boolean>();

    public void addSegment(int ID, int base, int limit) {
        segments.add(new Segment(ID, base, limit));
        inSwapFile.put(ID, Boolean.TRUE);
    }
    public Segment getSegment(int ID){
        return findSegment(ID);
    }

    public int getBase(int ID) {
        return findSegment(ID).BASE;
    }

    public void setBase(int ID, int value) {
        if (inSwapFile(ID)){
            throw new IllegalArgumentException("ACCESS DENIED");
        }else{
            findSegment(ID).BASE = value;
        }
    }

    public int getLimit(int ID) {
        return findSegment(ID).LIMIT;
    }

    public void swapToRam(int ID) {
        inSwapFile.put(ID, Boolean.FALSE);
    }

    public void swapToFile(int ID) {
        inSwapFile.put(ID, Boolean.TRUE);
    }

    public void flushSegment(int ID) {
        inSwapFile.remove(ID);
        segments.removeIf(segment -> (segment.ID == ID));
    }

    public boolean inSwapFile(int ID) {
        return inSwapFile.get(ID);
    }

    private Segment findSegment(int ID){
        for(Segment s : segments){
            if (s.ID == ID){
                return s;
            }
        }
        System.out.println("SEGMENT_DOES_NOT_EXIST");
        return new Segment(-1, -1, -1);
    }
}
