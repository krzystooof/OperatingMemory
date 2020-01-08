package semaphores;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Class Semaphores is used for synchronization
 *
 * @author Grzegorz
 * @version 1.1
 * @since 2019-12-16
 */


public class Semaphore {
    //value of Semaphore
    public int value = 0;

    //Queue of waiting processes
    Queue<Process> queue = new ArrayDeque<Process>();

    //Checking if warning happened in system
    SemaphoreChange changes = new SemaphoreChange();

    public Semaphore(){
        this.value = 0;
    }

    public Semaphore(int value){
        this.value = value;
    }

    /**
     * Allocate process in memory
     */
    private void block(Process process) {
        process.state = ProcessState.Waiting;
        changes.changes = -1; // process is blocked

    }

    /**
     * Decrease value of semaphore
     *
     * @param value value to decrease
     */
    public void waitSem(Process process, int value) {
        this.value -= value;
        if (this.value < 0) {
            this.queue.add(process);
            block(process);
        }

    }

    /**
     * Change semaphores.ProcessState to Ready from Waiting
     *
     * @param Process object
     */
    private void wakeUp(Process Process) {
        Process.state = ProcessState.Ready;
        changes.changes = 1;

    }

    /**
     * Increase value of semaphore
     *
     * @param value to increase
     */
    public void signalSem(Process process, int value) {
        this.value += value;
        if (this.value <= 0) {
            this.queue.remove(process);
            wakeUp(process);
        }
    }


}
