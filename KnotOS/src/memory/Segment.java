package memory;

public class Segment {
    public int ID;
    public int base;
    public int limit;

    public Segment(int id, int base, int limit) {
        this.ID = id;
        this.base = base;
        this.limit = limit;
    }
}
