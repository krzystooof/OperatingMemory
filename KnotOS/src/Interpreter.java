import cpuscheduler.*;

import java.io.*;
import java.util.HashMap;
import java.util.*;
import java.lang.String;

public class Interpreter {
    private HashMap<Integer, String> instructionMap = new HashMap<Integer, String>();
    private Vector<String> lines = new Vector<String>();
    public Vector<Byte> data = new Vector<Byte>();
    Pcb process;
    byte singleByte;

    public Interpreter(File file) {
        //Added mnemonics with machine codes
        instructionMap.put(1, "ADD");
        instructionMap.put(2, "SUB");
        instructionMap.put(3, "MUL");
        instructionMap.put(4, "INC");
        instructionMap.put(5, "DEC");
        instructionMap.put(6, "MOV");
        instructionMap.put(7, "MVI");
        instructionMap.put(8, "RES");
        instructionMap.put(9, "JMP");
        instructionMap.put(10, "JAXZ");
        instructionMap.put(11, "JIZ");
        instructionMap.put(12, "JINZ");
        instructionMap.put(13, "CP");
        instructionMap.put(14, "DP");
        instructionMap.put(19, "HLT");
        //Added registers with machine codes
        instructionMap.put(15, "AX");
        instructionMap.put(16, "BX");
        instructionMap.put(17, "CX");
        instructionMap.put(18, "DX");
    }

    void runInterpreter(File file) {
        Vector<Byte> Bytes = new Vector<Byte>();
        Bytes = getBytesFromFile(file);
        System.out.println("Array byte:");
        for (Byte a : Bytes)
            System.out.println(a);
    }

    byte toByte(String instruction) {
        byte variable = 0;
        for (HashMap.Entry<Integer, String> entry : instructionMap.entrySet()) {
            if (entry.getValue().equals(instruction)) {
                variable = entry.getKey().byteValue();
            }
        }
        return variable;
    }

