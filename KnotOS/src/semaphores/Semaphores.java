package semaphores;

import java.util.*;

/**
 * Class Semaphores is used for synchronization
 *
 * @author Grzegorz
 * @version 1.1
 * @since 2014-12-16
 */


public class Semaphores {
    //value of Semaphore
    public int value = 0;

    //Queue of waiting processes
    Queue<Process> queue = new ArrayDeque<Process>();

    //semaphores.Process object
    Process Process = new Process();

    //Checking if warning happened in system
    GlobalVariable changes = new GlobalVariable();

    /**
     * Allocates process in memory
     */
    private void block() {
        Process.state = ProcessState.Waiting;
        changes.changes = -1; // process is blocked

    }

    /**
     * Checking if memory is empty
     *
     * @param semaphore object
     */
    public void waitSem(Semaphores semaphore) {
        semaphore.value--;
        if (semaphore.value < 0) {
            semaphore.queue.add(Process);
            block();
        }

    }

    /**
     * Changes semaphores.ProcessState to Ready from Waiting
     *
     * @param Process object
     */
    private void wakeup(Process Process) {
        Process.state = ProcessState.Ready;
        changes.changes = 1;

    }

    /**
     * Frees up memory space
     *
     * @param semaphore object
     */
    public void signalSem(Semaphores semaphore) {
        semaphore.value++;
        if (semaphore.value <= 0) {
            semaphore.queue.remove(Process);
            wakeup(Process);
        }
    }


}
