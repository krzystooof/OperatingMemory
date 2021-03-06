

package memory;



import memory.PhysicalMemoryManager;
import memory.ramSegments;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PhysicalMemoryManagerTest {



    /*
    @Test
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
        ramSegments segmentTable = new ramSegments();
        PhysicalMemoryManager physicalMemoryManager = new PhysicalMemoryManager(128, segmentTable);
        segmentTable.addSegment(1, 20, 10);
        assertEquals(118, physicalMemoryManager.checkAvailableSpace());
    }
    @Test
    void checkAvailableSpace2() {
        //ram full
        ramSegments segmentTable = new ramSegments();
        PhysicalMemoryManager physicalMemoryManager = new PhysicalMemoryManager(128, segmentTable);
        segmentTable.addSegment(0, 0, 128);
        assertEquals(0, physicalMemoryManager.checkAvailableSpace());
    }

    @Test
    void checkAvailableSpace3() {
        ramSegments segmentTable = new ramSegments();
        PhysicalMemoryManager physicalMemoryManager = new PhysicalMemoryManager(128, segmentTable);
        segmentTable.addSegment(0, 0, 10);
        segmentTable.addSegment(1, 30, 10);
        segmentTable.addSegment(2, 80, 10);
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
    void bestfit1() {
        ramSegments segmentTable = new ramSegments();
        PhysicalMemoryManager physicalMemoryManager = new PhysicalMemoryManager(128, segmentTable);
        segmentTable.addSegment(0, 0, 10);
        assertEquals(10, physicalMemoryManager.bestfit(2));
    }

    @Test
    void bestfit2() {
        ramSegments ramSegments = new ramSegments();
        PhysicalMemoryManager physicalMemoryManager = new PhysicalMemoryManager(128,ramSegments);
        ramSegments.addSegment(0, 0, 10);
        ramSegments.addSegment(2, 11, 10);
        assertEquals(21, physicalMemoryManager.bestfit(2));
    }

    @Test
    void bestfit3() {
        ramSegments segmentTable = new ramSegments();
        PhysicalMemoryManager physicalMemoryManager = new PhysicalMemoryManager(128, segmentTable);
        segmentTable.addSegment(0, 0, 10);
        segmentTable.addSegment(1, 0, 0);
        segmentTable.addSegment(2, 11, 10);
        segmentTable.addSegment(3, 0, 0);
        segmentTable.addSegment(4, 24, 68);
        assertEquals(21, physicalMemoryManager.bestfit(2));
    }
    @Test
    void bestfit4() {
        ramSegments segmentTable = new ramSegments();
        PhysicalMemoryManager physicalMemoryManager = new PhysicalMemoryManager(128, segmentTable);
        segmentTable.addSegment(0, 0, 10);
        segmentTable.addSegment(1, 0, 0);
        segmentTable.addSegment(2, 11, 10);
        segmentTable.addSegment(3, 0, 0);
        segmentTable.addSegment(4, 24, 68);
        segmentTable.addSegment(5, 100, 28);
        assertEquals(21, physicalMemoryManager.bestfit(2));
    }
    @Test
    void bestfit5() {
        ramSegments segmentTable = new ramSegments();
        PhysicalMemoryManager physicalMemoryManager = new PhysicalMemoryManager(128, segmentTable);
        segmentTable.addSegment(0, 0, 10);
        segmentTable.addSegment(1, 0, 0);
        segmentTable.addSegment(2, 11, 10);
        segmentTable.addSegment(3, 0, 0);
        segmentTable.addSegment(4, 30, 65);
        segmentTable.addSegment(5, 100, 28);
        assertEquals(95, physicalMemoryManager.bestfit(2));
    }
    @Test
    void bestfit6(){
        ramSegments segmentTable = new ramSegments();
        PhysicalMemoryManager physicalMemoryManager = new PhysicalMemoryManager(256, segmentTable);
        segmentTable.addSegment(1, 64, 64);
        segmentTable.addSegment(4, 0, 64);
        assertEquals(128, physicalMemoryManager.bestfit(64));
    }
    @Test
    void bestfit7(){
        ramSegments segmentTable = new ramSegments();
        PhysicalMemoryManager physicalMemoryManager = new PhysicalMemoryManager(256, segmentTable);
        segmentTable.addSegment(1, 64, 64);
        segmentTable.addSegment(4, 126, 64);
        segmentTable.addSegment(3, 0, 0);
        segmentTable.addSegment(8, 254, 1);
        assertEquals(0, physicalMemoryManager.bestfit(64));
    }
    @Test
    void bestfit8(){
        ramSegments segmentTable = new ramSegments();
        PhysicalMemoryManager physicalMemoryManager = new PhysicalMemoryManager(256, segmentTable);
        segmentTable.addSegment(1, 60, 65);
        segmentTable.addSegment(4, 126, 64);
        segmentTable.addSegment(3, 0, 0);
        segmentTable.addSegment(8, 245, 1);
        assertEquals(190, physicalMemoryManager.bestfit(20));
    }
    @Test
    void bestfit9(){
        ramSegments segmentTable = new ramSegments();
        PhysicalMemoryManager physicalMemoryManager = new PhysicalMemoryManager(4, segmentTable);
        segmentTable.addSegment(1, 0, 1);
        segmentTable.addSegment(2, 2, 1);
        byte[] towrite = new byte[2];
        physicalMemoryManager.write(towrite,3,0);
        assertEquals(segmentTable.getSegment(2).BASE, 1);
    }
}