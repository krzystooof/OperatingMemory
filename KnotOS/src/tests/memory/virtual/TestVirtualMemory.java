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
    }

    @Test
    public void testLoad() {
        initTest();
        assertEquals(memory.getSpaceLeft(true, false), 64);
        assertEquals(memory.getSpaceLeft(false, true), 900);
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
        memory.read(0, 2);
        memory.read(1, 31);
        assertEquals(memory.getSpaceLeft(true, false), 4);
        memory.read(1,35);
        assertEquals(memory.getSpaceLeft(true, false), 0);
        memory.read(1,1);
        assertEquals(memory.getSpaceLeft(true, false), 16);

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
    }


    @Test
    public void testDelete() {
        initTest();
        memory.delete(0);
        assertThrows(IllegalArgumentException.class, () -> {
            memory.read(0, 10);
        });
        memory.delete(1);
        assertThrows(IllegalArgumentException.class, () -> {
            memory.read(1, 10);
        });
        memory.delete(2);
        assertThrows(IllegalArgumentException.class, () -> {
            memory.read(2, 10);
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
            memory.load(4, 1100, 100, code);
        });
    }
}
