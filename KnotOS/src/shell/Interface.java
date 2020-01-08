package shell;


import memory.virtual.VirtualMemory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;


/**
 * <h1>KnotOS Interface</h1>
 *  This class, along with bounded: Filesystem, Process
 *  SystemControl, TaskList, Tester, User classes are part
 *  of interface for cooperative project "KnotOS".
 * @author Jakub Chodubski
 * @version 1.3
 * @since 11.2019
 */
public class Interface {
    private static final int MAX_MODULES = 50; //defines maximum amount of modules system can handle
    private static int loadedModules = 0;
    private static Shell[] modules = new Shell[MAX_MODULES];
    private static ArrayList<String> post;
    private static boolean asciiDisplayed = false;
    private static VirtualMemory memory;

    /**
     * This is main method of this class.
     * It is used to run all other methods.
     * Contains while (true) loop.
     */
    public static void start() {
        //loads necessary components
        welcomeScreen();

        while (true) {
            //checks if loop should break
            if (quitCondition()) break;
            //post messages waiting to be displayed
            makePost();
            //displays user location in filesystem
            displayLocation();
            //reads user input
            ArrayList<String> userInput = readInput();
            //looks for command in loaded modules
            int foundModuleID = MAX_MODULES;
            boolean foundModule = false;
            for (int i = 0; i != loadedModules; i++) {
                for (int y = 0; y != modules[i].getShellCommands().size(); y++) {
                    String cmdToCheck = modules[i].getShellCommands().get(y);
                    String inputToCheck = userInput.get(0);
                    if (inputToCheck.equals(cmdToCheck)) {
                        foundModuleID = i;
                        foundModule = true;
                    }
                    if (foundModule) break;
                }
                if (foundModule) break;
            }
            //checks if any module was found
            if (foundModuleID == MAX_MODULES) {
                System.out.println("Command was not found. Help:");
                getHelp();
            } else {
                //checks if user wants help
                if ((userInput.size() > 1 && userInput.get(1).equals("help")) || (userInput.size() > 1 && userInput.get(1).equals("h")))
                    modules[foundModuleID].getHelp();
                    //passes command to module
                else modules[foundModuleID].pass(userInput);
            }
        }
        Filesystem.reloadSystemStorage();
        makePost();
        System.out.println("System has closed correctly");
    }

    /**
     * Is used to check if system
     * should start closing
     */
    private static boolean quitCondition() {
        boolean toReturn = false;
        toReturn = (toReturn || SystemControl.getUserExit());
        toReturn = (toReturn || !User.isLogged());
        //toReturn = (toReturn || !Filesystem.filesystemOK());
        if (toReturn) post("System is now closing");
        return toReturn;
    }


    /**
     * WelcomeScreen method enables
     * other sub-programs to
     * run initialization methods
     * and displays user-friendly
     * loading screen
     */
    private static void welcomeScreen() {
        System.out.println("KnotOS starting..");
        displayLogo(0);
        loadModule(new Filesystem());
        displayLogo(10);
        loadModule(new Process());
        displayLogo(20);
        loadModule(new User());
        displayLogo(30);
        loadModule(new SystemControl());
        displayLogo(40);
        post = new ArrayList<String>();
        displayLogo(50);
        loadModule(new Tester());
        displayLogo(60);
        memory  = new VirtualMemory(1024, 256);
        displayLogo(70);
        displayLogo(80);
        displayLogo(90);

        displayLogo(100);

        System.out.print("\n");
        User.login();
    }

    /**
     * This method is used to read input
     * from user and pass request to
     * specialized function
     */
    private static ArrayList<String> readInput() {
        Scanner scanner = new Scanner(System.in);
        String userInput = scanner.nextLine();
        String trimmed = userInput.trim();
        String[] inputArray = trimmed.split(" ");
        ArrayList<String> toReturn = new ArrayList<String>(Arrays.asList(inputArray));
        //delete null fields (double space prevention)
        int nullCount = 0;
        for (String field : toReturn) {
            if (field.equals("")) nullCount++;
        }
        for (int i = nullCount; i != 0; i--) toReturn.remove("");
        //deletes dashes
        for (int i = 1; i != toReturn.size(); i++) {
            if (toReturn.get(i).substring(0, 1).equals("-")) {
                toReturn.set(i, toReturn.get(i).substring(1));
            }
        }
        return toReturn;
    }

