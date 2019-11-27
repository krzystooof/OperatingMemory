import java.io.File;

public class VirtualMemory {
    private SegmentRecord[] segmentTable;
    private byte[] swapFile = new byte[512];
    private int[] segmentQueue;

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
     * @param assemblyProgram Block of assembly instructions, not necessarily File.
     * @return Array of numbers and offsets.
     */
    public SegmentRecord[] loadProcess(int textLength, int dataLength, File assemblyProgram) {
        return new SegmentRecord[1];
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




