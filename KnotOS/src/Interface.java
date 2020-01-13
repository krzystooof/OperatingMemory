import memory.PhysicalMemoryManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Interface {
    PhysicalMemoryManager memoryManager;
    public Interface(){
        memoryManager = new PhysicalMemoryManager(Integer.valueOf(getInput("RAM size: ").get(0)));
        memoryManager.printInfo();
        run();
    }
    private void showHelp(){
        System.out.println("write <segmentID> <offset> <charToWrite> -e");
        System.out.println("write <segmentID> <segmentLength> <charToWrite>");
        System.out.println("remove <segmentID>");
        System.out.println("read <segmentID> <offset>");
        System.out.println("read <segmentID>");
        System.out.println("print");
    }
    private ArrayList<String> getInput(String toShow){
        if(!toShow.isBlank())System.out.print(toShow);
        Scanner keyboard = new Scanner(System.in);
        String action = keyboard.nextLine();
        ArrayList<String> commands = new ArrayList<>(Arrays.asList(action.split(" ")));
        commands.removeIf((String::isBlank));
        return commands;

    }
    private void run() {
        try {
            ArrayList<String> input = getInput("$ ");
            switch (input.get(0)) {
                case "write":
                    // write <segmentID> <segmentLength> <charToWrite>
                    if (input.size() == 4) {
                        int segmentID = Integer.parseInt(input.get(1));
                        int segmentLength = Integer.parseInt(input.get(2));
                        byte[] toWrite = new byte[segmentLength];
                        for (int i =0;i<toWrite.length;i++) {
                            toWrite[i] = (byte) input.get(3).charAt(0);
                        }
                        memoryManager.write(toWrite, segmentID, 0);
                    }
                    // write <segmentID> <offset> <charToWrite> -e
                    else if (input.get(input.size()-1).equals("-e")) {
                        int segmentID = Integer.parseInt(input.get(1));
                        int offset = Integer.parseInt(input.get(2));
                        memoryManager.write(segmentID, offset, (byte) input.get(3).charAt(0));
                    }
                    else showHelp();
                    break;
                case "remove":
                    // remove <segmentID>
                    if (input.size()==2) memoryManager.remove(Integer.parseInt(input.get(1)));
                    else showHelp();
                    break;
                case "read":
                    // read <segmentID> <offset>
                    if (input.size()==3){
                        int segmentID = Integer.parseInt(input.get(1));
                        int offset = Integer.parseInt(input.get(2));
                        byte toShow = memoryManager.read(segmentID, offset);
                        System.out.println(toShow);
                    }
                    // read <segmentID>
                    else if (input.size()==1){
                        int segmentID = Integer.parseInt(input.get(1));
                        byte[] toShow = memoryManager.read(segmentID);
                        for(int i=0;i<toShow.length;i++) {
                            System.out.println(i+": "+toShow);
                        }
                    }
                    else showHelp();
                    break;
                case "print":
                    memoryManager.printInfo();
                    break;
                default:
                    showHelp();
                    break;
            }
        }
        catch (IllegalArgumentException e) {
            System.out.println(e);
        }
        run();
    }
}
