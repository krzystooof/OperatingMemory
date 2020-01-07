package shell;

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
                "     -delete <Login> <Password>\n" +
                "     -add <Login> <Password>\n" +
                "     -list\n" +
                "password <Action>\n" +
                "         -change <Old Pass> <New Pass>\n" +
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
        String newPass = params.get(1);
        String oldPass = params.get(0);
        //check for illegal characters
        if (oldPass.contains("\n") ||
            oldPass.contains(".") ||
            newPass.contains("\n") ||
            newPass.contains(".")) {
            Interface.post("Password cannot contain \".\" or newline character");
        } else {
            //passwords are legal. Reading old password and userID from storage
            String userID = Filesystem.restore("userName", currentUser);
            String correctPass = Filesystem.restore("userID", userID);
            if (userID != null && correctPass != null) { // checking if read was successful
                if (correctPass.equals(oldPass)) { // checking if user-provided password was ok
                    Filesystem.store("userID", userID, newPass); // saving new password
                    Interface.post("Password for current user has changed");
                }
            } else {
                Interface.post("Cannot access system storage");
            }
        }
    }

    private void deleteUser(ArrayList<String> params) {
        if (params.size() > 1) {
            String userCountString = Filesystem.restore("userID", "count");
            int userCount = Integer.parseInt(userCountString);
            if (userCount > 1) {
                String userID = Filesystem.restore("userName", params.get(0));
                if (params.get(0).equals(currentUser)) {
                    Interface.post("Cannot delete currently logged user");
                } else {
                    if (userID == null) {
                        Interface.post("Username not found");
                        return;
                    } else {
                        if (params.get(1).equals(Filesystem.restore("userID", userID))) {
                            Filesystem.removeValue("userID", userID);
                            Filesystem.removeValue("userName", params.get(0));
                            Interface.post("User " + params.get(0) + " deleted");
                            //decrement user count
                            userCount--;
                            Filesystem.store("userID", "count", Integer.toString(userCount));
                        } else {
                            Interface.post("Wrong password");
                        }
                    }
                }
            } else {
                Interface.post("Cannot delete only user");
            }
        } else Interface.post("Too few arguments");
    }

    private void addUser(ArrayList<String> params) {
        if (params.size() > 1) {
            String user = params.get(0);
            String password = params.get(1);
            //checking if user and password are legal
            if (user.contains("\n") ||
                    user.contains(".") ||
                    password.contains("\n") ||
                    password.contains(".")) {
                Interface.post("Username or password cannot contain \".\" or newline character");
            } else {
                //check if username is not taken
                if (Filesystem.restore("userName", user) == null) {
                    //user and password are legal. Incrementing user count
                    String userCount = Filesystem.restore("userID", "count");
                    String newUserCount = Integer.toString(Integer.parseInt(userCount) + 1);
                    if (Filesystem.store("userID", "count", newUserCount)) {
                        //check for first userID available
                        int userNum = 0;
                        for (int i = 0; i != Integer.parseInt(userCount) + 1; i++) {
                            if (Filesystem.restore("userID", ("user" + Integer.toString(i))) == null) {
                                userNum = i;
                                break;
                            }
                        }
                        String userID = "user" + Integer.toString(userNum);
                        boolean success = true;
                        success = success && Filesystem.store("userName", user, userID);
                        success = success && Filesystem.store("userID", userID, password);
                        if (success) Interface.post("New user added");
                        else Interface.post("Cannot access system storage");
                    } else Interface.post("Cannot access system storage");
                } else Interface.post("Username already taken");
            }
        } else Interface.post("Too few arguments");
    }

    private void listUsers(ArrayList<String> params) {
        String userCount = Filesystem.restore("userID", "count");
        if (userCount != null) {
            Interface.post("Total users: " + userCount);
        } else Interface.post("Cannot access system storage");
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
                } else {
                    System.out.println("Wrong password");
                }
            } else {
                System.out.println("Username not found");
            }
        }
    }

    public static boolean isLogged() {
        return userLogged;
    }

}
