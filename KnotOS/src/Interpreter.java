import cpuscheduler.*;

import java.io.*;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Vector;
import java.lang.String;

public class Interpreter
{
    private HashMap<Integer, String> instructionMap = new HashMap<Integer, String>();
    private Vector<String> line = new Vector<String>();
    public byte[] data;
    Pcb process;
    public Interpreter(File file)
    {
        instructionMap.put(1,"ADD");
        instructionMap.put(2,"SUB");
        instructionMap.put(3,"MUL");
        instructionMap.put(4,"INC");
        instructionMap.put(5,"DEC");
        instructionMap.put(6,"MOV");
        instructionMap.put(7,"MVI");
        instructionMap.put(8,"RES");
        instructionMap.put(9,"JMP");
        instructionMap.put(10,"JAXZ");
        instructionMap.put(11,"JIZ");
        instructionMap.put(12,"JINZ");
        instructionMap.put(13,"CP");
        instructionMap.put(14,"DP");
    }
    void runInterpreter(Pcb process, File file)
    {

        line=fileToLines(file);
        char space = ' ';
        char semicolon = ';';
        String instruction="";
        int totalBytes=1;
        Vector<String> Instructions = new Vector<String>();
        for(String word : line)
        {
            int i=0;
            while(word.charAt(i) != space)
            {
                if(word.charAt(i) != semicolon)
                {
                    instruction += word.charAt(i);
                    i++;
                }
                else
                    break;
            };
            Instructions.add(instruction);
            instruction="";
            totalBytes++;
        }
        System.out.println("Total bytes: " + totalBytes);
        data = new byte[totalBytes];
        byte singleByte = 0;
        int j=0;
        for(String singleInstruction : Instructions)
         {
             for(HashMap.Entry<Integer,String> entry : instructionMap.entrySet())
             {
                 if (entry.getValue().equals(singleInstruction))
                 {
                     j++;
                     singleByte = entry.getKey().byteValue();
                     data[j] = singleByte;
                 }
             }
         }
        System.out.println("Tablica bajtów: ");
        for (byte a : data)
            System.out.println(a);




    }

    private Vector<String> fileToLines(File file)
    {
        Vector<String> lines = new Vector<String>();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            String text = null;

            while ((text = reader.readLine()) != null)
            {
                lines.add(text);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
            }
        }
        return lines;
    }
    int counterStatus()
    {
    return process.programCounter;
    }
    private void instructionExecute(String instruction)
    {
        switch(instruction)
        {
            case "ADD":
                break;
            case "SUB":
                break;
            case "MUL":
                break;
        }

    }
    public byte[] readBytes()
    {
    return data;
    }
}
