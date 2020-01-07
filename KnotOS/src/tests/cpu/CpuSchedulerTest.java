package tests.cpu;

import cpuscheduler.CpuScheduler;
import cpuscheduler.PCB;
import cpuscheduler.State;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class CpuSchedulerTest {

    @Test
    public void testReadyList() {
        CpuScheduler cpuScheduler = new CpuScheduler();
        PCB first = new PCB(1, 1, State.NEW, "First");
        PCB second = new PCB(2, 2, State.NEW, "Second");
        PCB third = new PCB(3, 3, State.NEW, "Third");

        cpuScheduler.addProcess(first);
        cpuScheduler.addProcess(second);
        cpuScheduler.addProcess(third);

        List<PCB> items = new ArrayList<>();
        items.add(second);
        items.add(first);

        PCB[] itemsArray = new PCB[2];
        itemsArray = items.toArray(itemsArray);

        Queue<PCB> readyList = cpuScheduler.getReadyPCB();
        Object [] readyListArray = readyList.toArray();

        assertArrayEquals(readyListArray, itemsArray);

    }

    @Test
    public void testRemoveProcess() {
        CpuScheduler cpuScheduler = new CpuScheduler();
        PCB first = new PCB(1, 1, State.NEW, "First");
        PCB second = new PCB(2, 2, State.NEW, "Second");
        PCB third = new PCB(3, 3, State.NEW, "Third");

        cpuScheduler.addProcess(first);
        cpuScheduler.addProcess(second);
        cpuScheduler.addProcess(third);

        assertTrue(cpuScheduler.removeProcess("Third"));
        assertEquals("Second", cpuScheduler.getRunningPCB().NAME);

    }



    @Test
    public void testGetRunningPcb() {
        CpuScheduler cpuScheduler = new CpuScheduler();
        PCB first = new PCB(1, 1, State.NEW, "First");
        PCB second = new PCB(2, 2, State.NEW, "Second");
        PCB third = new PCB(3, 3, State.NEW, "Third");

        cpuScheduler.addProcess(third);
        assertEquals(cpuScheduler.getRunningPCB().NAME, "Third");

    }

    @Test
    public void testNextProcess() {
        CpuScheduler cpuScheduler = new CpuScheduler();
        PCB first = new PCB(1, 1, State.NEW, "First");
        PCB second = new PCB(2, 2, State.NEW, "Second");
        PCB third = new PCB(3, 3, State.NEW, "Third");

        cpuScheduler.addProcess(first);
        cpuScheduler.addProcess(third);
        cpuScheduler.addProcess(second);

        cpuScheduler.nextProcess();
        assertEquals("Second", cpuScheduler.getRunningPCB().NAME);
    }


}