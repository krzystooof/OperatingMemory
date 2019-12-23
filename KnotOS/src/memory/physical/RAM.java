/**
 * <h1>KnotOS PhysicalMemory</h1>
 * @author Krzysztof Greczka
 * @since 12.2019
 * This code is a project for Operating Systems 2019 subject.
 * <p>
 * Imitation of RandomAccessMemory
 */
package memory.physical;

public class RAM {
    private byte[] memory;
    private int ramSize = 128;

    public RAM() {
        memory = new byte[ramSize];
    }
    /**
     * Creates memory with given size
     **/
    public RAM(int ramSize) {
        memory = new byte[ramSize];
    }

    /**
     * Read byte from memory
     * @param adress index of wanted byte in memory (byte table)
     * @return wanted byte
     */
    protected byte getByte(int adress) {
        return memory[adress];
    }

    /**
     * Save given byte to memory
     * @param adress index of given byte in memory (byte table)
     * @param data given byte
     */
    protected void saveByte(int adress, byte data) {
        memory[adress] = data;
    }
    /**
     * Save given table of bytes to memory
     * @param address first index of given bytes in memory (byte table)
     * @param data given byte
     */
    protected void saveByte(int address,byte[] data){
        for(byte b : data){
            saveByte(address,b);
            address++;
        }
    }

    /**
     * Read part of memory
     * @param startIndex index of first byte in memory (byte table)
     * @param stopIndex index of last byte in memory (byte table)
     * @return byte[] wanted part of memory
     */
    protected byte[] getByte(int startIndex, int stopIndex) {
        byte[] result = new byte[stopIndex - startIndex + 1];
        for (int i = 0; i < result.length; i++) {
            result[i] = getByte(startIndex);
            startIndex++;
        }
        return result;
    }
}

