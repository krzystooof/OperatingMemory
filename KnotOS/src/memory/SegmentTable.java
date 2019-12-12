/**
 * <h1>KnotOS PhysicalMemory</h1>
 * @author Krzysztof Greczka
 * @since 12.2019
 * This code is a project for Operating Systems 2019 subject.
 * <p>
 *  Table of segments' info. Used to determine where to write and protect memory
 */
package memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class SegmentTable {
    private ArrayList<Segment> segments = new ArrayList<Segment>();
    private HashMap<Integer, Boolean> inSwapFile = new HashMap<Integer, Boolean>();

    public void addSegment(int ID, int base, int limit){
        segments.add(new Segment(ID, base, limit));
        inSwapFile.put(ID, Boolean.TRUE);
    }
    public int[] getSegmentData(int ID){
        return new int[]{segments.get(ID).base , segments.get(ID).limit};
    }
    public Segment get(int ID){
        return segments.get(ID);
    }

    public void swapToRam(int ID){
        inSwapFile.put(ID, Boolean.FALSE);
    }
    public void swapToFile(int ID){
        inSwapFile.put(ID, Boolean.TRUE);
    }
    public void flushSegment(int ID){
        inSwapFile.remove(ID);
        segments.remove(ID);
    }
    public boolean inSwapFile(int ID){
        if (inSwapFile.get(ID)){
            return true;
        }else {
            return false;
        }
    }

    public int getLen(int ID){
        return segments.get(ID).limit;
    }
}
