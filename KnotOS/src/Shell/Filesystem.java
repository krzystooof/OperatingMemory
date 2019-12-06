import java.util.ArrayList;

public class Filesystem implements Shell {
    private static ArrayList<String> shellCommands;

    public Filesystem() {
        shellCommands = new ArrayList<String>();
        shellCommands.add("cd");
        shellCommands.add("dir");
        shellCommands.add("mkdir");
        shellCommands.add("rmdir");
        shellCommands.add("rm");
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

    }
    private void dir(ArrayList<String> params) {

    }
    private void mkdir(ArrayList<String> params) {

    }
    private void rmdir(ArrayList<String> params) {

    }
    private void rm(ArrayList<String> params) {

    }
}
