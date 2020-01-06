import cpuscheduler.*;

import java.io.*;
import java.util.HashMap;
import java.util.*;
import java.lang.String;

/**
 * <h1>KnotOS Interpreter</h1>
 *
 * @author Zbigniew Jaryst
 * @version 1.0
 * @since 12.2019
 * This code is a project for Operating Systems 2019 subject.
 */

public class Interpreter {
    private HashMap<Integer, String> instructionMap = new HashMap<Integer, String>();
    private Vector<String> lines = new Vector<String>();
    private Vector<Byte> data = new Vector<Byte>();
    byte singleByte;

    public Interpreter(File file) {
        //Added mnemonics with machine codes
        instructionMap.put(1, "ADD"); //4 Bytes - R - L
        instructionMap.put(2, "SUB"); //4 Bytes - R - L
        instructionMap.put(3, "MUL"); //4 Bytes - R - L
        instructionMap.put(4, "INC"); //2 Bytes - Normnal, 3 - Logical
        instructionMap.put(5, "DEC"); //2 Bytes - Normnal, 3 - Logical
        instructionMap.put(6, "MOV"); //4 Bytes - R + 20
        instructionMap.put(7, "MVI"); //4 Bytes
        instructionMap.put(8, "RES"); //1 Byte
        instructionMap.put(9, "JMP"); //3 Bytes
        instructionMap.put(10, "JAXZ"); //3 Bytes
        instructionMap.put(11, "JIZ"); //3 Bytes
        instructionMap.put(12, "JINZ"); //3 Bytes
        instructionMap.put(13, "CP"); //3 Bytes
        instructionMap.put(14, "DP"); //2 Bytes
        instructionMap.put(19, "HLT"); //1 Byte
        instructionMap.put(20, ""); //2 Bytes, values
        //Added registers with machine codes
        instructionMap.put(15, "AX"); //1 Byte
        instructionMap.put(16, "BX"); //1 Byte
        instructionMap.put(17, "CX"); //1 Byte
        instructionMap.put(18, "DX"); //1 Byte
    }


    /**
     * The method is responsible for calling the rest of the methods needed.
     */
    void runInterpreter(Pcb process, File file) {
        Vector<Byte> Bytes = new Vector<Byte>();
        Bytes = getBytesFromFile(file);

        instructionExecute(process, "JIZ [16]");
        //instructionExecute(process, "JMP [0]");
        //instructionExecute(process, "RES");
        //instructionExecute(process, "JIZ [16]");

        byteInstructionToMnemonic(process, 4);
    }

    /**
    *
    *
    */
    byte toByte(String instruction) {
        byte variable = 0;
        for (HashMap.Entry<Integer, String> entry : instructionMap.entrySet()) {
            if (entry.getValue().equals(instruction)) {
                variable = entry.getKey().byteValue();
            }
        }
        return variable;
    }

    /**
     * Method shows current process with specific address
     * @param process current process
     * @param offset logical address
     */
    void showLine(Pcb process, int offset) {
        System.out.println(byteInstructionToMnemonic(process, offset));
    }

