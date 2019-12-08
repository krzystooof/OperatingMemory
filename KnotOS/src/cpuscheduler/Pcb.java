public class Pcb {

    private int pid;
    private int priority;
    private ProcessState state;
    private String name;
    private int programCounter;
    public Registers registers;

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

    public int getProgramCounter() {
        return programCounter;
    }

    public void setProgramCounter(int programCounter) {
        this.programCounter = programCounter;
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
