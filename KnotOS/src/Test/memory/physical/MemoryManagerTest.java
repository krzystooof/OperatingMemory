package memory.physical;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemoryManagerTest {

    @Test
    void writeCheckID() {
        MemoryManager memoryManager = new MemoryManager();
        int i1 = 23;
        byte[] toWrite = new byte[5];
        for (int i = 0; i < 5; i++) {
            toWrite[i] = (byte) i1;
        }
        memoryManager.write(toWrite);
        assertEquals(2, memoryManager.write(toWrite));
    }

    @Test
    void readNonExistingSegment() {
        MemoryManager memoryManager = new MemoryManager();
        int i1 = 23;
        byte[] toWrite = new byte[5];
        for (int i = 0; i < 5; i++) {
            toWrite[i] = (byte) i1;
        }
        memoryManager.write(toWrite);
        assertThrows(IllegalArgumentException.class, () -> memoryManager.read(5));
    }

    @Test
    void readOffsetOutOfSegment() {
        MemoryManager memoryManager = new MemoryManager();
        int i1 = 23;
        byte[] toWrite = new byte[5];
        for (int i = 0; i < 5; i++) {
            toWrite[i] = (byte) i1;
        }
        memoryManager.write(toWrite);
        assertThrows(IllegalArgumentException.class, () -> memoryManager.read(1, 8));
    }

    @Test
    void wipe() {
        MemoryManager memoryManager = new MemoryManager();
        int i1 = 23;
        byte[] toWrite = new byte[5];
        for (int i = 0; i < 5; i++) {
            toWrite[i] = (byte) i1;
        }
        memoryManager.write(toWrite);
        memoryManager.write(toWrite);
        memoryManager.wipe(2);
        assertThrows(IllegalArgumentException.class, () -> memoryManager.read(2));
    }

    @Test
    void makeMemoryFull() {
        int size = 16;
        int i1;
        byte[] toWrite = new byte[size];
        MemoryManager memoryManager = new MemoryManager(128);
        for (i1=0; i1 < (128-size)/size; i1++) {
            for (int i = 0; i < toWrite.length; i++) {
                toWrite[i] = (byte) i1;
            }
            memoryManager.write(toWrite);
        }
        for (int i = 0; i < toWrite.length; i++) {
            toWrite[i] = (byte) (i1+1);
        }
        assertArrayEquals(toWrite, memoryManager.read(memoryManager.write(toWrite)));
    }
    @Test
    void writeToFullMemory(){
        int size = 16;
        int i1;
        byte[] toWrite = new byte[size];
        MemoryManager memoryManager = new MemoryManager(128);
        for (i1=0; i1 < 128/size; i1++) {
            for (int i = 0; i < toWrite.length; i++) {
                toWrite[i] = (byte) i1;
            }
            memoryManager.write(toWrite);
        }
        assertThrows(IllegalArgumentException.class, () -> memoryManager.write(toWrite));
    }

    @Test
    void ManagerTest() {
        MemoryManager memoryManager = new MemoryManager();
        byte[] toWrite;
        for (int i1 = 1; i1 < 5; i1++) {
            int size = 0;
            switch (i1) {
                case 1:
                    size = 64;
                    break;
                case 2:
                    size = 32;
                    break;
                case 3:
                    memoryManager.wipe(1);
                    size = 10;
                    break;
                case 4:
                    size = 5;
                    break;
                case 5:
                    memoryManager.wipe(2);
                    memoryManager.wipe(4);
                    size = 8;
            }
            toWrite = new byte[size];
            for (int i = 0; i < toWrite.length; i++) {
                toWrite[i] = (byte) i1;
            }
            memoryManager.write(toWrite);
        }
        int i1 = 6;
        toWrite = new byte[5];
        for (int i = 0; i < toWrite.length; i++) {
            toWrite[i] = (byte) i1;
        }
        assertArrayEquals(toWrite, memoryManager.read(memoryManager.write(toWrite)));
    }

}