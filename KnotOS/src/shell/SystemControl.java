package shell;

import java.util.ArrayList;

public class SystemControl implements Shell {
    private static ArrayList<String> shellCommands;
    private static boolean userExit;

    SystemControl() {
        shellCommands = new ArrayList<String>();
        userExit = false;
        shellCommands.add("exit");
        shellCommands.add("help");
        shellCommands.add("echo");
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
                break;
            }
            case "help": {
                displayHelp();
                break;
            }
            case "echo": {
                params.remove(0);
                echo(params);
                break;
            }
        }
    }

    private void echo(ArrayList<String> params) {
        if (params.size() > 0) {
            String line = "";
            for (String word : params) {
                line += (word + " ");
            }
            Interface.post(line);
        }
    }

    @Override
    public void getHelp() {
        System.out.println("Help regarding system controls:\n" +
                "exit\n" +
                "help\n" +
                "echo <Print>\n");
    }

    @Override
    public String getName() {
        return "System Control";
    }

    private void displayHelp() {
        Interface.getHelp();
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
