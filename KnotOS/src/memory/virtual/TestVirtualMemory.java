package memory.virtual;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.IntStream;

public class TestVirtualMemory {
    VirtualMemory memory;

    public void initTestEnv(int virtualSize, int physicalSize, int procSize, int procLen, boolean testData) {
        this.memory = new VirtualMemory(virtualSize, physicalSize);
        byte[] code = new byte[48];
        IntStream.range(0, 63).forEach(n -> code[n] = 1);
        IntStream.range(0, procLen).forEach(n -> {
            if (testData) {
                memory.loadProcess(n, procSize, procSize, code);
            } else {
                memory.loadProcess(n, procSize, 0, code);
            }
        });
    }

    @Test
    public void testRead() {
        initTestEnv(1024, 128, 64, 1, true);
        assertEquals(memory.read(0, 5), 1);
        assertEquals(memory.read(0, 65), 1);
    }


    @Test
    public void testFlushProcess() {
        initTestEnv(1024, 128, 64, 1, true);
        assertThrows(IllegalArgumentException.class, () -> {
            memory.flushProcess(0);
        });
    }

    @Test
    public void testWrite() {
        initTestEnv(1024, 128, 64, 1, true);
        memory.write(0, 5, (byte) 9);
        assertEquals(memory.read(0, 5), (byte) 9);
    }

    @Test
    public void testShowMemoryLeft() {
        initTestEnv(1024, 128, 0, 0, true);
        assertEquals(memory.showMemoryLeft(true), 1024);
    }

    @Test
    public void testSwapToFile() {
        this.memory = new VirtualMemory(128, 16);
        byte[] code = new byte[8];
        IntStream.range(0, 7).forEach(n -> code[n] = 1);
        memory.loadProcess(0, 8, 0, code);
        memory.read(0, 1);
        memory.loadProcess(1, 8, 0, code);
        memory.read(1, 1);
        memory.loadProcess(2, 8, 0, code);
        assertEquals(memory.showMemoryLeft(true), 120);
        assertEquals(memory.showMemoryLeft(false), 0);
    }

    @Test
    public void testMemoryShortage() {
        initTestEnv(1024, 128, 64, 16, false);
        byte[] code = new byte[8];
        IntStream.range(0, 7).forEach(n -> code[n] = 1);
        assertThrows( IllegalStateException.class, () -> {
            memory.loadProcess(200, 8, 0, code);
        });
    }
}
