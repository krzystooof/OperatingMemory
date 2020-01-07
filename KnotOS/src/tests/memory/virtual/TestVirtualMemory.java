package tests.memory.virtual;

import memory.virtual.VirtualMemory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.IntStream;

public class TestVirtualMemory {
    VirtualMemory memory;

    /*
    Initialise test environment by simulating multiple process loading.
    Each process has one or two segment with different data for debug purposes.
     */
    public void initTest() {
        this.memory = new VirtualMemory(1024, 64);
        // Create first process
        byte[] firstProcess = new byte[28];
        IntStream.range(0, 28).forEach(n -> firstProcess[n] = 0);
        memory.load(0, 28, 0, firstProcess);
        // Create second process
        byte[] secondProcess = new byte[64];
        IntStream.range(0, 32).forEach(n -> secondProcess[n] = 1);
        IntStream.range(32, 64).forEach(n -> secondProcess[n] = 'A');
        memory.load(1, 32, 32, secondProcess);
        // Create third process
        byte[] thirdProcess = new byte[32];
        IntStream.range(0, 16).forEach(n -> thirdProcess[n] = 2);
        IntStream.range(16, 32).forEach(n -> thirdProcess[n] = 'B');
        memory.load(2, 16, 16, thirdProcess);
//        byte[] data = new byte[128];
//        IntStream.range(0, 64).forEach(n -> data[n] = 0);
//        IntStream.range(64, 128).forEach(n -> data[n] = 1);
//        memory.load(0, 64, 64, data);
//        IntStream.range(0, 64).forEach(n -> data[n] = 2);
//        IntStream.range(64, 128).forEach(n -> data[n] = 3);
//        memory.load(1, 64, 64, data);
//        IntStream.range(0, 64).forEach(n -> data[n] = 4);
//        IntStream.range(64, 128).forEach(n -> data[n] = 5);
//        memory.load(2, 64, 64, data);
//        IntStream.range(0, 64).forEach(n -> data[n] = 3);
//        IntStream.range(64, 128).forEach(n -> data[n] = 4);
//        memory.load(3, 64, 64, data);
//        memory.load(4, 64, 64, data);
//        IntStream.range(0, 64).forEach(n -> data[n] = 5);
//        IntStream.range(64, 128).forEach(n -> data[n] = 6);
//        memory.load(5, 64, 64, data);
//        IntStream.range(0, 32).forEach(n -> data[n] = 7);
//        IntStream.range(32, 64).forEach(n -> data[n] = 8);
//        memory.load(6, 32, 32, data);
//        IntStream.range(0, 10).forEach(n -> data[n] = 9);
//        memory.load(7, 10, 0, data);
//        IntStream.range(0, 10).forEach(n -> data[n] = 'A');
//        memory.load(8, 10, 0, data);
    }

    @Test
    public void testLoad() {
        initTest();
        assertEquals(memory.getSpaceLeft(true, false), 256);
        assertEquals(memory.getSpaceLeft(false, true), 172);
    }

    @Test
    public void testSegmentationError() {
        initTest();
        assertThrows(IllegalArgumentException.class, () -> {
            memory.read(0, 128);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            memory.read(1, 130);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            memory.read(2, -1);
        });
    }

    @Test
    public void testSwapToFile() {
        initTest();
        assertEquals(memory.getSpaceLeft(false, true), 172);

        memory.read(0, 2);
        memory.read(0, 92);
        memory.read(3, 2);
        memory.read(3, 92);
        assertEquals(memory.getSpaceLeft(true, false), 0);
        assertEquals(memory.getSpaceLeft(false, true), 172);

        memory.read(1,1);
        assertEquals(memory.getSpaceLeft(false, true), 108);
    }

    @Test
    public void testRead() {
        initTest();
        assertEquals(memory.read(0, 0), 0);
        assertEquals(memory.read(0, 27), 0);
        assertEquals(memory.read(1, 0), 1);
        assertEquals(memory.read(1, 32), 'A');
        assertEquals(memory.read(2, 15), 2);
        assertEquals(memory.read(2, 16), 'B');

        assertEquals(memory.read(0, 0), 0);
        assertEquals(memory.read(0, 27), 0);
        assertEquals(memory.read(1, 0), 1);
        assertEquals(memory.read(1, 32), 'A');
        assertEquals(memory.read(2, 15), 2);
        assertEquals(memory.read(2, 16), 'B');

        assertEquals(memory.read(0, 0), 0);
        assertEquals(memory.read(0, 27), 0);
        assertEquals(memory.read(1, 0), 1);
        assertEquals(memory.read(1, 32), 'A');
        assertEquals(memory.read(2, 15), 2);
        assertEquals(memory.read(2, 16), 'B');
        assertEquals(memory.read(0, 0), 0);
        assertEquals(memory.read(0, 27), 0);
        assertEquals(memory.read(1, 0), 1);
        assertEquals(memory.read(1, 32), 'A');
        assertEquals(memory.read(2, 15), 2);
        assertEquals(memory.read(2, 16), 'B');
//        assertEquals(memory.read(0, 63), 0);
//        assertEquals(memory.read(0, 127), 1);
//        assertEquals(memory.read(1, 0), 2);
//        assertEquals(memory.read(1, 64), 3);
//        assertEquals(memory.read(2, 63), 4);
//        assertEquals(memory.read(2, 64), 5);
//        assertEquals(memory.read(3, 1), 3);
//        assertEquals(memory.read(3, 127), 4);
//        assertEquals(memory.read(4, 0), 3);
//        assertEquals(memory.read(4, 127), 4);
//        assertEquals(memory.read(5, 63), 5);
//        assertEquals(memory.read(5, 127), 6);
//        assertEquals(memory.read(6, 31), 7);
//        assertEquals(memory.read(6, 32), 8);
//        assertEquals(memory.read(7, 2), 9);
//        assertEquals(memory.read(8, 2), 'A');
//        assertEquals(memory.read(0, 63), 0);
//        assertEquals(memory.read(0, 127), 1);
//        assertEquals(memory.read(1, 0), 2);
//        assertEquals(memory.read(1, 64), 3);
//        assertEquals(memory.read(2, 63), 4);
//        assertEquals(memory.read(2, 64), 5);
//        assertEquals(memory.read(3, 1), 3);
//        assertEquals(memory.read(3, 127), 4);
//        assertEquals(memory.read(4, 0), 3);
//        assertEquals(memory.read(4, 127), 4);
//        assertEquals(memory.read(5, 63), 5);
//        assertEquals(memory.read(5, 127), 6);
//        assertEquals(memory.read(6, 31), 7);
//        assertEquals(memory.read(6, 32), 8);
//        assertEquals(memory.read(7, 2), 9);
//        assertEquals(memory.read(8, 2), 'A');
    }


    @Test
    public void testDelete() {
        initTest();
        memory.delete(0);
        assertThrows(IllegalArgumentException.class, () -> {
            memory.read(0, 10);
        });
        memory.delete(5);
        assertThrows(IllegalArgumentException.class, () -> {
            memory.read(5, 10);
        });
        memory.delete(6);
        assertThrows(IllegalArgumentException.class, () -> {
            memory.read(6, 10);
        });
    }

    @Test
    public void testWrite() {
        initTest();
        memory.write(0, 5, (byte) 9);
        assertEquals(memory.read(0, 5), (byte) 9);
    }


    @Test
    public void testMemoryShortage() {
        initTest();
        byte[] code = new byte[1200];
        IntStream.range(0, 1200).forEach(n -> code[n] = 1);
        assertThrows(IllegalStateException.class, () -> {
            memory.load(6, 400, 100, code);
        });
    }
}
