package memory.physical;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemoryManagerTest {

    @Test
    void writeCheckID() {
        MemoryManager memoryManager = new MemoryManager();
        int i1 = 23;
        byte[] toWrite = new byte[5];
        for(int i =0;i<5;i++) {
            toWrite[i] = (byte) i1;
        }
        memoryManager.write(toWrite);
        assertEquals(2,memoryManager.write(toWrite));
    }

    @Test
    void readNonExistingSegment() {
        MemoryManager memoryManager = new MemoryManager();
        int i1 = 23;
        byte[] toWrite = new byte[5];
        for(int i =0;i<5;i++) {
            toWrite[i] = (byte) i1;
        }
        memoryManager.write(toWrite);
        assertThrows(IllegalArgumentException.class,() -> memoryManager.read(5));
    }
    @Test
    void readOffsetOutOfSegment() {
        MemoryManager memoryManager = new MemoryManager();
        int i1 = 23;
        byte[] toWrite = new byte[5];
        for(int i =0;i<5;i++) {
            toWrite[i] = (byte) i1;
        }
        memoryManager.write(toWrite);
        assertThrows(IllegalArgumentException.class,() -> memoryManager.read(1,8));
    }
    @Test
    void wipe(){
        MemoryManager memoryManager = new MemoryManager();
        int i1 = 23;
        byte[] toWrite = new byte[5];
        for(int i =0;i<5;i++) {
            toWrite[i] = (byte) i1;
        }
        memoryManager.write(toWrite);
        memoryManager.write(toWrite);
        memoryManager.wipe(2);
        assertThrows(IllegalArgumentException.class,() -> memoryManager.read(2));
    }
    //TODO read one byte, whole segment, part of segment
    @Test
    void ManagerTest(){
        //TODO bestfit not working here
        MemoryManager memoryManager = new MemoryManager();
        int i1 = 1;
        byte[] toWrite = new byte[64];
        for(int i =0;i<toWrite.length;i++) {
            toWrite[i] = (byte) i1;
        }
        memoryManager.write(toWrite);
        i1=2;
        toWrite = new byte[10];
        for(int i =0;i<toWrite.length;i++) {
            toWrite[i] = (byte) i1;
        }
        memoryManager.write(toWrite);
        i1=3;
        toWrite = new byte[22];
        for(int i =0;i<toWrite.length;i++) {
            toWrite[i] = (byte) i1;
        }
        memoryManager.write(toWrite);
        i1=4;
        toWrite = new byte[20];
        for(int i =0;i<toWrite.length;i++) {
            toWrite[i] = (byte) i1;
        }
        memoryManager.write(toWrite);
        i1=5;
        toWrite = new byte[12];
        for(int i =0;i<toWrite.length;i++) {
            toWrite[i] = (byte) i1;
        }
        memoryManager.write(toWrite);
        memoryManager.wipe(1);
        memoryManager.wipe(4);
        i1=6;
        toWrite = new byte[5];
        for(int i =0;i<toWrite.length;i++) {
            toWrite[i] = (byte) i1;
        }
        memoryManager.write(toWrite);
        assertArrayEquals(toWrite,memoryManager.read(6));
    }

}