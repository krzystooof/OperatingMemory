package Shell;

import java.io.File;
import java.util.ArrayList;

public class Filesystem implements Shell {
    private boolean noFilesystem;
    private static ArrayList<String> shellCommands;
    private static ArrayList<String> userLocationPathname;
    private static File userLocation;
    private static File systemDir;
    private final static String SYSTEM32 = "C/system32";

    Filesystem() {
        shellCommands = new ArrayList<String>();
        shellCommands.add("cd");
        shellCommands.add("dir");
        shellCommands.add("mkdir");
        shellCommands.add("rmdir");
        shellCommands.add("rm");

        noFilesystem = false;
        userLocation = new File("C");
        if (!userLocation.exists()) {
            userLocation.mkdir();
            noFilesystem = true;
            systemDir = new File("C/system32");
            systemDir.mkdir();
        }
        userLocationPathname = new ArrayList<String>();
        userLocationPathname.add(userLocation.getName());
    }

    @Override
    public ArrayList<String> getShellCommands() {
        return shellCommands;
    }

    @Override
    public void pass(ArrayList<String> params) {
        switch (params.get(0)) {
            case "cd": {
                if (params.size() > 0) params.remove(0);
                cd(params);
                break;
            }
            case "dir": {
                if (params.size() > 0) params.remove(0);
                dir(params);
                break;
            }
            case "mkdir": {
                if (params.size() > 0) params.remove(0);
                //mkdir(params);
                break;
            }
            case "rmdir": {
                if (params.size() > 0) params.remove(0);
                //rmdir(params);
                break;
            }
            case "rm": {
                if (params.size() > 0) params.remove(0);
                //rm(params);
                break;
            }
        }
    }

    private void cd(ArrayList<String> params) {
        if (params.size() > 0) {
            if (params.get(0).equals("..")) cdBack();
            else cdMove(params);
        }
        else {
            Interface.post("Too few arguments");
        }
    }

    private void cdMove(ArrayList<String> params) {
        ArrayList<String> list = getDirectoryList(userLocation);
        boolean directoryExists = false;
        for (String dirName : list) {
            if (dirName.equals(params.get(0))) {
                directoryExists = true;
                break;
            }
        }
        if (directoryExists) {
            /* TODO */
        }
        else {
            Interface.post("No such file or directory");
        }
    }

    private void cdBack() {
        if (userLocationPathname.size() > 1) {
            userLocationPathname.remove(userLocationPathname.size() - 1);
            userLocation = new File(makeStringPath(userLocationPathname));
        }
        else {
            Interface.post("There is no parent directory");
        }
    }

    private void dir(ArrayList<String> params) {
        String[] fileList = userLocation.list();
        for (String name : fileList) {
            System.out.println(name);
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
                "rm <Name>\n"
        );
    }

    @Override
    public String getName() {
        return "Filesystem";
    }

    /**
     * This function returns pathname to
     * current user working directory
     * @return pathname in form of ArrayList<String>
     */
    public static ArrayList<String> getCurrentLocation() {
        ArrayList<String> toReturn = new ArrayList<String>();
        for (String dir : userLocationPathname) {
            toReturn.add(dir);
        }
        if (userLocationPathname.size() > 0) {
            String drive = toReturn.get(0);
            toReturn.remove(0);
            String toAdd = drive + ":";
            toReturn.add(0, toAdd);
        }
        return toReturn;
    }

    private static boolean nameIsLegal(String nameToCheck) {
        if (nameToCheck.length() > 20) return false;
        if (nameToCheck.contains("*")) return false;
        if (nameToCheck.contains(".")) return false;
        if (nameToCheck.contains("\"")) return false;
        if (nameToCheck.contains("/")) return false;
        if (nameToCheck.contains("\\")) return false;
        if (nameToCheck.contains("[")) return false;
        if (nameToCheck.contains("]")) return false;
        if (nameToCheck.contains(":")) return false;
        if (nameToCheck.contains(";")) return false;
        if (nameToCheck.contains("|")) return false;
        if (nameToCheck.contains(",")) return false;
        return true;
    }

    private static ArrayList<String> getDirectoryList(File location) {
        File[] directoryFiles = location.listFiles(File::isDirectory);
        ArrayList<String> directories = new ArrayList<String>();
        for (File dir : directoryFiles) {
            directories.add(dir.getName());
        }
        return directories;
    }

    private static String makeStringPath(ArrayList<String> path) {
        ArrayList<String> location = new ArrayList<String>();
        for (String dir : path) {
            location.add(dir);
        }
        String pathString = new String();
        boolean firstSlash = true;
        while (location.size() > 0) {
            if (firstSlash) pathString = location.get(0);
            else pathString = pathString + "/" + location.get(0);
            location.remove(0);
        }
        return pathString;
    }
}