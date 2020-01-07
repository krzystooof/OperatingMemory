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


public class Semaphores {
    //value of Semaphore
    public int value = 0;

    //Queue of waiting processes
    Queue<Process> queue = new ArrayDeque<Process>();

    //semaphores.Process object
    Process Process = new Process();

    //Checking if warning happened in system
    SemaphoreChange changes = new SemaphoreChange();

    /**
     * Allocate process in memory
     */
    private void block() {
        Process.state = ProcessState.Waiting;
        changes.changes = -1; // process is blocked

    }

    /**
     * Decrease value of semaphore
     *
     * @param semaphore object
     */
    public void waitSem(Semaphores semaphore, int value) {
        semaphore.value -= value;
        if (semaphore.value < 0) {
            semaphore.queue.add(Process);
            block();
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
     * @param semaphore object
     */
    public void signalSem(Semaphores semaphore, int value) {
        semaphore.value += value;
        if (semaphore.value <= 0) {
            semaphore.queue.remove(Process);
            wakeUp(Process);
        }
    }


}
