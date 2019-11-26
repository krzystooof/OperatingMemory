import java.util.Scanner;

public class Interface {

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
            readInput();
            if (quitCondition) break;
        }
    }

    private static boolean quitCondition;

    /* WelcomeScreen method enables
     * other sub-programs to
     * run initialization methods
     * and displays user-friendly
     * loading screen
     */
    private static void welcomeScreen() {
        displayLogo(0);
        //Here you can insert your initial methods
        displayLogo(100);
    }

    /* This method is used to read input
     * from user and pass request to
     * specialized function
     */
    private static void readInput() {
        Scanner scanner = new Scanner(System.in);
        String userInput = scanner.nextLine();
        String inputArray[] = userInput.split(" ");
        switch (inputArray[1]) {
            case "process": {
                break;
            }
            case "file": {
                break;
            }
            case "run": {
                break;
            }
            case "quit": {

                break;
            }
            default: {
                break;
            }
        }
    }

    /* displayLogo presents user with
     * nice loading screen containing
     * system name along with progress
     * bar
     * @param progress Percentage progress
     * to be displayed on screen
     */
    private static void displayLogo(int progress) {

    }

    private static void displayLocation() {

    }
}
