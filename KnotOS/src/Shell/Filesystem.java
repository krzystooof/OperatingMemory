package Shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class Filesystem implements Shell {
    private static boolean noFilesystem = true;
    private static boolean storageOK = true;
    private static ArrayList<String> shellCommands;
    private static ArrayList<String> userLocationPathname;
    private static File userLocation;
    private final static String SYSTEM32 = "C/system32";
    private static File systemStorageFile;
    private static ArrayList<String> systemStorage;

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
            File systemDir;
            systemDir = new File(SYSTEM32);
            systemDir.mkdir();
            String systemStoragePath = SYSTEM32 + "/SYSTEM.knot";
            systemStorageFile = new File(systemStoragePath);
            try {
                systemStorageFile.createNewFile();
            } catch (Exception e) {
                Interface.post("Unknown error");
                Interface.post(e.getMessage());
                storageOK = false;
            }
        }
        reloadSystemStorage();
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
                mkdir(params);
                break;
            }
            case "rmdir": {
                if (params.size() > 0) params.remove(0);
                rmdir(params);
                break;
            }
            case "rm": {
                if (params.size() > 0) params.remove(0);
                rm(params);
                break;
            }
        }
    }

    private void mkdir(ArrayList<String> params) {
        if (nameIsLegal(params.get(0))) {
            ArrayList<String> path = new ArrayList<String>();
            for (String dir : userLocationPathname) path.add(dir);
            path.add(params.get(0));
            String stringPath = makeStringPath(path);
            File toMake = new File(stringPath);
            if (!toMake.exists()) {
                toMake.mkdir();
                Interface.post("Directory created");
            } else Interface.post("Name already taken");
        }
        else Interface.post("Illegal name");
    }

    private void rmdir(ArrayList<String> params) {
        ArrayList<String> path = new ArrayList<String>();
        for (String dir : userLocationPathname) path.add(dir);
        path.add(params.get(0));
        String stringPath = makeStringPath(path);
        File toRemove = new File(stringPath);
        if (toRemove.exists() && toRemove.isDirectory()) {
            remove(params.get(0));
        } else Interface.post("Directory does not exist");
    }

    private void rm(ArrayList<String> params) {
        ArrayList<String> path = new ArrayList<String>();
        for (String dir : userLocationPathname) path.add(dir);
        path.add(params.get(0));
        String stringPath = makeStringPath(path);
        File toRemove = new File(stringPath);
        if (toRemove.exists() && toRemove.isFile()) {
            remove(params.get(0));
        } else Interface.post("File does not exist");
    }

    private void remove(String filename) {
        ArrayList<String> path = new ArrayList<String>();
        for (String dir : userLocationPathname) path.add(dir);
        path.add(filename);
        String stringPath = makeStringPath(path);
        File toRemove = new File(stringPath);
        if (toRemove.delete()) Interface.post("Deleted successfully");
        else Interface.post("Error occurred while deleting file or directory");
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
            userLocationPathname.add(params.get(0));
            userLocation = new File(makeStringPath(userLocationPathname));
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
        if ((fileList != null) && (fileList.length > 0)) {
            for (String name : fileList) System.out.println(name);
        }
        else Interface.post("Directory is empty");
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
            if (firstSlash) {
                pathString = location.get(0);
                firstSlash = false;
            }
            else pathString = pathString + "/" + location.get(0);
            location.remove(0);
        }
        return pathString;
    }

    /** Returns false if
     * filesystem is not ready
     * to work on.
     */
    public static boolean filesystemOK() {
        return (!noFilesystem && storageOK);
    }

    //TODO Review comment below
    /**
     * Allows other modules to store single
     * line of text (standard characters only)
     * and save it until next system launch.
     * Stored values will be available to recover only
     * during next system use and not after that.
     * @param key String to access value later
     * @param value String value to store. Cannot contain
     *              newline character (\n).
     * @return If saved correctly will return true. Otherwise false.
     */
    public static boolean store(String key, String value) {
        if (key.contains("\n") || value.contains("\n")) return false;
        boolean found = false;
        for (int i = 0; i > systemStorage.size();i += 2) {
            if (systemStorage.get(i) == key) {
                systemStorage.set(i+1,value);
                found = true;
            }
        }
        if (!found) {
            systemStorage.add(key);
            systemStorage.add(value);
        }
        return true;
    }

    /**
     * Used to recover values stored via store(key, value)
     * method.
     * @param key String inputed to store(key, value).
     * @return String of saved value. If key no found
     * is null.
     */
    public static String restore(String key) {
        String toReturn = null;
        for (int i = 0; i > systemStorage.size();i += 2) {
            if (systemStorage.get(i) == key) {
                toReturn = systemStorage.get(i+1);
            }
        }
        return toReturn;
    }

    /**
     * Must be called before reading anything from
     * SystemStorage and before system shuts down to update
     * storage file.
     */
    public static void reloadSystemStorage() { //TODO make it save SystemStorage
        if (systemStorage.size() > 0) {
            try {
                String line = null;
                FileReader storageReader = new FileReader(systemStorageFile);
                BufferedReader bufferedReader = new BufferedReader(storageReader);
                while ((line = bufferedReader.readLine()) != null) {
                    systemStorage.add(line);
                }
                bufferedReader.close();
            } catch (Exception e) {
                Interface.post("Unknown error");
                Interface.post(e.getMessage());
                storageOK = false;
            }
        } else {
            //TODO save
        }
    }
}