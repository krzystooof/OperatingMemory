package memory;

import memory.Segment;

import java.util.ArrayList;

public class ramSegments {
    public ArrayList<Segment> segments = new ArrayList<>();

    public void addSegment(int ID, int base, int limit) {
        segments.add(new Segment(ID, base, limit));
    }

    public Segment getSegment(int ID) {
        return findSegment(ID);
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

    public void delete(int ID) {
        segments.removeIf(segment -> (segment.ID == ID));
    }

    private Segment findSegment(int ID) {
        for (Segment segment : segments) {
            if (segment.ID == ID) {
                return segment;
            }
        }
        throw new IllegalArgumentException("SEGMENT DOES NOT EXIST");
    }
}


