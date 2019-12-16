import java.io.*; 
import java.util.*;

public class Semaphores {
    public int value = 0; 
    Queue<Process> queue = new ArrayDeque<Process>(); //Queue of waiting processes
    Semaphores(int value){
        this.value=value;
    }
}
//Checking if memory is empty
public void wait(Semaphores semaphore){ 
    semaphore.value--;
	if(semaphore.value<0){
		semaphore.queue.add(Process);
		block();
    }

}
//Changes ProcessState to waiting
public void block(){
    if(ProcessState=Ready)
    {
    	ProcessState=Waiting;
    	changes=-1;
    }
}
//Frees up memory space
public void signal(Semaphores semaphore){ 
    semaphore.value++;
    if(semaphore.value<=0){
        semaphore.queue.remove(Process);
        wakeup(Process);
    }
}
//Changes ProcessState to Ready
public void wakeup(Process){
	if(ProcessState=Waiting)
	{
		ProcessState=Ready;
		changes=1;
	}
	
}
    

