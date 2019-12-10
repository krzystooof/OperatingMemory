package Shell;

import java.io.File;
import java.util.ArrayList;

public class Filesystem implements Shell {
    private static ArrayList<String> shellCommands;
    private static ArrayList<String> currentLocation;

    public Filesystem() {
        shellCommands = new ArrayList<String>();
        shellCommands.add("cd");
        shellCommands.add("dir");
        shellCommands.add("mkdir");
        shellCommands.add("rmdir");
        shellCommands.add("rm");

        currentLocation = new ArrayList<String>();
        File dir = new File("drive");
        dir.mkdir();
    }

    @Override
    public ArrayList<String> getShellCommands() {
        return shellCommands;
    }

    @Override
    public void pass(ArrayList<String> params) {
        switch (params.get(0)) {
            case "cd": {
                params.remove(0);
                cd(params);
                break;
            }
            case "dir": {
                params.remove(0);
                dir(params);
                break;
            }
            case "mkdir": {
                params.remove(0);
                mkdir(params);
                break;
            }
            case "rmdir": {
                params.remove(0);
                rmdir(params);
                break;
            }
            case "rm": {
                params.remove(0);
                rm(params);
                break;
            }
        }
    }

    @Override
    public void getHelp() {
        System.out.println("Help in regard to filesystem\n" +
                "cd <Directory>\n" +
                "cd ..\n" +
                "dir \n" +
                "mkdir <Name>\n" +
                "rmdir <Name>\n" +
                "rm <Name>\n");
    }

    @Override
    public String getName() {
        return "Filesystem";
    }

    private void cd(ArrayList<String> params) {
        if (params.get(0).equals("..")) {
            if (currentLocation.size() != 0) currentLocation.remove(currentLocation.size()-1);
            else Interface.post("There is no parent directory");
        }
        else {
            if (checkIfExists(params.get(0))) {
                currentLocation.add(params.get(0));
            } else {
                Interface.post("No such file or directory");
            }
        }
    }

    private void dir(ArrayList<String> params) {
        ArrayList<String> files = listFilesForFolder(getCurrentFolderFile());
        for (int i=0;i!= files.size();i++) {
            System.out.println(files.get(i));
        }
    }

    private void mkdir(ArrayList<String> params) {
        if (checkFileName(params.get(0))) {
            if (checkIfExists(params.get(0))) {
                Interface.post("Every directory has to have unique name");
            } else {
                String pathname = new String();
                pathname = "drive";
                for (int i = 0; i != currentLocation.size(); i++) {
                    pathname += "\\";
                    pathname += currentLocation.get(i);
                }
                pathname += params.get(0);
                File mkdir = new File(pathname);
                Interface.post("Directory created");
            }
        }
        else {
            Interface.post("Illegal name\n Name should not contain following symbols: * . \" / \\ [ ] : ; | ,");
        }
    }

    private void rmdir(ArrayList<String> params) {
        rm(params);
    }

    private void rm(ArrayList<String> params) {
        if (checkIfExists(params.get(0))) {
            currentLocation.add(params.get(0));
            File toDelete = getCurrentFolderFile();
            currentLocation.remove(currentLocation.size() - 1);
        }
    }

    public ArrayList<String> getCurrentLocation() {
        ArrayList<String> toReturn = currentLocation;
        toReturn.add(0,"C:");
        return toReturn;
    }

    private ArrayList<String> listFilesForFolder(final File folder) {
        ArrayList<String> list = new ArrayList<String>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                list.add(fileEntry.getName());
            } else {
                list.add(fileEntry.getName());
            }
        }
        return list;
    }

    private File getCurrentFolderFile() {
        String pathname = new String();
        pathname = "drive";
        for (int i = 0 ; i != currentLocation.size(); i++) {
            pathname += "\\";
            pathname += currentLocation.get(i);
        }
        File roReturn = new File(pathname);
        return roReturn;
    }

    private boolean checkIfExists(String filename) {
        ArrayList<String> list = listFilesForFolder(getCurrentFolderFile());
        for (int i = 0; i != list.size(); i++) {
            if (list.get(i) == filename) return true;
        }
        return false;
    }

    private boolean checkFileName(String name) {
        if (name.length() > 20) return false;
        if (name.contains("*")) return false;
        if (name.contains(".")) return false;
        if (name.contains("\"")) return false;
        if (name.contains("/")) return false;
        if (name.contains("\\")) return false;
        if (name.contains("[")) return false;
        if (name.contains("]")) return false;
        if (name.contains(":")) return false;
        if (name.contains(";")) return false;
        if (name.contains("|")) return false;
        if (name.contains(",")) return false;
        return true;
    }
}
