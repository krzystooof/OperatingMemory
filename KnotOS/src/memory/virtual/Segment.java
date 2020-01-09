package memory.virtual;


public class Segment implements Comparable<Segment> {
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

    /**
     * Created to allow sorting. Ascending sorting by startIndex (0-n)
     *
     * @param segment other instance of same class
     * @return› segment with lower base
     */
    @Override
    public int compareTo(Segment segment) {
        return Integer.compare(this.BASE, segment.BASE);
    }

}
