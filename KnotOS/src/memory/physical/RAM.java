package memory.physical;

public class RAM {
    private byte[] memory;
    private int ramSize=128;

    public RAM() {
        memory = new byte[ramSize];
    }
    public RAM(int ramSize){
        memory = new byte[ramSize];
    }

    protected byte getByte(int adress) {
        return memory[adress];
    }

    protected void saveByte(int adress, byte data) {
        memory[adress] = data;
    }
    protected byte[] getBytes(int startIndex, int stopIndex) {
        byte[] result = new byte[stopIndex - startIndex + 1];
        for (int i = 0; i < result.length; i++) {
            result[i] = getByte(startIndex);
            startIndex++;
        }
        return result;
    }
}
