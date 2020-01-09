package memory.virtual;

import java.util.ArrayList;
import java.util.HashMap;


public class SegmentTable {
    public ArrayList<Segment> segments = new ArrayList<>();
    public HashMap<Integer, Boolean> inSwapFile = new HashMap<>();

    public void addSegment(int ID, int base, int limit) {
        segments.add(new Segment(ID, base, limit));
        inSwapFile.put(ID, Boolean.TRUE);
    }

    public Segment getSegment(int ID) {
        return findSegment(ID);
    }

    public int getBase(int ID) {
        return findSegment(ID).BASE;
    }

    public void setBase(int ID, int value) {
            findSegment(ID).BASE = value;

    }

    public int getLimit(int ID) {
        try{
            return findSegment(ID).LIMIT;
        }
        catch (IllegalArgumentException e){
            return 0;
        }
    }

    public void updateToRam(int ID) {
        inSwapFile.put(ID, Boolean.FALSE);
    }

    public void updateToSwap(int ID) {
        inSwapFile.put(ID, Boolean.TRUE);
    }

    public void delete(int ID) {
        inSwapFile.remove(ID);
        segments.removeIf(segment -> (segment.ID == ID));
    }

    public boolean inSwapFile(int ID) {
        return inSwapFile.get(ID);
    }

    private Segment findSegment(int ID) {
        for (Segment segment : segments) {
            if (segment.ID == ID) {
                return segment;
            }
        }
        throw new IllegalArgumentException("SEGMENT DOES NOT EXIST");
    }

    public boolean hasHighestBase(int ID){
        Segment highest = findSegment(ID);
        for (Segment segment : segments) {
            if (segment.BASE > highest.BASE && inSwapFile(segment.ID)){
                 return false;
            }
        }
        return true;
    }

}
