package memory;

public class Segment {
    public int ID;
    public int BASE;
    public int LIMIT;

    public Segment(int id, int base, int limit) {
        this.ID = id;
        this.BASE = base;
        this.LIMIT = limit;
    }
}
