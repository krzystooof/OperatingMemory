package memory;

import memory.Segment;

import java.util.ArrayList;

public class ramSegments {
    public ArrayList<Segment> segments = new ArrayList<>();

    public void addSegment(int ID, int base, int limit) {
        segments.add(new Segment(ID, base, limit));
    }

    public void setBase(int ID, int value) {
        getSegment(ID).BASE = value;

    }

    public void delete(int ID) {
        segments.removeIf(segment -> (segment.ID == ID));
    }

    public boolean findSegment(int ID){
        try {
            Segment segment = getSegment(ID);
            return true;
        }
        catch (IllegalArgumentException e) {return false;}
    }

    public Segment getSegment(int ID) {
        for (Segment segment : segments) {
            if (segment.ID == ID) {
                return segment;
            }
        }
        throw new IllegalArgumentException("SEGMENT DOES NOT EXIST");
    }
}


