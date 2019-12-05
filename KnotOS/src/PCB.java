
public class PCB 
{
 int PID;
 int CNT_STATUS;
 Registers REGS;
 String Command;
	 PCB(int PID, String Command)
	 {
		 this.PID=PID;
		 this.CNT_STATUS=0;
		 this.REGS.reg_AX=0;
		 this.REGS.reg_BX=0;
		 this.REGS.reg_CX=0;
		 this.REGS.reg_DX=0;
		 this.Command=Command;
	 }
	 PCB()
	 {
		 
	 }
}
