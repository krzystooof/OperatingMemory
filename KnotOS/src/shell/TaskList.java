package shell;

import cpuscheduler.CpuScheduler;
import cpuscheduler.PCB;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class TaskList {
    CpuScheduler cpuScheduler; // TODO make constructor take scheduler as argument
    //TODO Instance of virtual memory needed here
    ArrayList<PCB> PCBs;

    public TaskList() {
        PCBs = new ArrayList<PCB>();
        cpuScheduler = new CpuScheduler();
        PriorityQueue<PCB> queue= cpuScheduler.getReadyPCB();
        for (PCB single : queue) {
            PCBs.add(single);
        }
        PCB first = cpuScheduler.getRunningPCB();
        PCBs.add(0,first);
        display();
    }

    private void display() {
        printHeader();
        for (PCB current : PCBs) {
            printFill(current.NAME, 25);
            System.out.print(" ");
            printFill(Integer.toString(current.PID), 8);
            System.out.print(" ");
            printFill(current.state.toString(), 16);
            System.out.print(" ");
            printFill(Integer.toString(current.PRIORITY), 11);
            System.out.print(" ");
            printFill("Unknown", 12);
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
        printLoop("=", 212);
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
            System.out.print("");
        }
    }
}
