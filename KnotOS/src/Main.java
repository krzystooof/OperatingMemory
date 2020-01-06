import cpuscheduler.CpuScheduler;
import cpuscheduler.Pcb;
import cpuscheduler.State;
import Shell.Interface;
import memory.virtual.VirtualMemory;

public class Main {
    VirtualMemory memory = new VirtualMemory(1024,256);
    public static void main(String[] args) {
        Interface.start();
    }
}
