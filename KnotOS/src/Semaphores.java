import java.io.*;
import java.util.*;

//block,waitt,wakeup,signal - Tested

 
public class Semaphores {
  public int value = 0;
  Queue<Process> queue = new ArrayDeque<Process>(); //Queue of waiting processes
  Process Process = new Process();
  GlobalVariable changes = new GlobalVariable();
  /*
  Semaphores(int value){
      this.value=value;
  }
  */
 
 
  //Changes ProcessState to waiting
  private void block(){
        Process.state=ProcessState.Waiting;
        changes.changes=-1; // process is blocked
       
  }
   
  //Checking if memory is empty
  public void waitt(Semaphores semaphore){
     semaphore.value--;
        if(semaphore.value<0){
        semaphore.queue.add(Process);
            block();
        }
 
    }
 
  //Changes ProcessState to Ready
    private void wakeup(Process Process){
        Process.state=ProcessState.Ready;
        changes.changes=1;
       
    }
 
    //Frees up memory space
    public void signal(Semaphores semaphore){
        semaphore.value++;
        if(semaphore.value<=0){
            semaphore.queue.remove(Process);
            wakeup(Process);
        }
    }
   
   
}
