package Shell;

import java.util.ArrayList;

public class System implements Shell {
    private static ArrayList<String> shellCommands;
    private static boolean userExit;

    System() {
        userExit = false;
        shellCommands.add("exit");
    }

    @Override
    public ArrayList<String> getShellCommands() {
        return shellCommands;
    }

    @Override
    public void pass(ArrayList<String> params) {
        switch (params.get(0)) {
            case "exit": {
                exit();
            }
        }
    }

    @Override
    public void getHelp() {

    }

    @Override
    public String getName() {
        return null;
    }

    private void exit() {
        userExit = true;
    }

    /** This functions tells other modules
     *  if user is to exit system
     * @return true if user wants to exit KnotOS
     */
    public static boolean getUserExit() {
        return userExit;
    }
}
