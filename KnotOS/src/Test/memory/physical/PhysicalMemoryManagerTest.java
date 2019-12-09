package memory.physical;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PhysicalMemoryManagerTest {

    @Test
    void writeCheckID() {
        PhysicalMemoryManager physicalMemoryManager = new PhysicalMemoryManager();
        int i1 = 23;
        byte[] toWrite = new byte[5];
        for (int i = 0; i < 5; i++) {
            toWrite[i] = (byte) i1;
        }
        physicalMemoryManager.write(toWrite);
        assertEquals(2, physicalMemoryManager.write(toWrite));
    }

    @Test
    void readNonExistingSegment() {
        PhysicalMemoryManager physicalMemoryManager = new PhysicalMemoryManager();
        int i1 = 23;
        byte[] toWrite = new byte[5];
        for (int i = 0; i < 5; i++) {
            toWrite[i] = (byte) i1;
        }
        physicalMemoryManager.write(toWrite);
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
        physicalMemoryManager.write(toWrite);
        assertThrows(IllegalArgumentException.class, () -> physicalMemoryManager.read(1, 8));
    }

    @Test
    void wipe() {
        PhysicalMemoryManager physicalMemoryManager = new PhysicalMemoryManager();
        int i1 = 23;
        byte[] toWrite = new byte[5];
        for (int i = 0; i < 5; i++) {
            toWrite[i] = (byte) i1;
        }
        physicalMemoryManager.write(toWrite);
        physicalMemoryManager.write(toWrite);
        physicalMemoryManager.wipe(2);
        assertThrows(IllegalArgumentException.class, () -> physicalMemoryManager.read(2));
    }

    @Test
    void makeMemoryFull() {
        int size = 16;
        int i1;
        byte[] toWrite = new byte[size];
        PhysicalMemoryManager physicalMemoryManager = new PhysicalMemoryManager(128);
        for (i1=0; i1 < (128-size)/size; i1++) {
            for (int i = 0; i < toWrite.length; i++) {
                toWrite[i] = (byte) i1;
            }
            physicalMemoryManager.write(toWrite);
        }
        for (int i = 0; i < toWrite.length; i++) {
            toWrite[i] = (byte) (i1+1);
        }
        assertArrayEquals(toWrite, physicalMemoryManager.read(physicalMemoryManager.write(toWrite)));
    }
    @Test
    void writeToFullMemory(){
        int size = 16;
        int i1;
        byte[] toWrite = new byte[size];
        PhysicalMemoryManager physicalMemoryManager = new PhysicalMemoryManager(128);
        for (i1=0; i1 < 128/size; i1++) {
            for (int i = 0; i < toWrite.length; i++) {
                toWrite[i] = (byte) i1;
            }
            physicalMemoryManager.write(toWrite);
        }
        assertThrows(IllegalArgumentException.class, () -> physicalMemoryManager.write(toWrite));
    }

    @Test
    void ManagerTest() {
        PhysicalMemoryManager physicalMemoryManager = new PhysicalMemoryManager();
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
                    physicalMemoryManager.wipe(1);
                    size = 10;
                    break;
                case 4:
                    size = 5;
                    break;
                case 5:
                    physicalMemoryManager.wipe(2);
                    physicalMemoryManager.wipe(4);
                    size = 8;
            }
            toWrite = new byte[size];
            for (int i = 0; i < toWrite.length; i++) {
                toWrite[i] = (byte) i1;
            }
            physicalMemoryManager.write(toWrite);
        }
        int i1 = 6;
        toWrite = new byte[5];
        for (int i = 0; i < toWrite.length; i++) {
            toWrite[i] = (byte) i1;
        }
        assertArrayEquals(toWrite, physicalMemoryManager.read(physicalMemoryManager.write(toWrite)));
    }

}