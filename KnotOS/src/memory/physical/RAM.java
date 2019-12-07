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

    public byte getByte(int adress) {
        return memory[adress];
    }

    public void saveByte(int adress, byte data) {
        memory[adress] = data;
    }
}
