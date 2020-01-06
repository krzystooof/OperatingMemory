
public class main {

	public static void main(String[] args) throws InterruptedException  {
		Semaphores semafor= new Semaphores();
		System.out.println(semafor.value);
		semafor.signalSem(semafor);
		System.out.println(semafor.value);
		semafor.waitSem(semafor);
		System.out.println(semafor.value);
		semafor.waitSem(semafor);
		System.out.println(semafor.value);
		semafor.waitSem(semafor);
		System.out.println(semafor.queue.size());
		semafor.signalSem(semafor);
		System.out.println(semafor.queue.size());
		
	}

}
