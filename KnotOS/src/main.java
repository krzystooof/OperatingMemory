
public class main {

	public static void main(String[] args) throws InterruptedException  {
		Semaphores semafor= new Semaphores();
		System.out.println(semafor.value);
		semafor.signal(semafor);
		System.out.println(semafor.value);
		semafor.waitt(semafor);
		System.out.println(semafor.value);
		semafor.waitt(semafor);
		System.out.println(semafor.value);
		semafor.waitt(semafor);
		System.out.println(semafor.queue.size());
		semafor.signal(semafor);
		System.out.println(semafor.queue.size());
		
	}

}
