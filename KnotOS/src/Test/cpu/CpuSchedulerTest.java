package Test.cpu;

import cpuscheduler.CpuScheduler;
import cpuscheduler.Pcb;
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
        Pcb first = new Pcb(1, 1, State.NEW, "First");
        Pcb second = new Pcb(2, 2, State.NEW, "Second");
        Pcb third = new Pcb(3, 3, State.NEW, "Third");

        cpuScheduler.addProcess(first);
        cpuScheduler.addProcess(second);
        cpuScheduler.addProcess(third);

        List<Pcb> items = new ArrayList<>();
        items.add(second);
        items.add(first);

        Pcb [] itemsArray = new Pcb[2];
        itemsArray = items.toArray(itemsArray);

        Queue<Pcb> readyList = cpuScheduler.getReadyPcb();
        Object [] readyListArray = readyList.toArray();

        assertArrayEquals(readyListArray, itemsArray);

    }

    @Test
    public void testRemoveProcess() {
        CpuScheduler cpuScheduler = new CpuScheduler();
        Pcb first = new Pcb(1, 1, State.NEW, "First");
        Pcb second = new Pcb(2, 2, State.NEW, "Second");
        Pcb third = new Pcb(3, 3, State.NEW, "Third");

        cpuScheduler.addProcess(first);
        cpuScheduler.addProcess(second);
        cpuScheduler.addProcess(third);

        assertTrue(cpuScheduler.removeProcess(3));
        assertEquals(cpuScheduler.getRunningPcb().name, "Second");

    }



    @Test
    public void testGetRunningPcb() {
        CpuScheduler cpuScheduler = new CpuScheduler();
        Pcb first = new Pcb(1, 1, State.NEW, "First");
        Pcb second = new Pcb(2, 2, State.NEW, "Second");
        Pcb third = new Pcb(3, 3, State.NEW, "Third");

        cpuScheduler.addProcess(third);
        assertEquals(cpuScheduler.getRunningPcb().name, "Third");

    }

    @Test
    public void testNextProcess() {
        CpuScheduler cpuScheduler = new CpuScheduler();
        Pcb first = new Pcb(1, 1, State.NEW, "First");
        Pcb second = new Pcb(2, 2, State.NEW, "Second");
        Pcb third = new Pcb(3, 3, State.NEW, "Third");

        cpuScheduler.addProcess(first);
        cpuScheduler.addProcess(third);
        cpuScheduler.addProcess(second);

        cpuScheduler.nextProcess();
        assertEquals("Second", cpuScheduler.getRunningPcb().name);
    }


}