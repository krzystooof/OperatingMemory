package Shell;

import java.util.ArrayList;

public class User implements Shell {
    private static ArrayList<String> shellCommands;
    private static boolean userLogged = false;
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
                    if (params.size() > 0) params.remove(0);
                    user(params);
                    break;
                }
                case "logout": {
                    if (params.size() > 0) params.remove(0);
                    logout(params);
                    break;
                }
                case "password": {
                    if (params.size() > 0) params.remove(0);
                    password(params);
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
                "logout\n");
    }

    @Override
    public String getName() {
        return "User Account Management";
    }


    private void user(ArrayList<String> params) {
        if (params.size() > 0) {
            switch (params.get(0)) {
                case "delete": {
                    if (params.size() > 0) params.remove(0);
                    deleteUser(params);
                    break;
                }
                case "add": {
                    if (params.size() > 0) params.remove(0);
                    addUser(params);
                    break;
                }
                case "list": {
                    if (params.size() > 0) params.remove(0);
                    listUsers(params);
                    break;
                }
                default: {
                    getHelp();
                    break;
                }
            }
        }
        else {
            getHelp();
        }
    }

    private void password(ArrayList<String> params) {
        if (params.size() > 0) {
            switch (params.get(0)) {
                case "change": {
                    params.remove(0);
                    changePassword(params);
                    break;
                }
                default: {
                    getHelp();
                    break;
                }
            }
        } else {
            getHelp();
        }
    }

    private void changePassword(ArrayList<String> params) {
    }

    private void deleteUser(ArrayList<String> params) {
    }

    private void addUser(ArrayList<String> params) {
    }

    private void listUsers(ArrayList<String> params) {
    }

    private void logout(ArrayList<String> params) {
        userLogged = false;
        System.out.println("Do you want to log again?");
        //TODO
    }
    public static void login() {
        userLogged = true;
    }

    public static boolean isLogged() {
        return userLogged;
    }

}
