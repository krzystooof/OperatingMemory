package memory.physical;

public class RAM {
    private byte[] memory;

    public RAM() {
        memory = new byte[128];
    }

    public byte getByte(int adress) {
        return memory[adress];
    }

    public void saveByte(int adress, byte data) {
        memory[adress] = data;
    }
}
