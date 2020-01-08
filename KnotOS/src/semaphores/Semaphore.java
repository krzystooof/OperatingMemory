package semaphores;

import cpuscheduler.State;
import cpuscheduler.PCB;

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
    Queue<PCB> queue = new ArrayDeque<PCB>();

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
    private void block(PCB process) {
        process.state = State.WAITING;
        changes.changes = -1; // process is blocked

    }

    /**
     * Decrease value of semaphore
     *
     * @param process object
     */
    public void waitSem(PCB process) {
        value--;
        if (value < 0) {
            queue.add(process);
            block(process);
        }

    }

    /**
     * Change semaphores.ProcessState to Ready from Waiting
     *
     * @param process object
     */
    private void wakeUp(PCB process) {
        process.state = State.READY;
    }

    /**
     * Increase value of semaphore
     *
     * @param process object
     */
    public void signalSem(PCB process) {
         value++;
        if (value <= 0) {
            queue.remove(process);
            wakeUp(process);
        }
    }


}
