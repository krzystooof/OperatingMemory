package cpuscheduler;

public class Pcb {

    public int pid;
    public int priority;
    public State state;
    public String name;
    public int programCounter;
    public Registers registers;

    public Pcb(int pid, int priority, State state, String name) {
        this.pid = pid;
        this.priority = priority;
        this.state = state;
        this.name = name;
    }


    @Override
    public String toString() {
        return "Pcb{" +
                "pid=" + pid +
                ", priority=" + priority +
                ", state=" + state +
                ", name='" + name + '\'' +
                ", programCounter=" + programCounter +
                ", registers=" + registers +
                '}';
    }
}
