package semaphores;

public class Process {
    public ProcessState state;
    public Process(){
        this.state= ProcessState.Ready;
    }
}
