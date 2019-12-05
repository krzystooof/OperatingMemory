
public class Interpreter 
{
	PCB Process;
	void Create_Process(int PID,String Command)
	{
		Process.PID=PID;
		Process.Command=Command;
	}
	
	int Counter_Status()
	{
		return Process.CNT_STATUS;
	}
	
	@SuppressWarnings("unused")
	private void Instruction_Execute(String Command)
	{
		
	}
	void Read_Bytes()
	{
		
	}
}