    /**
     * displayLogo presents user with
     * nice loading screen containing
     * system name along with progress
     * bar
     *
     * @param progress Percentage progress
     *                 to be displayed on screen
     */
    private static void displayLogo(int progress) {
        if (!asciiDisplayed) {
            System.out.println("__/\\\\\\________/\\\\\\_________________________________________________/\\\\\\\\\\__________/\\\\\\\\\\\\\\\\\\\\\\___        ");
            System.out.println(" _\\/\\\\\\_____/\\\\\\//________________________________________________/\\\\\\///\\\\\\______/\\\\\\/////////\\\\\\_       ");
            System.out.println("  _\\/\\\\\\__/\\\\\\//_____________________________________/\\\\\\________/\\\\\\/__\\///\\\\\\___\\//\\\\\\______\\///__      ");
            System.out.println("   _\\/\\\\\\\\\\\\//\\\\\\______/\\\\/\\\\\\\\\\\\_______/\\\\\\\\\\_____/\\\\\\\\\\\\\\\\\\\\\\__/\\\\\\______\\//\\\\\\___\\////\\\\\\_________     ");
            System.out.println("    _\\/\\\\\\//_\\//\\\\\\____\\/\\\\\\////\\\\\\____/\\\\\\///\\\\\\__\\////\\\\\\////__\\/\\\\\\_______\\/\\\\\\______\\////\\\\\\______    ");
            System.out.println("     _\\/\\\\\\____\\//\\\\\\___\\/\\\\\\__\\//\\\\\\__/\\\\\\__\\//\\\\\\____\\/\\\\\\______\\//\\\\\\______/\\\\\\__________\\////\\\\\\___   ");
            System.out.println("      _\\/\\\\\\_____\\//\\\\\\__\\/\\\\\\___\\/\\\\\\_\\//\\\\\\__/\\\\\\_____\\/\\\\\\_/\\\\___\\///\\\\\\__/\\\\\\_____/\\\\\\______\\//\\\\\\__  ");
            System.out.println("       _\\/\\\\\\______\\//\\\\\\_\\/\\\\\\___\\/\\\\\\__\\///\\\\\\\\\\/______\\//\\\\\\\\\\______\\///\\\\\\\\\\/_____\\///\\\\\\\\\\\\\\\\\\\\\\/___ ");
            System.out.println("        _\\///________\\///__\\///____\\///_____\\/////_________\\/////_________\\/////_________\\///////////_____");
            asciiDisplayed = true;
        }
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
        }
        if (progress == 100) {
            System.out.print("Loading: " + progress + "% ... Done\r");
        } else {
            System.out.print("Loading: " + progress + "%\r");
        }

    }


    private static void displayLocation() {
        ArrayList<String> location = Filesystem.getCurrentLocation();
        boolean firstSlash = true;
        while (location.size() > 0) {
            if (firstSlash) {
                System.out.print(location.get(0));
                firstSlash = false;
            }
            else System.out.print("\\" + location.get(0));
            location.remove(0);
        }
        System.out.print(">");
    }

    /**
     * Iterates through every module's
     * getHelp() function effectively displaying
     * all help available
     */
    public static void getHelp() {
        for (int i = 0; i != loadedModules; i++) modules[i].getHelp();
    }

    private static boolean loadModule(Shell module) {
        for (int i = 0; i != module.getShellCommands().size(); i++) {
            for (int y = 0; y != loadedModules; y++)
                for (int a = 0; a != modules[y].getShellCommands().size(); a++)
                    if (modules[y].getShellCommands().get(a) == module.getShellCommands().get(i)) {
                        post("Module" + module.getName() + "was not loaded due to command conflicts");
                        return false;
                    }
        }
        modules[loadedModules] = module;
        loadedModules++;
        return true;
    }

    /**
     * Use this method to post some text on the screen
     * right after system returns control back to interface
     * @param toPost String to post on screen
     */
    public static void post(String toPost) {
        try {
            if (toPost != null) post.add(toPost);
        } catch (Exception e) {
            post(e.getMessage());
        }
    }

    /**
     * Private function used in pair with post(toPost);
     */
    private static void makePost() {
        for (int i = 0; i != post.size(); i++)
            System.out.println(post.get(i));
        post = new ArrayList<String>();
    }

    public static boolean askUserYN(String question) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println(question + "(y/n)");
            String ans = scanner.next();
            if (ans.equals("y")) return true;
            if (ans.equals("n")) return false;
            System.out.println("Incorrect input");
            System.out.println("Please answer again");
        }
    }

    public static String askUser(String question) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(question);
        return scanner.nextLine();
    }

    public static void pass(String cmd) {

        String trimmed = cmd.trim();
        String[] inputArray = trimmed.split(" ");
        ArrayList<String> userInput = new ArrayList<String>(Arrays.asList(inputArray));
        //delete null fields (double space prevention)
        int nullCount = 0;
        for (String field : userInput) {
            if (field.equals("")) nullCount++;
        }
        for (int i = nullCount; i != 0; i--) userInput.remove("");
        //delete null fields (double space prevention)
        userInput.remove(null);
        for (int i = 1; i != userInput.size(); i++) {
            if (userInput.get(i).substring(0, 1) == "-") {
                userInput.set(i, userInput.get(i).substring(1));
            }
        }

        int foundModuleID = MAX_MODULES;
        boolean foundModule = false;
        for (int i = 0; i != loadedModules; i++) {
            for (int y = 0; y != modules[i].getShellCommands().size(); y++) {
                String cmdToCheck = modules[i].getShellCommands().get(y);
                String inputToCheck = userInput.get(0);
                if (inputToCheck.equals(cmdToCheck)) {
                    foundModuleID = i;
                    foundModule = true;
                }
                if (foundModule) break;
            }
            if (foundModule) break;
        }
        //checks if any module was found
        if (foundModuleID == MAX_MODULES) {
            System.out.println("Command was not found. Help:");
            getHelp();
        } else {
            //checks if user wants help
            if ((userInput.size() > 1 && userInput.get(1).equals("help")) || (userInput.size() > 1 && userInput.get(1).equals("h")))
                modules[foundModuleID].getHelp();
                //passes command to module
            else modules[foundModuleID].pass(userInput);
        }
        makePost();
    }

    public static VirtualMemory getMemory() {
        return memory;
    }
}
