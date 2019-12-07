package memory.physical;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SegmentsTableTest {

    @Test
    void addAndGetSegment() {
        SegmentsTable segmentsTable = new SegmentsTable();
        segmentsTable.addSegment(1,20,30);
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
}