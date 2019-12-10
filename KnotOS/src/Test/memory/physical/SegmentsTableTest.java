package memory.physical;

import memory.SegmentTable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SegmentsTableTest {

    @Test
    void addAndGetSegment() {
        SegmentTable segmentTable = new SegmentTable();
        segmentTable.addSegment(1,20,30);
        segmentTable.addSegment(2,3,2);
        segmentTable.addSegment(4,120,128);
        int[] result = new int[2];
        result[0] = 20;
        result[1] = 30;
        assertArrayEquals(result, segmentTable.getSegment(1));
    }

    @Test
    void isEmpty() {
        SegmentTable segmentTable = new SegmentTable();
        segmentTable.addSegment(1,20,30);
        assertEquals(false, segmentTable.isEmpty());
    }
    @Test
    void bestfitEmptySegmentsTable(){
        SegmentTable segmentTable = new SegmentTable();
        assertEquals(0, segmentTable.bestfit(100));
    }
    @Test
    void bestfitRequestedSizeTooBig(){
        SegmentTable segmentTable = new SegmentTable(128);
        assertEquals(-1, segmentTable.bestfit(150));
    }
    @Test
    void bestfitOneSegmentTable(){
        SegmentTable segmentTable = new SegmentTable();
        segmentTable.addSegment(1,0,30);
        assertEquals(31, segmentTable.bestfit(10));
    }
    @Test
    void bestfit(){
        SegmentTable segmentTable = new SegmentTable();
        segmentTable.addSegment(1,0,30);
        segmentTable.addSegment(2,40,50);
        segmentTable.addSegment(3,55,60);
        segmentTable.addSegment(4,80,81);
        assertEquals(61, segmentTable.bestfit(20));
    }
    @Test
    void bestfit2(){
        SegmentTable segmentTable = new SegmentTable();
        segmentTable.addSegment(1,0,30);
        segmentTable.addSegment(2,40,50);
        segmentTable.addSegment(3,80,127);
        assertEquals(31, segmentTable.bestfit(9));
    }
    @Test
    void bestfit3(){
        SegmentTable segmentTable = new SegmentTable();
        segmentTable.addSegment(1,0,63);
        segmentTable.addSegment(2,64,73);
        segmentTable.addSegment(3,74,95);
        segmentTable.addSegment(4,96,127);
        segmentTable.deleteEntry(2);
        assertEquals(64, segmentTable.bestfit(10));
    }
    @Test
    void bestfitNoAvailableSpace(){
        SegmentTable segmentTable = new SegmentTable();
        segmentTable.addSegment(1,0,63);
        segmentTable.addSegment(2,64,73);
        segmentTable.addSegment(3,74,95);
        segmentTable.addSegment(4,96,127);
        assertEquals(-1, segmentTable.bestfit(10));
    }

}