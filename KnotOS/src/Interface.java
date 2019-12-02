import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Interface {
    private static final int MAX_MODULES = 50; //defines maximum amount of modules system can handle
    private static boolean quitCondition;

    public Interface(boolean quitCondition) {
        this.quitCondition = false;
    }

    /* This is main method of this class.
     * It is used to run all other methods.
     * Contains while (true) loop.
     */
    public static void start() {

        welcomeScreen();
        while (true) {
            displayLocation();
            ArrayList<String> userInput = readInput();

            if (quitCondition) break;
        }
    }


    /* WelcomeScreen method enables
     * other sub-programs to
     * run initialization methods
     * and displays user-friendly
     * loading screen
     */
    private static Shell[] welcomeScreen() {
        Shell[] modules = new Shell[MAX_MODULES];
        displayLogo(0);
        //Here you can insert your initial methods
        displayLogo(100);
        return modules;
    }

    /* This method is used to read input
     * from user and pass request to
     * specialized function
     */
    private static ArrayList<String> readInput() {
        Scanner scanner = new Scanner(System.in);
        String userInput = scanner.nextLine();
        String[] inputArray = userInput.split(" ");
        ArrayList<String> toReturn = new ArrayList<String>(Arrays.asList(inputArray));
        //deletes dashes
        for (int i = 1; i != toReturn.size(); i++) {
            if(toReturn.get(i).substring(0,1) == "-") {
                toReturn.set(i,toReturn.get(i).substring(1));
            }
        }
        return toReturn;
    }

    /* displayLogo presents user with
     * nice loading screen containing
     * system name along with progress
     * bar
     * @param progress Percentage progress
     * to be displayed on screen
     */
    private static void displayLogo(int progress) {
        if (progress < 10) {
            System.out.println("KnotOS");
        }
    }

    private static void displayLocation() {

    }
}
