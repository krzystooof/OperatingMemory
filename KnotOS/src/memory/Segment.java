/**
 * <h1>KnotOS PhysicalMemory</h1>
 * @author Krzysztof Greczka
 * @since 12.2019
 * This code is a project for Operating Systems 2019 subject.
 * <p>
 *  Record of info needed to read and write data. Used in SegmentsTable class.
 */
package memory;

public class Segment implements Comparable<Segment> {
    private int id;
    private int base;
    private int limit;

    public Segment(int id, int base, int limit) {
        this.id = id;
        this.base = base;
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }

    protected void setLimit(int limit) {
        this.limit = limit;
    }

    public int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    public int getBase() {
        return base;
    }

    protected void setBase(int base) {
        this.base = base;
    }

    /**
     * Created to allow sorting. Ascending sorting by startIndex (0-n)
     * @param segment other instance of same class
     * @return›
     */
    @Override
    public int compareTo(Segment segment) {
        return Integer.compare(this.base, segment.base);
    }
}
