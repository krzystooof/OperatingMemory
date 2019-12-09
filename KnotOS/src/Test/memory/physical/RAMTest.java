package memory.physical;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RAMTest {

    @Test
    void getAndSaveByte() {
        RAM ram = new RAM();
        byte data = 23;
        ram.saveByte(10,data);
        assertEquals(23,ram.getByte(10));

    }
}