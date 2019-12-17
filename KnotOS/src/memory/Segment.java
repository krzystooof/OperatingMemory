package memory;


public class Segment {
    public int ID;
    public int BASE;
    public int LIMIT;

    /**
     * Creates segment structure
     *
     * @param id    segment unique id
     * @param base  start index
     * @param limit size of block
     */
    public Segment(int id, int base, int limit) {
        this.ID = id;
        this.BASE = base;
        this.LIMIT = limit;
    }
}
