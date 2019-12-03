import java.util.ArrayList;

public class User implements Shell {
    private static ArrayList<String> shellCommands;

    public User() {
        shellCommands = new ArrayList<String>();
        shellCommands.add("user");
        shellCommands.add("logout");
        shellCommands.add("password");
    }

    @Override
    public ArrayList<String> getShellCommands() {
        return shellCommands;
    }

    @Override
    public void pass(ArrayList<String> params) {
        switch (params.get(0)) {
            case "user": {
                params.remove(0);
                user(params);
                break;
            }
            case "logout": {
                params.remove(0);
                logout(params);
                break;
            }
            case "password": {
                params.remove(0);
                password(params);
                break;

            }
            case "encrypt": {
                break;
            }
            case "decrypt": {
                break;
            }
        }
    }

    @Override
    public void getHelp() {
        System.out.println("Help in regard to user accounts:\n" +
                "user <Action> <Login> <Password>\n" +
                "      -delete <Login> <Password>\n" +
                "      -add <Login> <Password>\n" +
                "      -list" +
                "password <Action>\n" +
                "logout");
    }


    private void user(ArrayList<String> params) {
        switch (params.get(0)) {
            case "delete": {
                break;
            }
            case "add": {
                break;
            }
            case "list": {

            }
            default: {
                getHelp();
                break;
            }
        }
    }

    private void logout(ArrayList<String> params) {

    }

    private void password(ArrayList<String> params) {
        switch (params.get(0)) {
            case "change": {
                break;
            }
            default: {
                getHelp();
                break;
            }
        }
    }
}
