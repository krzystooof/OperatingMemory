package shell;

import cpuscheduler.CpuScheduler;
import cpuscheduler.PCB;
import memory.virtual.VirtualMemory;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class TaskList implements Shell{
    CpuScheduler cpuScheduler;
    VirtualMemory memory;
    ArrayList<PCB> PCBs;
    ArrayList<String> shellCommands;

    TaskList() {
        shellCommands = new ArrayList<>();
        cpuScheduler = Process.getCpuScheduler();
        memory = Interface.getMemory();
        shellCommands.add("tasklist");
        shellCommands.add("memread");
        shellCommands.add("swap");
    }

    private void runTaskList() {
        PCBs = new ArrayList<PCB>();
        PriorityQueue<PCB> queue = cpuScheduler.getReadyPCB();
        PriorityQueue<PCB> waitingQueue = cpuScheduler.getWaitingPCB();

        if (queue != null) {
            if (queue.size() != 0) {
                for (PCB single : queue) {
                    PCBs.add(single);
                }
            }
        }

        if(waitingQueue != null){
            if (waitingQueue.size() != 0) {
                for (PCB single : waitingQueue) {
                    PCBs.add(single);
                }
            }
        }
        PCB first = cpuScheduler.getRunningPCB();
        PCBs.add(0,first);
        display();
        System.out.print("\n");
    }

    private void displayByteArray(byte[] processMemory) {
        int i = 1;
        for (byte singleByte : processMemory) {
            System.out.print(singleByte);
            if (i < 10) {
                if (singleByte < 10) System.out.print("      ");
                else {
                    if (singleByte < 100) System.out.print("     ");
                    else System.out.print("    ");
                }
            } else {
                System.out.print("\n");
                i = 0;
            }
            i++;
        }
        System.out.println();
    }

    private void display() {
        printHeader();
        for (PCB current : PCBs) {
            String memUsageString = null;
            try {
                byte[] memUsage;
                memUsage = memory.getProcessMemory(current.PID);
                memUsageString = Integer.toString(memUsage.length);
                memUsageString = memUsageString + " B";
            } catch (NullPointerException e) {
                memUsageString = "Not found";
            }
            printFill(current.NAME, 25);
            System.out.print(" ");
            printFill(Integer.toString(current.PID), 8);
            System.out.print(" ");
            printFill(current.state.toString(), 16);
            System.out.print(" ");
            printFill(Integer.toString(current.PRIORITY), 11);
            System.out.print(" ");
            printFill(memUsageString, 12);
            System.out.print("\n");
        }
    }

    private void printHeader(){
        printFill("Image name", 25);
        System.out.print(" ");
        printFill("PID", 8);
        System.out.print(" ");
        printFill("State", 16);
        System.out.print(" ");
        printFill("Priority", 11);
        System.out.print(" ");
        printFill("Mem usage", 12);
        System.out.print("\n");
        printLoop("=", 25);
        System.out.print(" ");
        printLoop("=", 8);
        System.out.print(" ");
        printLoop("=", 16);
        System.out.print(" ");
        printLoop("=", 11);
        System.out.print(" ");
        printLoop("=", 12);
        System.out.print("\n");
    }

    private void printFill(String toPrint, int characters) {
        if (toPrint.length() > characters) {
            toPrint = toPrint.substring(0,(characters-4));
            toPrint = toPrint + "...";
        }
        System.out.print(toPrint);
        for (int i = 0; i < (characters- toPrint.length()); i++) System.out.print(" ");
    }

    private void printLoop(String toPrint, int times) {
        for (int i = 0; i != times; i++) {
            System.out.print(toPrint);
        }
    }

    @Override
    public ArrayList<String> getShellCommands() {
        return shellCommands;
    }

    @Override
    public void pass(ArrayList<String> params) {
        switch (params.get(0)) {
            case "tasklist": {
                runTaskList();
                break;
            }
            case "memread": {
                params.remove(0);
                memread(params);
                break;
            }
            case "swap": {
                params.remove(0);
                swap(params);
                break;
            }
        }
    }

    private void swap(ArrayList<String> params) {
        if (params.size() > 1) {
            try {
                switch (params.get(0)) {
                    case "file": {
                        memory.swapToFile(Integer.parseInt(params.get(1)));
                        break;
                    }
                    case "ram": {
                        memory.swapToRam(Integer.parseInt(params.get(1)));
                        break;
                    }
                }
            } catch (NumberFormatException e) {
                Interface.post("ID must be a number");
            }
        } else {
            Interface.post("Too few arguments");
        }
    }

    @Override
    public void getHelp() {
        System.out.println("Help in regard to process and memory diagnostics:\n" +
                "tasklist\n" +
                "memread <PID>\n" +
                "memread -virtual\n" +
                "memread -physical\n" +
                "memread -segment" +
                "swap file <ID>\n" +
                "swap ram <ID>\n");

    }

    @Override
    public String getName() {
        return "Process and memory diagnostics";
    }

    private void memread(ArrayList<String> params) {
        if (params.size() > 0) {
            switch (params.get(0)) {
                case "virtual": {
                    System.out.println("Free memory: " + memory.getSpaceLeft(false, true));
                    displayByteArray(memory.getMemory(false,true));
                    break;
                }
                case "physical": {
                    System.out.println("Free memory: " + memory.getSpaceLeft(true, false));
                    displayByteArray(memory.getMemory(true,false));
                    break;
                }
                case "segment": {
                    memory.showSegmentTable();
                    break;
                }
                default: {
                    try {
                        String PID = params.get(0);
                        byte[] processMemory = memory.getProcessMemory(Integer.parseInt(PID));
                        displayByteArray(processMemory);
                    } catch (NullPointerException e) {
                        Interface.post("Process not found in memory");
                    } catch (NumberFormatException e) {
                        Interface.post("Incorrect PID");
                    }
                    break;
                }
            }
        } else Interface.post("Too few arguments");
    }
}
