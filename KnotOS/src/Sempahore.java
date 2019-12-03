import java.io.*; 
import java.util.*;

public class Semaphore{
    public int value;
    Queue<*zmienna procesu*> queue = new ArrayDeque<*zmienna procesu*>();
    Semaphore(int value){
        this.value=value;
    }
}

public void wait(Sempahore semaphore){
    semaphore.value--;
    if(sempahore.value<0){
        queue.add(P);
        block();
    }

}

public void block(){
    if(wws[i]){  // wait for Wakeup flag associated with process i
        Block process i
    }
    else{
        wws[i]=false;
    }
}

---------------------------------------------------

public void signal(Sempahore semaphore){
    semaphore.value++;
    if(semaphore.value<=0){
        queue.remove(P);
        wakeup(P);
    }
}

public void wakeup(Process P){
    if ready(i)     //process is ready
        wws[i]=true;
    else
        Activate proecess i;

}

P - process