    public Vector<Byte> getBytesFromFile(File file) {
        lines = fileToLines(file);
        char space = ' ';
        String instruction = "";
        String firstParameter = "";
        String second_parameter = "";
        String t_word = "";
        Vector<Byte> byteInstruction = new Vector<Byte>();
        int i = 0;

        for (String singleLine : lines) {
            //  System.out.println("Printed line: " + singleLine);
            if (singleLine.equals("RES") || singleLine.equals("HLT")) {
                singleByte = toByte(singleLine);
                data.add(singleByte);
                //System.out.println("Machine code: " + singleByte);
            } else if (!singleLine.equals("HLT") || !singleLine.equals("RES")) {
                //Receiving a single command
                while (singleLine.charAt(i) != space) {
                    instruction += singleLine.charAt(i);
                    i++;
                }
                if (instruction.equals("ADD") || instruction.equals("SUB") || instruction.equals("MUL") || instruction.equals("MOV") || instruction.equals("MVI")) {

                    singleByte = toByte(instruction);
                    byteInstruction.add(singleByte);
                    // data.add(singleByte);

                    //Receiving first parameter
                    i = 4;
                    while (singleLine.charAt(i) != space) {
                        firstParameter += singleLine.charAt(i);
                        i++;
                    }
                    singleByte = toByte(firstParameter);
                    byteInstruction.add(singleByte);
                    //data.add(singleByte);
                    i = 7;
                    //Receiving second parameter
                    while (i < singleLine.length()) {
                        second_parameter += singleLine.charAt(i);
                        i++;
                    }
                    //CHECKS LOGICAL ADDRESS
                    if (second_parameter.charAt(0) == '[') {
                        String number = "";
                        String number2 = "";
                        byte value = byteInstruction.get(0);
                        value += (byte) 40;
                        byteInstruction.set(0, value);
                        for (int j = 1; j < second_parameter.length() - 1; j++) {
                            number += second_parameter.charAt(j);
                        }
                        if (number.length() == 1 || number.length() == 2) {
                            byteInstruction.add((byte) 0);
                            byteInstruction.add((byte) Integer.parseInt(number));
                        }
                        if (number.length() == 3) {
                            number2 = "0" + number.charAt(0);
                            byteInstruction.add((byte) Integer.parseInt(number2));
                            byteInstruction.add((byte) Integer.parseInt(number.substring(1, 3)));
                        }
                        if (number.length() == 4) {
                            throw new IllegalArgumentException("Nie ma określonego adresu logicznego!");
                        }
                    }
                    //CHECKS IF SECOND PARAMETER = REGISTER
                    else if (second_parameter.equals("AX") || second_parameter.equals("BX") || second_parameter.equals("CX") || second_parameter.equals("DX")) {
                        byte value = byteInstruction.get(0);
                        value += (byte) 20;
                        byteInstruction.set(0, value);
                        singleByte = toByte(second_parameter);
                        byteInstruction.add(singleByte);
                        byteInstruction.add((byte) 0);
                    } else { //CHECKS IF SECOND PARAMETR IS NUMBER
                        if (second_parameter.length() == 1 || second_parameter.length() == 2) {
                            byteInstruction.add((byte) 0);
                            byteInstruction.add((byte) Integer.parseInt(second_parameter));
                        }
                        if (second_parameter.length() == 3) {
                            byteInstruction.add((byte) Integer.parseInt(second_parameter.substring(0, 1)));
                            byteInstruction.add((byte) Integer.parseInt(second_parameter.substring(1, 3)));
                        }
                        if (second_parameter.length() == 4) {
                            byteInstruction.add((byte) Integer.parseInt(second_parameter.substring(0, 2)));
                            byteInstruction.add((byte) Integer.parseInt(second_parameter.substring(2, 4)));
                        }
                    }
                    firstParameter = "";
                    second_parameter = "";
                    for (byte a : byteInstruction)
                        data.add(a);
                    byteInstruction.clear();
                }
                if (instruction.equals("INC") || instruction.equals("DEC")) {
                    singleByte = toByte(instruction);
                    byteInstruction.add(singleByte);

                    i = 4;
                    while (i < singleLine.length()) {
                        firstParameter += singleLine.charAt(i);
                        i++;
                    }
                    if (firstParameter.charAt(0) == '[') {
                        String number = "";
                        String number2 = "";
                        byte value = byteInstruction.get(0);
                        value += (byte) 40;
                        byteInstruction.set(0, value);
                        for (int j = 1; j < firstParameter.length() - 1; j++) {
                            number += firstParameter.charAt(j);
                        }
                        if (number.length() == 1 || number.length() == 2) {
                            byteInstruction.add((byte) 0);
                            byteInstruction.add((byte) Integer.parseInt(number));
                        }
                        if (number.length() == 3) {
                            number2 = "0" + number.charAt(0);
                            byteInstruction.add((byte) Integer.parseInt(number2));
                            byteInstruction.add((byte) Integer.parseInt(number.substring(1, 3)));
                        }
                        if (number.length() == 4) {
                            throw new IllegalArgumentException("Nie ma określonego adresu logicznego!");
                        }
                    }
                    if (firstParameter.equals("AX") || firstParameter.equals("BX") || firstParameter.equals("CX") || firstParameter.equals("DX")) {
                        singleByte = toByte(firstParameter);
                        byteInstruction.add(singleByte);
                    }
                    firstParameter = "";
                }
                for (byte a : byteInstruction)
                    data.add(a);
                byteInstruction.clear();

                if (instruction.equals("JMP") || instruction.equals("JAXZ") || instruction.equals("JIZ") || instruction.equals("JINZ")) {
                    singleByte = toByte(instruction);
                    byteInstruction.add(singleByte);

                    i = 4;
                    if (instruction.equals("JAXZ") || instruction.equals("JINZ"))
                        i = 5;
                    while (i < singleLine.length()) {
                        firstParameter += singleLine.charAt(i);
                        i++;
                    }
                    if (firstParameter.charAt(0) == '[') {
                        String number = "";
                        String number2 = "";
                        for (int j = 1; j < firstParameter.length() - 1; j++) {
                            number += firstParameter.charAt(j);
                        }
                        if (number.length() == 1 || number.length() == 2) {
                            byteInstruction.add((byte) 0);
                            byteInstruction.add((byte) Integer.parseInt(number));
                        }
                        if (number.length() == 3) {
                            number2 = "0" + number.charAt(0);
                            byteInstruction.add((byte) Integer.parseInt(number2));
                            byteInstruction.add((byte) Integer.parseInt(number.substring(1, 3)));
                        }
                        if (number.length() == 4) {
                            throw new IllegalArgumentException("Nie ma określonego adresu logicznego!");
                        }
                    }
                    firstParameter = "";
                }
                for (byte a : byteInstruction)
                    data.add(a);
                byteInstruction.clear();
                if (instruction.equals("CP")) {
                    singleByte = toByte(instruction);
                    byteInstruction.add(singleByte);

                    //   data.add(singleByte);
                    i = 3;
                    while (singleLine.charAt(i) != space) {
                        firstParameter += singleLine.charAt(i);
                        i++;
                    }
                    byteInstruction.add((byte) Integer.parseInt(firstParameter));

                    i = 5;
                    while (i < singleLine.length()) {
                        second_parameter += singleLine.charAt(i);
                        i++;
                    }
                    byteInstruction.add((byte) Integer.parseInt(second_parameter));

                    firstParameter = "";
                    second_parameter = "";
                }
                for (byte a : byteInstruction)
                    data.add(a);
                byteInstruction.clear();
                if (instruction.equals("DP")) {
                    singleByte = toByte(instruction);
                    byteInstruction.add(singleByte);
                    //data.add(singleByte);

                    i = 3;
                    while (i < singleLine.length()) {
                        firstParameter += singleLine.charAt(i);
                        i++;
                    }
                    byteInstruction.add((byte) Integer.parseInt(firstParameter));

                    firstParameter = "";
                }
                for (byte a : byteInstruction)
                    data.add(a);
                byteInstruction.clear();
            }
            instruction = "";
            i = 0;

        }
        System.out.println("Byte array: ");
        for (byte a : data)
            System.out.println(a);
        System.out.println("Total bytes: " + data.size());

        return data;
    }

    private Vector<String> fileToLines(File file) {
        Vector<String> lines = new Vector<String>();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            String text = null;

            while ((text = reader.readLine()) != null) {
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

    int counterStatus() {
        return process.programCounter;
    }

    private void instructionExecute(String instruction) {
        switch (instruction) {
            case "ADD":
                break;
            case "SUB":
                break;
            case "MUL":
                break;
        }

    }
/*    public byte[] readBytes()
    {
    return data;
    }*/
}
