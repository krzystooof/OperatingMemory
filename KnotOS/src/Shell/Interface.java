package Shell;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Interface {
    private static final int MAX_MODULES = 50; //defines maximum amount of modules system can handle
    private static int loadedModules = 0;
    private static Shell[] modules = new Shell[MAX_MODULES];
    private static ArrayList<String> post;

    /**
     * This is main method of this class.
     * It is used to run all other methods.
     * Contains while (true) loop.
     */
    public void start() {
        welcomeScreen();
        while (true) {
            if (quitCondition()) break;
            makePost();
            displayLocation();
            int foundModuleID = MAX_MODULES;
            ArrayList<String> userInput = readInput();
            for (int i = 0; i != loadedModules; i++ ) {
              for (int y= 0 ; y!= modules[i].getShellCommands().size(); y++) {
                  String cmdToCheck = modules[i].getShellCommands().get(y);
                  String inputToCheck = userInput.get(0);
                  if (inputToCheck.equals(cmdToCheck)) {
                      foundModuleID = i;
                  }
              }
            }
            if (foundModuleID == MAX_MODULES) {
                System.out.println("Command was not found. Help:");
                getHelp();
            }
            else {
                if ((userInput.get(1) == "help" && userInput.size() > 1) || (userInput.get(1) == "h" && userInput.size() > 1))
                    modules[foundModuleID].getHelp();
                else modules[foundModuleID].pass(userInput);
            }
        }
    }

    /**
    * Is used to check if system
    * should start closing*/
    private boolean quitCondition() {
        return false;
    }


    /** WelcomeScreen method enables
     * other sub-programs to
     * run initialization methods
     * and displays user-friendly
     * loading screen
     */
    private Shell[] welcomeScreen() {
        String notLoaded1 = "Module";
        String notLoaded2 = "was not loaded.";
        displayLogo(0);
        loadModule(new Filesystem());
        loadModule(new Process());
        loadModule(new User());
        displayLogo(10);
        post = new ArrayList<String>();
        displayLogo(20);

        displayLogo(30);
        displayLogo(40);
        displayLogo(50);
        displayLogo(60);
        displayLogo(70);
        displayLogo(80);
        displayLogo(90);

        displayLogo(100);
        return modules;
    }

    /** This method is used to read input
     * from user and pass request to
     * specialized function
     */
    private ArrayList<String> readInput() {
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

    /** displayLogo presents user with
     * nice loading screen containing
     * system name along with progress
     * bar
     * @param progress Percentage progress
     * to be displayed on screen
     */
    private void displayLogo(int progress) {
        
        System.out.println("KnotOS");
        System.out.println("Loading: " + progress + "%");
    }

    private void displayLocation() {

    }

    private void getHelp() {
      for (int i = 0; i != loadedModules; i++) modules[i].getHelp();
    }

    private boolean loadModule(Shell module) {
      for (int i = 0; i != module.getShellCommands().size(); i++) {
        for (int y = 0 ; y != loadedModules; y++)
          for (int a = 0; a != modules[y].getShellCommands().size(); a++)
            if (modules[y].getShellCommands().get(a) == module.getShellCommands().get(i)) {
                post("Module" + module.getName() + "was not loaded due to commands conflict");
                return false;
            }
      }
      modules[loadedModules] = module;
      loadedModules++;
      return true;
    }

    public static void post(String toPost) {
      post.add(toPost);
    }

    private void makePost() {
      for (int i = 0; i != post.size(); i++)
        System.out.println(post.get(i));
      post = new ArrayList<String>();
    }

}
