public class Pcb {

    //TODO:
    // Registers
    // counter
    // etc.
    private int pid;
    private int priority;
    private ProcessState state;
    private String name;

    public Pcb(int pid, int priority, ProcessState state, String name) {
        this.pid = pid;
        this.priority = priority;
        this.state = state;
        this.name = name;
    }

    public int getPid() {
        return pid;
    }

    public int getPriority() {
        return priority;
    }

    public ProcessState getState() {
        return state;
    }

    public void setState(ProcessState state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Pcb{" +
                "pid=" + pid +
                ", priority=" + priority +
                ", state=" + state +
                ", name='" + name + '\'' +
                '}' + "\n";
    }
}
