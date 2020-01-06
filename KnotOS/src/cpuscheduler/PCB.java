package cpuscheduler;

/**
 * Class Pcb represents semaphores.Process Control Block
 */
public class PCB {

    public int PID;
    public int PRIORITY;
    public String NAME;
    public State state;
    public int programCounter;
    public Registers registers;

    public PCB(int PID, int PRIORITY, State state, String NAME) {
        this.PID = PID;
        this.PRIORITY = PRIORITY;
        this.NAME = NAME;
        this.state = state;
    }


    @Override
    public String toString() {
        return "Pcb{" +
                "pid=" + PID +
                ", priority=" + PRIORITY +
                ", state=" + state +
                ", name='" + NAME + '\'' +
                ", programCounter=" + programCounter +
                ", registers=" + registers +
                '}';
    }
}
