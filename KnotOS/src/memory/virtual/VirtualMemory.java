package memory.virtual;

import java.util.HashMap;

public class VirtualMemory {
    private HashMap processMap = new HashMap<Integer, Integer[]>();
    private HashMap segmentMap = new HashMap<Integer, Boolean>();
    private SegmentRecord[] segmentTable;

    private byte[] swapFile = new byte[1024];
    private int[] segmentQueue;
    // pointer for RAM needed

    /**
     * Swaps segment from swap file to RAM.
     */
    private void swapToRam(int segmentId) {
    }

    /**
     * Swaps segment from RAM to swap file.
     */
    private void swapToDisc(int segmentId) {
    }

    /**
     * Checks segment table, and informs RAM if CPU call is valid.lic boolean validMemoryCall(int segment, int offset) {
        return false;
    }

    /**
     * Representation of segment for PCB.
     *
     * @param assemblyProgram Block of assembly instructions.
     */
    public void loadProcess(int textLength, int dataLength, byte[] assemblyProgram) {

    }

    /**
     * Removes program from memory.
     */
    public void flushProcess(SegmentRecord[] data) {
    }

    /**
     * Reads memory cell.
     *
     * @return byte from RAM.
     */
    public byte readMemory(int id, int offset) {
        return 0;
    }

    /**
     * Overwrites memory cell.
     */
    public void writeMemory(int segment, int offset, byte data) {
    }
}




