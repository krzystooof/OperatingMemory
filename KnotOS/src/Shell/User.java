package Shell;

import java.beans.IntrospectionException;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class User implements Shell {
    private static ArrayList<String> shellCommands;
    private static boolean userLogged = false;
    private static String currentUser;
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
        //TODO change askUser Strings to param.get() Strings
        boolean correct = false;
        String newPassword = null;
        while (!correct) {
            newPassword = Interface.askUser("Enter new password");
            if (newPassword.contains("\n") || newPassword.contains(".")) {
                System.out.println("Password cannot contain \".\"");
                continue;
            }
            correct = true;
        }
        String userID = Filesystem.restore("userName", currentUser);
        Filesystem.store("userID", userID, newPassword);
    }

    private void deleteUser(ArrayList<String> params) {
        String userCountString = Filesystem.restore("userID", "count");
        int userCount = Integer.parseInt(userCountString);
        if (userCount > 1) {
            String userID = Filesystem.restore("userName", params.get(0));
            if (userID == null) {
                Interface.post("Username not found");
                return;
            } else {
                if (params.get(1).equals(Filesystem.restore("userID", userID))) {
                    Filesystem.removeValue("UserID", userID);
                    Filesystem.removeValue("UserName", params.get(0));
                } else {
                    Interface.post("Wrong password");
                }
            }

        }
    }

    private void addUser(ArrayList<String> params) {
        //TODO
        Interface.post("This function is still in development");
    }

    private void listUsers(ArrayList<String> params) {
        //TODO
        Interface.post("This function is still in development");
    }

    private void logout(ArrayList<String> params) {
        userLogged = false;
        if (Interface.askUserYN("Do you want to log again")) login();
    }

    public static void login() {
        while (!userLogged) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Please enter username or type exit: ");
            String user = scanner.nextLine();
            if (user.equals("exit")) break;
            System.out.print("Please enter password for provided username: ");
            String userPassword = scanner.nextLine();
            String userID = Filesystem.restore("userName", user);
            if (userID != null) {
                String goodPassword = Filesystem.restore("userID", userID);
                if (userPassword.equals(goodPassword)) {
                    userLogged = true;
                    currentUser = user;
                    Interface.post(("Welcome back " + user));
                }
            }
        }
    }

    public static boolean isLogged() {
        return userLogged;
    }

}
