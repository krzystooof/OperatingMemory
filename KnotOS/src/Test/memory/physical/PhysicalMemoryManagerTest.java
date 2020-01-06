

package memory.physical;



import memory.SegmentTable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PhysicalMemoryManagerTest {

    /*
    Changed logic  - tests not longer valid
    @Test
    void readNonExistingSegment() {
        PhysicalMemoryManager physicalMemoryManager = new PhysicalMemoryManager();
        int i1 = 23;
        byte[] toWrite = new byte[5];
        for (int i = 0; i < 5; i++) {
            toWrite[i] = (byte) i1;
        }
        physicalMemoryManager.write(toWrite, 1);
        assertThrows(IllegalArgumentException.class, () -> physicalMemoryManager.read(5));
    }

    @Test
    void readOffsetOutOfSegment() {
        PhysicalMemoryManager physicalMemoryManager = new PhysicalMemoryManager();
        int i1 = 23;
        byte[] toWrite = new byte[5];
        for (int i = 0; i < 5; i++) {
            toWrite[i] = (byte) i1;
        }
        physicalMemoryManager.write(toWrite, 1);
        assertThrows(IllegalArgumentException.class, () -> physicalMemoryManager.read(1, 8));
    }


    @Test
    void makeMemoryFull() {
        int size = 16;
        int i1;
        byte[] toWrite = new byte[size];
        PhysicalMemoryManager physicalMemoryManager = new PhysicalMemoryManager();
        for (i1 = 0; i1 < (128 - size) / size; i1++) {
            for (int i = 0; i < toWrite.length; i++) {
                toWrite[i] = (byte) i1;
            }
            physicalMemoryManager.write(toWrite, i1);
        }
        for (int i = 0; i < toWrite.length; i++) {
            toWrite[i] = (byte) (i1 + 1);
        }
        assertArrayEquals(toWrite, physicalMemoryManager.read(physicalMemoryManager.write(toWrite, 100)));
    }
    */

    @Test
    void checkAvailableSpace1() {
        //one segment
        SegmentTable segmentTable = new SegmentTable();
        PhysicalMemoryManager physicalMemoryManager = new PhysicalMemoryManager(128, segmentTable);
        segmentTable.addSegment(0, 0, 10);
        segmentTable.inSwapFile.put(0, false);
        segmentTable.addSegment(1, 20, 10);
        segmentTable.inSwapFile.put(1, true);
        assertEquals(118, physicalMemoryManager.checkAvailableSpace());
    }
    @Test
    void checkAvailableSpace2() {
        //ram full
        SegmentTable segmentTable = new SegmentTable();
        PhysicalMemoryManager physicalMemoryManager = new PhysicalMemoryManager(128, segmentTable);
        segmentTable.addSegment(0, 0, 128);
        segmentTable.inSwapFile.put(0, false);
        assertEquals(0, physicalMemoryManager.checkAvailableSpace());
    }
    @Test
    void checkAvailableSpace3() {
        SegmentTable segmentTable = new SegmentTable();
        PhysicalMemoryManager physicalMemoryManager = new PhysicalMemoryManager(128, segmentTable);
        segmentTable.addSegment(0, 0, 10);
        segmentTable.inSwapFile.put(0, false);
        segmentTable.addSegment(1, 30, 10);
        segmentTable.inSwapFile.put(1, false);
        segmentTable.addSegment(2, 80, 10);
        segmentTable.inSwapFile.put(2, false);
        assertEquals(98, physicalMemoryManager.checkAvailableSpace());
    }

    /*
    Changed logic of write() - test not longer valid
    @Test
    void writeToFullMemory() {
        int size = 16;
        int i1;
        byte[] toWrite = new byte[size];
        PhysicalMemoryManager physicalMemoryManager = new PhysicalMemoryManager();
        for (i1 = 0; i1 < 128 / size; i1++) {
            for (int i = 0; i < toWrite.length; i++) {
                toWrite[i] = (byte) i1;
            }
            physicalMemoryManager.write(toWrite, i1);
            physicalMemoryManager.segmentTable.inSwapFile.put(i1, false);
        }
        assertThrows(IllegalArgumentException.class, () -> physicalMemoryManager.write(toWrite, 100));
    }
    */
    @Test
    void getRamSegmentsOneSegment() {
        SegmentTable segmentTable = new SegmentTable();
        PhysicalMemoryManager physicalMemoryManager = new PhysicalMemoryManager(128, segmentTable);
        segmentTable.addSegment(0, 0, 10);
        segmentTable.inSwapFile.put(0, false);
        assertEquals(1, physicalMemoryManager.getRamSegments().size());
    }

    @Test
    void getRamSegmentsMoreSegments() {
        SegmentTable segmentTable = new SegmentTable();
        PhysicalMemoryManager physicalMemoryManager = new PhysicalMemoryManager(128, segmentTable);
        segmentTable.addSegment(0, 0, 10);
        segmentTable.inSwapFile.put(0, false);
        segmentTable.addSegment(1, 0, 0);
        segmentTable.inSwapFile.put(1, true);
        segmentTable.addSegment(2, 11, 21);
        segmentTable.inSwapFile.put(2, false);
        segmentTable.addSegment(3, 0, 0);
        segmentTable.inSwapFile.put(3, true);
        assertEquals(2, physicalMemoryManager.getRamSegments().size());
    }

    @Test
    void bestfit1() {
        SegmentTable segmentTable = new SegmentTable();
        PhysicalMemoryManager physicalMemoryManager = new PhysicalMemoryManager(128, segmentTable);
        segmentTable.addSegment(0, 0, 10);
        segmentTable.inSwapFile.put(0, false);
        assertEquals(10, physicalMemoryManager.bestfit(2));
    }

    @Test
    void bestfit2() {
        SegmentTable segmentTable = new SegmentTable();
        PhysicalMemoryManager physicalMemoryManager = new PhysicalMemoryManager(128, segmentTable);
        segmentTable.addSegment(0, 0, 10);
        segmentTable.inSwapFile.put(0, false);
        segmentTable.addSegment(1, 0, 0);
        segmentTable.inSwapFile.put(1, true);
        segmentTable.addSegment(2, 11, 10);
        segmentTable.inSwapFile.put(2, false);
        segmentTable.addSegment(3, 0, 0);
        segmentTable.inSwapFile.put(3, true);
        assertEquals(21, physicalMemoryManager.bestfit(2));
    }

    @Test
    void bestfit3() {
        SegmentTable segmentTable = new SegmentTable();
        PhysicalMemoryManager physicalMemoryManager = new PhysicalMemoryManager(128, segmentTable);
        segmentTable.addSegment(0, 0, 10);
        segmentTable.inSwapFile.put(0, false);
        segmentTable.addSegment(1, 0, 0);
        segmentTable.inSwapFile.put(1, true);
        segmentTable.addSegment(2, 11, 10);
        segmentTable.inSwapFile.put(2, false);
        segmentTable.addSegment(3, 0, 0);
        segmentTable.inSwapFile.put(3, true);
        segmentTable.addSegment(4, 24, 68);
        segmentTable.inSwapFile.put(4, false);
        assertEquals(21, physicalMemoryManager.bestfit(2));
    }
    @Test
    void bestfit4() {
        SegmentTable segmentTable = new SegmentTable();
        PhysicalMemoryManager physicalMemoryManager = new PhysicalMemoryManager(128, segmentTable);
        segmentTable.addSegment(0, 0, 10);
        segmentTable.inSwapFile.put(0, false);
        segmentTable.addSegment(1, 0, 0);
        segmentTable.inSwapFile.put(1, true);
        segmentTable.addSegment(2, 11, 10);
        segmentTable.inSwapFile.put(2, false);
        segmentTable.addSegment(3, 0, 0);
        segmentTable.inSwapFile.put(3, true);
        segmentTable.addSegment(4, 24, 68);
        segmentTable.inSwapFile.put(4, false);
        segmentTable.addSegment(5, 100, 28);
        segmentTable.inSwapFile.put(5, false);
        assertEquals(21, physicalMemoryManager.bestfit(2));
    }
    @Test
    void bestfit5() {
        SegmentTable segmentTable = new SegmentTable();
        PhysicalMemoryManager physicalMemoryManager = new PhysicalMemoryManager(128, segmentTable);
        segmentTable.addSegment(0, 0, 10);
        segmentTable.inSwapFile.put(0, false);
        segmentTable.addSegment(1, 0, 0);
        segmentTable.inSwapFile.put(1, true);
        segmentTable.addSegment(2, 11, 10);
        segmentTable.inSwapFile.put(2, false);
        segmentTable.addSegment(3, 0, 0);
        segmentTable.inSwapFile.put(3, true);
        segmentTable.addSegment(4, 30, 65);
        segmentTable.inSwapFile.put(4, false);
        segmentTable.addSegment(5, 100, 28);
        segmentTable.inSwapFile.put(5, false);
        assertEquals(95, physicalMemoryManager.bestfit(2));
    }
}