    /**
     * Method checks if given instruction is single integer
     * @param word
     * @return
     */
    boolean isInteger(String word) {
        try {
            int number = Integer.parseInt(word);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Method translates received instruction to vector bytes
     * @param file
     * @return
     */
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
            if (isInteger(singleLine)) {
                data.add((byte) 20);
                data.add((byte) Integer.parseInt(singleLine));
            } else {
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

                        //Receiving first parameter
                        i = 4;
                        while (singleLine.charAt(i) != space) {
                            firstParameter += singleLine.charAt(i);
                            i++;
                        }
                        singleByte = toByte(firstParameter);
                        byteInstruction.add(singleByte);
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
                    } else if (instruction.equals("INC") || instruction.equals("DEC")) {
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
                    } else if (instruction.equals("JMP") || instruction.equals("JAXZ") || instruction.equals("JIZ") || instruction.equals("JINZ")) {
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
                    } else if (instruction.equals("CP")) {
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
                    } else if (instruction.equals("DP")) {
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
                    } else {
                        System.out.println("NUMBER: " + instruction);
                    }
                    for (byte a : byteInstruction)
                        data.add(a);
                    byteInstruction.clear();

                }
                instruction = "";
                i = 0;
            }
        }
        return data;
    }

    /**
     * Substituted method 
     * @param offset
     * @return
     */
    public byte read(int offset) {
        byte A = 0;
        int i = 0;

        for (byte b : data) {
            if (i == offset) {
                A = b;
            }
            i++;
        }
        return A;
    }


    Vector<Byte> loadBytesToByteInstruction(int PID, int offset) {
        Vector<Byte> oneInstruction = new Vector<Byte>();
        byte oneByte = read(offset);
        //Read 4 bytes
        if (oneByte == 1 || oneByte == 2 || oneByte == 3 || oneByte == 26 || oneByte == 7 || oneByte == 21 || oneByte == 22 || oneByte == 23 || oneByte == 41 || oneByte == 42 || oneByte == 43) {
            oneInstruction.add(oneByte);
            for (int i = offset + 1; i < offset + 4; i++) {
                oneByte = read(i);
                oneInstruction.add(oneByte);
            }
        }//Read 3 bytes
        else if (oneByte == 44 || oneByte == 45 || oneByte == 9 || oneByte == 10 || oneByte == 11 || oneByte == 12 || oneByte == 13) {
            oneInstruction.add(oneByte);
            for (int i = offset + 1; i < offset + 3; i++) {
                oneByte = read(i);
                oneInstruction.add(oneByte);
            }
        }//Read 2 bytes
        else if (oneByte == 4 || oneByte == 5 || oneByte == 14 || oneByte == 20) {
            oneInstruction.add(oneByte);
            for (int i = offset + 1; i < offset + 2; i++) {
                oneByte = read(i);
                oneInstruction.add(oneByte);
            }
        }//Read 1 byte
        else if (oneByte == 8 || oneByte == 19) {
            oneInstruction.add(oneByte);
        }

        return oneInstruction;
    }

    public String byteInstructionToMnemonic(Pcb process, int offset) {
        Vector<Byte> oneInstruction = new Vector<Byte>();
        oneInstruction = loadBytesToByteInstruction(process.pid, offset);
        String trueInstruction = "";
        if (oneInstruction.get(0) <= 20) {
            if (oneInstruction.get(0) == 1) {
                trueInstruction += "ADD ";
                if (oneInstruction.get(1) == 15) {
                    trueInstruction += "AX ";
                } else if (oneInstruction.get(1) == 16) {
                    trueInstruction += "BX ";
                } else if (oneInstruction.get(1) == 17) {
                    trueInstruction += "CX ";
                } else if (oneInstruction.get(1) == 18) {
                    trueInstruction += "DX ";
                }
                if (oneInstruction.get(2) != 0 && oneInstruction.get(3) != 0) {
                    trueInstruction += Byte.toString(oneInstruction.get(2)) + Byte.toString(oneInstruction.get(3));
                } else if (oneInstruction.get(2) != 0 && oneInstruction.get(3) == 0) {
                    trueInstruction += Byte.toString(oneInstruction.get(2)) + "00";
                } else if (oneInstruction.get(2) == 0 && oneInstruction.get(3) != 0) {
                    trueInstruction += Byte.toString(oneInstruction.get(3));
                }
            } else if (oneInstruction.get(0) == 2) {
                trueInstruction += "SUB ";
                if (oneInstruction.get(1) == 15) {
                    trueInstruction += "AX ";
                } else if (oneInstruction.get(1) == 16) {
                    trueInstruction += "BX ";
                } else if (oneInstruction.get(1) == 17) {
                    trueInstruction += "CX ";
                } else if (oneInstruction.get(1) == 18) {
                    trueInstruction += "DX ";
                }
                if (oneInstruction.get(2) != 0 && oneInstruction.get(3) != 0) {
                    trueInstruction += Byte.toString(oneInstruction.get(2)) + Byte.toString(oneInstruction.get(3));
                } else if (oneInstruction.get(2) != 0 && oneInstruction.get(3) == 0) {
                    trueInstruction += Byte.toString(oneInstruction.get(2)) + "00";
                } else if (oneInstruction.get(2) == 0 && oneInstruction.get(3) != 0) {
                    trueInstruction += Byte.toString(oneInstruction.get(3));
                }

            } else if (oneInstruction.get(0) == 3) {
                trueInstruction += "MUL ";
                if (oneInstruction.get(1) == 15) {
                    trueInstruction += "AX ";
                } else if (oneInstruction.get(1) == 16) {
                    trueInstruction += "BX ";
                } else if (oneInstruction.get(1) == 17) {
                    trueInstruction += "CX ";
                } else if (oneInstruction.get(1) == 18) {
                    trueInstruction += "DX ";
                }
                if (oneInstruction.get(2) != 0 && oneInstruction.get(3) != 0) {
                    trueInstruction += Byte.toString(oneInstruction.get(2)) + Byte.toString(oneInstruction.get(3));
                } else if (oneInstruction.get(2) != 0 && oneInstruction.get(3) == 0) {
                    trueInstruction += Byte.toString(oneInstruction.get(2)) + "00";
                } else if (oneInstruction.get(2) == 0 && oneInstruction.get(3) != 0) {
                    trueInstruction += Byte.toString(oneInstruction.get(3));
                }
            } else if (oneInstruction.get(0) == 4) {
                trueInstruction += "INC ";
                if (oneInstruction.get(1) == 15) {
                    trueInstruction += "AX";
                } else if (oneInstruction.get(1) == 16) {
                    trueInstruction += "BX";
                } else if (oneInstruction.get(1) == 17) {
                    trueInstruction += "CX";
                } else if (oneInstruction.get(1) == 18) {
                    trueInstruction += "DX";
                }
            } else if (oneInstruction.get(0) == 5) {
                trueInstruction += "DEC ";
                if (oneInstruction.get(1) == 15) {
                    trueInstruction += "AX";
                } else if (oneInstruction.get(1) == 16) {
                    trueInstruction += "BX";
                } else if (oneInstruction.get(1) == 17) {
                    trueInstruction += "CX";
                } else if (oneInstruction.get(1) == 18) {
                    trueInstruction += "DX";
                }
            } else if (oneInstruction.get(0) == 7) {
                trueInstruction += "MVI ";
                if (oneInstruction.get(1) == 15) {
                    trueInstruction += "AX ";
                } else if (oneInstruction.get(1) == 16) {
                    trueInstruction += "BX ";
                } else if (oneInstruction.get(1) == 17) {
                    trueInstruction += "CX ";
                } else if (oneInstruction.get(1) == 18) {
                    trueInstruction += "DX ";
                }
                if (oneInstruction.get(2) != 0 && oneInstruction.get(3) != 0) {
                    trueInstruction += Byte.toString(oneInstruction.get(2)) + Byte.toString(oneInstruction.get(3));
                } else if (oneInstruction.get(2) != 0 && oneInstruction.get(3) == 0) {
                    trueInstruction += oneInstruction.get(2) + "00";
                } else if (oneInstruction.get(2) == 0 && oneInstruction.get(3) != 0) {
                    trueInstruction += Byte.toString(oneInstruction.get(3));
                }
            } else if (oneInstruction.get(0) == 8)
                trueInstruction += "RES";
            else if (oneInstruction.get(0) == 9) {
                trueInstruction += "JMP ";
                if (oneInstruction.get(1) == 0 && oneInstruction.get(2) != 0)
                    trueInstruction += "[" + oneInstruction.get(2) + "]";
                else if (oneInstruction.get(1) != 0 && oneInstruction.get(2) != 0)
                    trueInstruction += "[" + oneInstruction.get(1) + oneInstruction.get(2) + "]";
                else if (oneInstruction.get(1) != 0 && oneInstruction.get(2) == 0)
                    trueInstruction += "[" + oneInstruction.get(1) + "00]";
            } else if (oneInstruction.get(0) == 10) {
                trueInstruction += "JAXZ ";
                if (oneInstruction.get(1) == 0 && oneInstruction.get(2) != 0)
                    trueInstruction += "[" + oneInstruction.get(2) + "]";
                else if (oneInstruction.get(1) != 0 && oneInstruction.get(2) != 0)
                    trueInstruction += "[" + oneInstruction.get(1) + oneInstruction.get(2) + "]";
                else if (oneInstruction.get(1) != 0 && oneInstruction.get(2) == 0)
                    trueInstruction += "[" + oneInstruction.get(1) + "00]";
            } else if (oneInstruction.get(0) == 11) {
                trueInstruction += "JIZ ";
                if (oneInstruction.get(1) == 0 && oneInstruction.get(2) != 0)
                    trueInstruction += "[" + oneInstruction.get(2) + "]";
                else if (oneInstruction.get(1) != 0 && oneInstruction.get(2) != 0)
                    trueInstruction += "[" + oneInstruction.get(1) + oneInstruction.get(2) + "]";
                else if (oneInstruction.get(1) != 0 && oneInstruction.get(2) == 0)
                    trueInstruction += "[" + oneInstruction.get(1) + "00]";
            } else if (oneInstruction.get(0) == 12) {
                trueInstruction += "JINZ ";
                if (oneInstruction.get(1) == 0 && oneInstruction.get(2) != 0)
                    trueInstruction += "[" + oneInstruction.get(2) + "]";
                else if (oneInstruction.get(1) != 0 && oneInstruction.get(2) != 0)
                    trueInstruction += "[" + oneInstruction.get(1) + oneInstruction.get(2) + "]";
                else if (oneInstruction.get(1) != 0 && oneInstruction.get(2) == 0)
                    trueInstruction += "[" + oneInstruction.get(1) + "00]";
            } else if (oneInstruction.get(0) == 13) {
                trueInstruction += "CP " + oneInstruction.get(1) + " " + oneInstruction.get(2);
            } else if (oneInstruction.get(0) == 14) {
                trueInstruction += "DP " + oneInstruction.get(1);
            } else if (oneInstruction.get(0) == 19) {
                trueInstruction += "HLT";
            } else if (oneInstruction.get(0) == 20) {
                trueInstruction += oneInstruction.get(1);
            }

        } else if (oneInstruction.get(0) > 20) {
            if (oneInstruction.get(0) == 26) {
                trueInstruction += "MOV ";
                if (oneInstruction.get(1) == 15)
                    trueInstruction += "AX ";
                else if (oneInstruction.get(1) == 16)
                    trueInstruction += "BX ";
                else if (oneInstruction.get(1) == 17)
                    trueInstruction += "CX ";
                else if (oneInstruction.get(1) == 18)
                    trueInstruction += "DX ";

                if (oneInstruction.get(2) == 15)
                    trueInstruction += "AX";
                else if (oneInstruction.get(2) == 16)
                    trueInstruction += "BX";
                else if (oneInstruction.get(2) == 17)
                    trueInstruction += "CX";
                else if (oneInstruction.get(2) == 18)
                    trueInstruction += "DX";
            } else if (oneInstruction.get(0) == 21) {
                trueInstruction += "ADD ";
                if (oneInstruction.get(1) == 15)
                    trueInstruction += "AX ";
                else if (oneInstruction.get(1) == 16)
                    trueInstruction += "BX ";
                else if (oneInstruction.get(1) == 17)
                    trueInstruction += "CX ";
                else if (oneInstruction.get(1) == 18)
                    trueInstruction += "DX ";

                if (oneInstruction.get(2) == 15)
                    trueInstruction += "AX";
                else if (oneInstruction.get(2) == 16)
                    trueInstruction += "BX";
                else if (oneInstruction.get(2) == 17)
                    trueInstruction += "CX";
                else if (oneInstruction.get(2) == 18)
                    trueInstruction += "DX";
            } else if (oneInstruction.get(0) == 22) {
                trueInstruction += "SUB ";
                if (oneInstruction.get(1) == 15)
                    trueInstruction += "AX ";
                else if (oneInstruction.get(1) == 16)
                    trueInstruction += "BX ";
                else if (oneInstruction.get(1) == 17)
                    trueInstruction += "CX ";
                else if (oneInstruction.get(1) == 18)
                    trueInstruction += "DX ";

                if (oneInstruction.get(2) == 15)
                    trueInstruction += "AX";
                else if (oneInstruction.get(2) == 16)
                    trueInstruction += "BX";
                else if (oneInstruction.get(2) == 17)
                    trueInstruction += "CX";
                else if (oneInstruction.get(2) == 18)
                    trueInstruction += "DX";
            } else if (oneInstruction.get(0) == 23) {
                trueInstruction += "MUL ";
                if (oneInstruction.get(1) == 15)
                    trueInstruction += "AX ";
                else if (oneInstruction.get(1) == 16)
                    trueInstruction += "BX ";
                else if (oneInstruction.get(1) == 17)
                    trueInstruction += "CX ";
                else if (oneInstruction.get(1) == 18)
                    trueInstruction += "DX ";

                if (oneInstruction.get(2) == 15)
                    trueInstruction += "AX";
                else if (oneInstruction.get(2) == 16)
                    trueInstruction += "BX";
                else if (oneInstruction.get(2) == 17)
                    trueInstruction += "CX";
                else if (oneInstruction.get(2) == 18)
                    trueInstruction += "DX";
            } else if (oneInstruction.get(0) == 41) {
                trueInstruction += "ADD ";
                if (oneInstruction.get(1) == 15)
                    trueInstruction += "AX ";
                else if (oneInstruction.get(1) == 16)
                    trueInstruction += "BX ";
                else if (oneInstruction.get(1) == 17)
                    trueInstruction += "CX ";
                else if (oneInstruction.get(1) == 18)
                    trueInstruction += "DX ";

                if (oneInstruction.get(2) == 0 && oneInstruction.get(3) != 0)
                    trueInstruction += "[" + oneInstruction.get(3) + "]";
                else if (oneInstruction.get(2) != 0 && oneInstruction.get(3) != 0)
                    trueInstruction += "[" + oneInstruction.get(2) + oneInstruction.get(3) + "]";
                else if (oneInstruction.get(2) != 0 && oneInstruction.get(3) == 0)
                    trueInstruction += "[" + oneInstruction.get(2) + "00]";
            } else if (oneInstruction.get(0) == 42) {
                trueInstruction += "SUB ";
                if (oneInstruction.get(1) == 15)
                    trueInstruction += "AX ";
                else if (oneInstruction.get(1) == 16)
                    trueInstruction += "BX ";
                else if (oneInstruction.get(1) == 17)
                    trueInstruction += "CX ";
                else if (oneInstruction.get(1) == 18)
                    trueInstruction += "DX ";

                if (oneInstruction.get(2) == 0 && oneInstruction.get(3) != 0)
                    trueInstruction += "[" + oneInstruction.get(3) + "]";
                else if (oneInstruction.get(2) != 0 && oneInstruction.get(3) != 0)
                    trueInstruction += "[" + oneInstruction.get(2) + oneInstruction.get(3) + "]";
                else if (oneInstruction.get(2) != 0 && oneInstruction.get(3) == 0)
                    trueInstruction += "[" + oneInstruction.get(2) + "00]";
            } else if (oneInstruction.get(0) == 43) {
                trueInstruction += "MUL ";
                if (oneInstruction.get(1) == 15)
                    trueInstruction += "AX ";
                else if (oneInstruction.get(1) == 16)
                    trueInstruction += "BX ";
                else if (oneInstruction.get(1) == 17)
                    trueInstruction += "CX ";
                else if (oneInstruction.get(1) == 18)
                    trueInstruction += "DX ";

                if (oneInstruction.get(2) == 0 && oneInstruction.get(3) != 0)
                    trueInstruction += "[" + oneInstruction.get(3) + "]";
                else if (oneInstruction.get(2) != 0 && oneInstruction.get(3) != 0)
                    trueInstruction += "[" + oneInstruction.get(2) + oneInstruction.get(3) + "]";
                else if (oneInstruction.get(2) != 0 && oneInstruction.get(3) == 0)
                    trueInstruction += "[" + oneInstruction.get(2) + "00]";
            } else if (oneInstruction.get(0) == 44) {
                trueInstruction += "INC ";
                {
                    if (oneInstruction.get(1) == 0 && oneInstruction.get(2) != 0)
                        trueInstruction += "[" + oneInstruction.get(2) + "]";
                    else if (oneInstruction.get(1) != 0 && oneInstruction.get(2) != 0)
                        trueInstruction += "[" + oneInstruction.get(1) + oneInstruction.get(2) + "]";
                    else if (oneInstruction.get(1) != 0 && oneInstruction.get(2) == 0)
                        trueInstruction += "[" + oneInstruction.get(1) + "00]";
                }
            } else if (oneInstruction.get(0) == 45) {
                trueInstruction += "DEC ";
                {
                    if (oneInstruction.get(1) == 0 && oneInstruction.get(2) != 0)
                        trueInstruction += "[" + oneInstruction.get(2) + "]";
                    else if (oneInstruction.get(1) != 0 && oneInstruction.get(2) != 0)
                        trueInstruction += "[" + oneInstruction.get(1) + oneInstruction.get(2) + "]";
                    else if (oneInstruction.get(1) != 0 && oneInstruction.get(2) == 0)
                        trueInstruction += "[" + oneInstruction.get(1) + "00]";
                }
            }
        }
        return trueInstruction;
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

    void instructionExecute(Pcb process, String instruction) {
        Registers regs = process.registers;
        int size = 0;
        char space = ' ';
        String word = "";
        String firstParameter = "";
        String secondParameter = "";
        instruction = instruction.toUpperCase();
        if (instruction.charAt(0) == 'R' || instruction.charAt(0) == 'H') {
            if (word.equals("RES")) {
                process.programCounter += 1;
                regs.ax = 0;
                regs.bx = 0;
                regs.cx = 0;
                regs.dx = 0;
            } else if (word.equals("HLT")) {
                process.programCounter += 1;

            }
        } else {
            while (instruction.charAt(size) != space) {
                word += instruction.charAt(size);
                size++;
            }
            System.out.println("Instruction: " + word);
            //Parameter completion
            if (word.equals("ADD") || word.equals("SUB") || word.equals("MUL") || word.equals("MOV") || word.equals("MVI")) {
                int i = 4;
                while (instruction.charAt(i) != space) {
                    firstParameter += instruction.charAt(i);
                    i++;
                }
                i = 7;
                System.out.println("First Parameter: " + firstParameter);
                while (i < instruction.length()) {
                    secondParameter += instruction.charAt(i);
                    i++;
                }
                System.out.println("Second Parameter: " + secondParameter);
            } else if (word.equals("INC") || word.equals("DEC") || word.equals("JMP") || word.equals("JIZ")) {
                int i = 4;
                while (i < instruction.length()) {
                    firstParameter += instruction.charAt(i);
                    i++;
                }
                System.out.println("First Parameter: " + firstParameter);
            } else if (word.equals("JAXZ") || word.equals("JINZ")) {
                int i = 5;
                while (i < instruction.length()) {
                    firstParameter += instruction.charAt(i);
                    i++;
                }
                System.out.println("First Parameter: " + firstParameter);
            } else if (word.equals("CP")) {
                int i = 3;
                while (instruction.charAt(i) != space) {
                    firstParameter += instruction.charAt(i);
                    i++;
                }
                i = 5;
                System.out.println("First Parameter: " + firstParameter);
                while (i < instruction.length()) {
                    secondParameter += instruction.charAt(i);
                    i++;
                }
                System.out.println("Second Parameter: " + secondParameter);
            } else if (word.equals("DP")) {
                int i = 3;
                while (i < instruction.length()) {
                    firstParameter += instruction.charAt(i);
                    i++;
                }
                System.out.println("First Parameter: " + firstParameter);
            }
        }
        //Executing instructions
        if (size == 2) {
            if (word.equals("CP")) {
                //create process
            }
            if (word.equals("DP")) {
                //delete process
            }
        } else if (size == 3) {
            if (word.equals("ADD")) {
                process.programCounter += 4;
                //Checks logical address
                if (secondParameter.charAt(0) == '[') {
                    String value = "";
                    int i = 1;
                    while (i < secondParameter.length() - 1) {
                        value += secondParameter.charAt(i);
                        i++;
                    }
                    int logicalAddress = Integer.parseInt(value);
                    if (firstParameter.equals("AX")) {
                        regs.ax += Integer.parseInt(byteInstructionToMnemonic(process, logicalAddress));
                    } else if (firstParameter.equals("BX")) {
                        regs.bx += Integer.parseInt(byteInstructionToMnemonic(process, logicalAddress));
                    } else if (firstParameter.equals("CX")) {
                        regs.cx += Integer.parseInt(byteInstructionToMnemonic(process, logicalAddress));
                    } else if (firstParameter.equals("DX")) {
                        regs.dx += Integer.parseInt(byteInstructionToMnemonic(process, logicalAddress));
                    }
                    //Checks Registers
                } else if (secondParameter.charAt(0) == 'A' || secondParameter.charAt(0) == 'B' || secondParameter.charAt(0) == 'C' || secondParameter.charAt(0) == 'D') {
                    if (firstParameter.equals("AX") && secondParameter.equals("BX"))
                        regs.ax += regs.bx;
                    else if (firstParameter.equals("AX") && secondParameter.equals("CX"))
                        regs.ax += regs.cx;
                    else if (firstParameter.equals("AX") && secondParameter.equals("DX"))
                        regs.ax += regs.dx;

                    if (firstParameter.equals("BX") && secondParameter.equals("AX"))
                        regs.bx += regs.ax;
                    else if (firstParameter.equals("BX") && secondParameter.equals("CX"))
                        regs.bx += regs.cx;
                    else if (firstParameter.equals("BX") && secondParameter.equals("DX"))
                        regs.bx += regs.dx;

                    if (firstParameter.equals("CX") && secondParameter.equals("AX"))
                        regs.cx += regs.ax;
                    else if (firstParameter.equals("CX") && secondParameter.equals("BX"))
                        regs.cx += regs.bx;
                    else if (firstParameter.equals("CX") && secondParameter.equals("DX"))
                        regs.cx += regs.dx;

                    if (firstParameter.equals("DX") && secondParameter.equals("AX"))
                        regs.dx += regs.ax;
                    else if (firstParameter.equals("DX") && secondParameter.equals("BX"))
                        regs.dx += regs.bx;
                    else if (firstParameter.equals("DX") && secondParameter.equals("CX"))
                        regs.dx += regs.cx;
                }
                //Checks values
                else {
                    int value = Integer.parseInt(secondParameter);
                    if (firstParameter.equals("AX"))
                        regs.ax += value;
                    if (firstParameter.equals("BX"))
                        regs.bx += value;
                    if (firstParameter.equals("CX"))
                        regs.cx += value;
                    if (firstParameter.equals("DX"))
                        regs.dx += value;
                }
            }
            if (word.equals("SUB")) {
                process.programCounter += 4;
                if (secondParameter.charAt(0) == '[') {
                    String value = "";
                    int i = 1;
                    while (i < secondParameter.length() - 1) {
                        value += secondParameter.charAt(i);
                        i++;
                    }
                    int logicalAddress = Integer.parseInt(value);
                    if (firstParameter.equals("AX")) {
                        regs.ax -= Integer.parseInt(byteInstructionToMnemonic(process, logicalAddress));
                    } else if (firstParameter.equals("BX")) {
                        regs.bx -= Integer.parseInt(byteInstructionToMnemonic(process, logicalAddress));
                    } else if (firstParameter.equals("CX")) {
                        regs.cx -= Integer.parseInt(byteInstructionToMnemonic(process, logicalAddress));
                    } else if (firstParameter.equals("DX")) {
                        regs.dx -= Integer.parseInt(byteInstructionToMnemonic(process, logicalAddress));
                    }

                } else if (secondParameter.charAt(0) == 'A' || secondParameter.charAt(0) == 'B' || secondParameter.charAt(0) == 'C' || secondParameter.charAt(0) == 'D') {
                    if (firstParameter.equals("AX") && secondParameter.equals("BX"))
                        regs.ax -= regs.bx;
                    else if (firstParameter.equals("AX") && secondParameter.equals("CX"))
                        regs.ax -= regs.cx;
                    else if (firstParameter.equals("AX") && secondParameter.equals("DX"))
                        regs.ax -= regs.dx;

                    if (firstParameter.equals("BX") && secondParameter.equals("AX"))
                        regs.bx -= regs.ax;
                    else if (firstParameter.equals("BX") && secondParameter.equals("CX"))
                        regs.bx -= regs.cx;
                    else if (firstParameter.equals("BX") && secondParameter.equals("DX"))
                        regs.bx -= regs.dx;

                    if (firstParameter.equals("CX") && secondParameter.equals("AX"))
                        regs.cx -= regs.ax;
                    else if (firstParameter.equals("CX") && secondParameter.equals("BX"))
                        regs.cx -= regs.bx;
                    else if (firstParameter.equals("CX") && secondParameter.equals("DX"))
                        regs.cx -= regs.dx;

                    if (firstParameter.equals("DX") && secondParameter.equals("AX"))
                        regs.dx -= regs.ax;
                    else if (firstParameter.equals("DX") && secondParameter.equals("BX"))
                        regs.dx -= regs.bx;
                    else if (firstParameter.equals("DX") && secondParameter.equals("CX"))
                        regs.dx -= regs.cx;
                } else {
                    int value = Integer.parseInt(secondParameter);
                    if (firstParameter.equals("AX"))
                        regs.ax -= value;
                    if (firstParameter.equals("BX"))
                        regs.bx -= value;
                    if (firstParameter.equals("CX"))
                        regs.cx -= value;
                    if (firstParameter.equals("DX"))
                        regs.dx -= value;
                }
            }
            if (word.equals("MUL")) {
                process.programCounter += 4;
                if (secondParameter.charAt(0) == '[') {
                    String value = "";
                    int i = 1;
                    while (i < secondParameter.length() - 1) {
                        value += secondParameter.charAt(i);
                        i++;
                    }
                    int logicalAddress = Integer.parseInt(value);
                    if (firstParameter.equals("AX")) {
                        regs.ax *= Integer.parseInt(byteInstructionToMnemonic(process, logicalAddress));
                    } else if (firstParameter.equals("BX")) {
                        regs.bx *= Integer.parseInt(byteInstructionToMnemonic(process, logicalAddress));
                    } else if (firstParameter.equals("CX")) {
                        regs.cx *= Integer.parseInt(byteInstructionToMnemonic(process, logicalAddress));
                    } else if (firstParameter.equals("DX")) {
                        regs.dx *= Integer.parseInt(byteInstructionToMnemonic(process, logicalAddress));
                    }

                } else if (secondParameter.charAt(0) == 'A' || secondParameter.charAt(0) == 'B' || secondParameter.charAt(0) == 'C' || secondParameter.charAt(0) == 'D') {
                    if (firstParameter.equals("AX") && secondParameter.equals("BX"))
                        regs.ax *= regs.bx;
                    else if (firstParameter.equals("AX") && secondParameter.equals("CX"))
                        regs.ax *= regs.cx;
                    else if (firstParameter.equals("AX") && secondParameter.equals("DX"))
                        regs.ax *= regs.dx;

                    if (firstParameter.equals("BX") && secondParameter.equals("AX"))
                        regs.bx *= regs.ax;
                    else if (firstParameter.equals("BX") && secondParameter.equals("CX"))
                        regs.bx *= regs.cx;
                    else if (firstParameter.equals("BX") && secondParameter.equals("DX"))
                        regs.bx *= regs.dx;

                    if (firstParameter.equals("CX") && secondParameter.equals("AX"))
                        regs.cx *= regs.ax;
                    else if (firstParameter.equals("CX") && secondParameter.equals("BX"))
                        regs.cx *= regs.bx;
                    else if (firstParameter.equals("CX") && secondParameter.equals("DX"))
                        regs.cx *= regs.dx;

                    if (firstParameter.equals("DX") && secondParameter.equals("AX"))
                        regs.dx *= regs.ax;
                    else if (firstParameter.equals("DX") && secondParameter.equals("BX"))
                        regs.dx *= regs.bx;
                    else if (firstParameter.equals("DX") && secondParameter.equals("CX"))
                        regs.dx *= regs.cx;
                } else {
                    int value = Integer.parseInt(secondParameter);
                    if (firstParameter.equals("AX"))
                        regs.ax *= value;
                    if (firstParameter.equals("BX"))
                        regs.bx *= value;
                    if (firstParameter.equals("CX"))
                        regs.cx *= value;
                    if (firstParameter.equals("DX"))
                        regs.dx *= value;
                }
            }
            if (word.equals("INC")) {/* DODAC ADRES LOGICZNY*/
                if (firstParameter.charAt(0) == '[') {
                    process.programCounter += 3;
                    String value = "";
                    int i = 1;
                    int j = 0;
                    while (i < firstParameter.length() - 1) {
                        value += firstParameter.charAt(i);
                        i++;
                    }
                    int logicalAddress = Integer.parseInt(value);
                    for (Byte a : data) {
                        if (j == logicalAddress) {
                            int k = Integer.parseInt(byteInstructionToMnemonic(process, logicalAddress)) + 1;
                        }
                        j++;
                    }
                } else if (firstParameter.equals("AX")) {
                    regs.ax++;
                    process.programCounter += 2;
                } else if (firstParameter.equals("BX")) {
                    regs.bx++;
                    process.programCounter += 2;
                } else if (firstParameter.equals("CX")) {
                    regs.bx++;
                    process.programCounter += 2;
                } else if (firstParameter.equals("DX")) {
                    regs.dx++;
                    process.programCounter += 2;
                }
            }
            if (word.equals("DEC")) {/* DODAC ADRES LOGICZNY*/
                if (firstParameter.charAt(0) == '[') {
                    String value = "";
                    int i = 1;
                    int j = 0;
                    while (i < firstParameter.length() - 1) {
                        value += firstParameter.charAt(i);
                        i++;
                    }
                    int logicalAddress = Integer.parseInt(value);
                    for (Byte a : data) {
                        if (j == logicalAddress) {
                            int k = Integer.parseInt(byteInstructionToMnemonic(process, logicalAddress)) - 1;
                        }
                        j++;
                    }
                } else if (firstParameter.equals("AX"))
                    regs.ax--;
                else if (firstParameter.equals("BX"))
                    regs.bx--;
                else if (firstParameter.equals("CX"))
                    regs.cx--;
                else if (firstParameter.equals("DX"))
                    regs.dx--;
            }
            if (word.equals("MOV")) {
                process.programCounter += 4;
                if (secondParameter.charAt(0) == 'A' || secondParameter.charAt(0) == 'B' || secondParameter.charAt(0) == 'C' || secondParameter.charAt(0) == 'D') {
                    if (firstParameter.equals("AX") && secondParameter.equals("BX"))
                        regs.ax = regs.bx;
                    else if (firstParameter.equals("AX") && secondParameter.equals("CX"))
                        regs.ax = regs.cx;
                    else if (firstParameter.equals("AX") && secondParameter.equals("DX"))
                        regs.ax = regs.dx;

                    if (firstParameter.equals("BX") && secondParameter.equals("AX"))
                        regs.bx = regs.ax;
                    else if (firstParameter.equals("BX") && secondParameter.equals("CX"))
                        regs.bx = regs.cx;
                    else if (firstParameter.equals("BX") && secondParameter.equals("DX"))
                        regs.bx = regs.dx;

                    if (firstParameter.equals("CX") && secondParameter.equals("AX"))
                        regs.cx = regs.ax;
                    else if (firstParameter.equals("CX") && secondParameter.equals("BX"))
                        regs.cx = regs.bx;
                    else if (firstParameter.equals("CX") && secondParameter.equals("DX"))
                        regs.cx = regs.dx;

                    if (firstParameter.equals("DX") && secondParameter.equals("AX"))
                        regs.dx = regs.ax;
                    else if (firstParameter.equals("DX") && secondParameter.equals("BX"))
                        regs.dx = regs.bx;
                    else if (firstParameter.equals("DX") && secondParameter.equals("CX"))
                        regs.dx = regs.cx;
                }
            }
            if (word.equals("MVI")) {
                process.programCounter += 4;
                int value = Integer.parseInt(secondParameter);
                if (firstParameter.equals("AX"))
                    regs.ax = value;
                if (firstParameter.equals("BX"))
                    regs.bx = value;
                if (firstParameter.equals("CX"))
                    regs.cx = value;
                if (firstParameter.equals("DX"))
                    regs.dx = value;
            }
            if (word.equals("JMP")) {
                process.programCounter += 3;

                if (firstParameter.charAt(0) == '[') {
                    String value = "";
                    int i = 1;
                    int j = 0;
                    while (i < firstParameter.length() - 1) {
                        value += firstParameter.charAt(i);
                        i++;
                    }
                    int logicalAddress = Integer.parseInt(value);
                    instructionExecute(process, byteInstructionToMnemonic(process, logicalAddress));
                    process.programCounter = logicalAddress;
                }
            }
            if (word.equals("JIZ")) {
                process.programCounter += 3;
                if (regs.ax == 0 && regs.bx == 0 && regs.cx == 0 && regs.dx == 0) {
                    if (firstParameter.charAt(0) == '[') {
                        String value = "";
                        int i = 1;
                        int j = 0;
                        while (i < firstParameter.length() - 1) {
                            value += firstParameter.charAt(i);
                            i++;
                        }
                        int logicalAddress = Integer.parseInt(value);
                        instructionExecute(process, byteInstructionToMnemonic(process, logicalAddress));
                        process.programCounter = logicalAddress;
                    }
                } else
                    throw new IllegalArgumentException("Nie można zrealizować danego warunku!");

            }
        } else if (size == 4) {
            if (word.equals("JAXZ")) {
                process.programCounter += 3;

            }
            if (word.equals("JINZ")) {
                process.programCounter += 3;

            }
        }
        process.saveRegisters(regs);
    }
}