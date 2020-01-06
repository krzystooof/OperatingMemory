package cpuscheduler;
import java.util.*;

// TODO:
// - aging

/**
 * Class CpuScheduler represents a simulator of cpu scheduler
 * based on  preemptive priority algorithm.
 *
 * @author  Olek
 * @version 1.0
 * @since   01-12-2019
 */
public class CpuScheduler {

    // semaphores.Process in Running state
    private Pcb runningPcb;


    //List of processes in Ready state
    private PriorityQueue<Pcb> readyPcb;


    //List of processes in Waiting state
    private PriorityQueue<Pcb> waitingPcb;

    /**
     *  Method change state of process to Running
     * @param pcb process
     */
    private void runProcess(Pcb pcb){
        pcb.state = State.RUNNING;
        this.runningPcb = pcb;
    }


    /**
     * Method finds next process which will be execute in the near future
     *
     * @return next process which will be execute in the near future
     */
    private Pcb findNextProccess(){
        Pcb pcb = readyPcb.poll();
        return pcb;
    }

    /**
     *  Public constructor of CpuScheduler class
     */
    public CpuScheduler(){
        waitingPcb = new PriorityQueue<>(new PcbComparator());
        readyPcb = new PriorityQueue<>(new PcbComparator());
        runProcess(new Pcb(0,0, State.NEW, "idle thread"));
    }

    /**
     * Method adds process to list of readyPcb
     * @param pcb object
     * @return true if process is valid otherwise false
     *
     * */
    public boolean addProcess(Pcb pcb){

        // Checks if priority is valid
        if (pcb.priority > 31 && pcb.priority < 0){
            return false;
        }

        if(pcb.priority > this.runningPcb.priority){
            if(runningPcb.priority != 0){
                runningPcb.state = State.READY;
                readyPcb.add(runningPcb);
            }
            runProcess(pcb);
        }
        else {
            readyPcb.add(pcb);
        }

        return true;
    }

    /**
     *  Method removes process from list of readyPcb
     * @param  pid int
     * @return true if process is in a list otherwise false
     *
     */
    public boolean removeProcess(int pid){

        if(getRunningPcb().pid == pid){
            runProcess(findNextProccess());
            return true;
        }
        else {
            for (Pcb pcb : readyPcb) {
                if (pcb.pid == pid) {
                    readyPcb.remove(pcb);
                    return true;
                }

            }
        }

        return false;
    }
    /**
     * Method returns list of readyPcb
     * @return List of ready PCB if the list is empty return null
     */
    public PriorityQueue<Pcb> getReadyPcb() {
        if(readyPcb.isEmpty()){
            return null;
        }
        return readyPcb;
    }

    /**
     * Method returns list of readyPcb
     * @return List of ready PCB if the list is empty return null
     */
    public PriorityQueue<Pcb> getWaitingPcb() {
        if(waitingPcb.isEmpty()){
            return null;
        }
        return waitingPcb;
    }


    /**
     * Method return process which has Running state,
     * if ready list is empty, returns process with priority 0 - Idle.
     * @return PCB which has state Running
     */
    public Pcb getRunningPcb() {

        if (readyPcb.isEmpty() && runningPcb==null){
            return new Pcb(0,0, State.RUNNING, "Idle process");
        }

        return runningPcb;
    }

    /**
     * Method tells Cpu to find next process to be executed.
     * @return true if ready list is not empty, otherwise false
     */
    public boolean nextProcess(){
        if(readyPcb.isEmpty()){
            return false;
        }
        runProcess(findNextProccess());
        return true;
    }
}

/**
 * Comparator for comparing different Pcb objects
 */
class PcbComparator implements Comparator<Pcb>{

    @Override
    public int compare(Pcb o1, Pcb o2) {
        return Integer.compare(o2.priority, o1.priority);
    }
}