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
    private PCB runningPCB;


    //List of processes in Ready state
    private PriorityQueue<PCB> readyPCB;


    //List of processes in Waiting state
    private PriorityQueue<PCB> waitingPCB;

    /**
     *  Method change state of process to Running
     * @param pcb process
     */
    private void runProcess(PCB pcb){
        pcb.state = State.RUNNING;
        this.runningPCB = pcb;
    }


    /**
     * Method finds next process which will be execute in the near future
     *
     * @return next process which will be execute in the near future
     */
    private PCB findNextProccess(){
        PCB pcb = readyPCB.poll();
        return pcb;
    }

    /**
     *  Public constructor of CpuScheduler class
     */
    public CpuScheduler(){
        waitingPCB = new PriorityQueue<>(new PcbComparator());
        readyPCB = new PriorityQueue<>(new PcbComparator());
        runProcess(new PCB(0,0, State.NEW, "idle thread"));
    }

    /**
     * Method adds process to list of readyPcb
     * @param pcb object
     * @return true if process is valid otherwise false
     *
     * */
    public boolean addProcess(PCB pcb){

        // Checks if priority is valid
        if (pcb.PRIORITY > 31 && pcb.PRIORITY < 0){
            // TODO:
            // Condition 'pcb.priority > 31 && pcb.priority < 0' is always 'false'
            return false;
        }

        if(pcb.PRIORITY > this.runningPCB.PRIORITY){
            if(runningPCB.PRIORITY != 0){
                runningPCB.state = State.READY;
                readyPCB.add(runningPCB);
            }
            runProcess(pcb);
        }
        else {
            readyPCB.add(pcb);
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

        if(getRunningPCB().PID == pid){
            runProcess(findNextProccess());
            return true;
        }
        else {
            for (PCB pcb : readyPCB) {
                if (pcb.PID == pid) {
                    readyPCB.remove(pcb);
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
    public PriorityQueue<PCB> getReadyPCB() {
        if(readyPCB.isEmpty()){
            return null;
        }
        return readyPCB;
    }

    /**
     * Method returns list of readyPcb
     * @return List of ready PCB if the list is empty return null
     */
    public PriorityQueue<PCB> getWaitingPCB() {
        if(waitingPCB.isEmpty()){
            return null;
        }
        return waitingPCB;
    }


    /**
     * Method return process which has Running state,
     * if ready list is empty, returns process with priority 0 - Idle.
     * @return PCB which has state Running
     */
    public PCB getRunningPCB() {

        if (readyPCB.isEmpty() && runningPCB ==null){
            return new PCB(0,0, State.RUNNING, "Idle process");
        }

        return runningPCB;
    }

    /**
     * Method tells Cpu to find next process to be executed.
     * @return true if ready list is not empty, otherwise false
     */
    public boolean nextProcess(){
        if(readyPCB.isEmpty()){
            return false;
        }
        runProcess(findNextProccess());
        return true;
    }
}

/**
 * Comparator for comparing different Pcb objects
 */
class PcbComparator implements Comparator<PCB>{

    @Override
    public int compare(PCB o1, PCB o2) {
        return Integer.compare(o2.PRIORITY, o1.PRIORITY);
    }
}