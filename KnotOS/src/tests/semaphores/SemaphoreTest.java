//package tests.semaphores;
//
//import org.junit.jupiter.api.Test;
//import semaphores.Process;
//import semaphores.ProcessState;
//import semaphores.Semaphore;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//class SemaphoreTest {
//
//    @Test
//    void waitSem() {
//        Semaphore semaphore = new Semaphore(20);
//        Process process= new Process();
//        semaphore.waitSem(process,20);
//        assertEquals(0,semaphore.value);
//    }
//
//    @Test
//    void signalSem() {
//        Semaphore semaphore = new Semaphore(0);
//        Process process= new Process();
//        semaphore.signalSem(process,20);
//        assertEquals(20,semaphore.value);
//    }
//    @Test
//    void block(){
//        Process process = new Process();
//        Semaphore semaphore = new Semaphore(1);
//        semaphore.waitSem(process,2);
//        assertEquals(process.state,ProcessState.Waiting);
//    }
//    @Test
//    void wakeUp(){
//        Process process = new Process();
//        process.state = ProcessState.Waiting;
//        Semaphore semaphore = new Semaphore(-1);
//        semaphore.signalSem(process,1);
//        assertEquals(process.state,ProcessState.Ready);
//    }
//    @Test
//    void queueTest(){
//        Process process1 = new Process();
//        Process process2 = new Process();
//        Semaphore semaphore = new Semaphore(0);
//        semaphore.waitSem(process1,1);
//        semaphore.waitSem(process2,1);
//        assertEquals(process1,semaphore.queue.remove());
//        assertEquals(process2,semaphore.queue.remove());
//    }
//    @Test
//    void queueTest2(){
//        Process process1 = new Process();
//        Semaphore semaphore = new Semaphore(0);
//        semaphore.waitSem(process1,1);
//        semaphore.signalSem(process1,1);
//        assertEquals(0,semaphore.queue.size());
//    }
//}