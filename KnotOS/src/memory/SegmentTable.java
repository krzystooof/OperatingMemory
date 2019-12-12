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

    public int getBase(int ID) {
        return segments.get(ID).BASE;
    }

    public int getLimit(int ID) {
        return segments.get(ID).LIMIT;
    }


    public void swapToRam(int ID) {
        inSwapFile.put(ID, Boolean.FALSE);
    }

    public void swapToFile(int ID) {
        inSwapFile.put(ID, Boolean.TRUE);
    }

    public void flushSegment(int ID) {
        inSwapFile.remove(ID);
        segments.remove(ID);
    }

    public boolean inSwapFile(int ID) {
        return inSwapFile.get(ID);
    }

}
