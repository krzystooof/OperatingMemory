package memory.physical;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SegmentsTableTest {

    @Test
    void addAndGetSegment() {
        SegmentsTable segmentsTable = new SegmentsTable();
        segmentsTable.addSegment(1,20,30);
        segmentsTable.addSegment(2,3,2);
        segmentsTable.addSegment(4,120,128);
        int[] result = new int[2];
        result[0] = 20;
        result[1] = 30;
        assertArrayEquals(result,segmentsTable.getSegment(1));
    }

    @Test
    void isEmpty() {
        SegmentsTable segmentsTable = new SegmentsTable();
        segmentsTable.addSegment(1,20,30);
        assertEquals(false,segmentsTable.isEmpty());
    }
    @Test
    void bestfitEmptySegmentsTable(){
        SegmentsTable segmentsTable = new SegmentsTable();
        assertEquals(0,segmentsTable.bestfit(100));
    }
    @Test
    void hobestfitRequestedSizeTooBig(){
        SegmentsTable segmentsTable = new SegmentsTable(128);
        assertEquals(-1,segmentsTable.bestfit(150));
    }
    @Test
    void bestfitOneSegmentTable(){
        SegmentsTable segmentsTable = new SegmentsTable();
        segmentsTable.addSegment(1,0,30);
        assertEquals(31,segmentsTable.bestfit(10));
    }
    @Test
    void bestfit(){
        SegmentsTable segmentsTable = new SegmentsTable();
        segmentsTable.addSegment(1,0,30);
        segmentsTable.addSegment(2,40,50);
        segmentsTable.addSegment(3,55,60);
        segmentsTable.addSegment(4,80,81);
        assertEquals(61,segmentsTable.bestfit(20));
    }
    @Test
    void bestfit2(){
        SegmentsTable segmentsTable = new SegmentsTable();
        segmentsTable.addSegment(1,0,30);
        segmentsTable.addSegment(2,40,50);
        segmentsTable.addSegment(3,80,127);
        assertEquals(31,segmentsTable.bestfit(9));
    }
    @Test
    void bestfit3(){
        SegmentsTable segmentsTable = new SegmentsTable();
        segmentsTable.addSegment(1,0,63);
        segmentsTable.addSegment(2,64,73);
        segmentsTable.addSegment(3,74,95);
        segmentsTable.addSegment(4,96,127);
        segmentsTable.deleteEntry(2);
        assertEquals(64,segmentsTable.bestfit(10));
    }
    @Test
    void bestfitNoAvailableSpace(){
        SegmentsTable segmentsTable = new SegmentsTable();
        segmentsTable.addSegment(1,0,63);
        segmentsTable.addSegment(2,64,73);
        segmentsTable.addSegment(3,74,95);
        segmentsTable.addSegment(4,96,127);
        assertEquals(-1,segmentsTable.bestfit(10));
    }

}