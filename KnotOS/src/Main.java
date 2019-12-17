import java.io.File;
import java.util.Scanner;
import cpuscheduler.*;

import static cpuscheduler.State.NEW;
//1. jcob przekazuje plik tekstowy a ja go musze przerobic na arraya bajtowego
// w byte.  Command = File. map rozkazów np. kod rozkazu A, a wartość MOV.
public class Main
{
    public static void main(String[] args)
    {
        /*
        Scanner input = new Scanner(System.in);
        System.out.print("Podaj liczbe ");
        int liczba = input.nextInt();

        System.out.print(First.toString());
        System.out.print(liczba);*/

        Pcb First = new Pcb(1,2,NEW, "Pierwszy");
        File file = new File("asm.txt");
        Interpreter Asm = new Interpreter(file);
        Asm.runInterpreter(First,file);
    }
}
