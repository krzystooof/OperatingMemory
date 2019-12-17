package Shell;

import java.util.ArrayList;

public class Process implements Shell {
    private ArrayList<String> shellCommands;
    private static boolean isStepMode;

    public Process() {
        shellCommands = new ArrayList<String>();
        shellCommands.add("start");
        shellCommands.add("killtask");
        shellCommands.add("step");
        shellCommands.add("process");
        shellCommands.add("p");

        isStepMode = false;
    }

    @Override
    public void getHelp() {
        help();
    }

    @Override
    public String getName() {
        return "Process";
    }

    @Override
    public ArrayList<String> getShellCommands() {
        return shellCommands;
    }

    @Override
    public void pass(ArrayList<String> params) {
        switch (params.get(0)) {
            case "start": {
                if (params.size() > 0) params.remove(0);
                create(params);
                break;
            }
            case "killtask": {
                if (params.size() > 0) params.remove(0);
                kill(params);
                break;
            }
            case "step": {
                if (params.size() > 0) params.remove(0);
                debug(params);
                break;
            }
            case "p":
            case "process": {
                if (params.size() > 0) params.remove(0);
                processPass(params);
                break;
            }
        }
    }

    private void processPass(ArrayList<String> params) {
        switch (params.get(0)) {
            case "create": {
                if (params.size() > 0) params.remove(0);
                create(params);
                break;
            }
            case "kill": {
                if (params.size() > 0) params.remove(0);
                kill(params);
                break;
            }
            case "step": {
                if (params.size() > 0) params.remove(0);
                debug(params);
                break;
            }
        }
    }

    private void create(ArrayList<String> param) {

    }

    private void kill(ArrayList<String> param) {

    }

    private void debug(ArrayList<String> param) {
        if (isStepMode) {
            isStepMode = false;
            Interface.post("Step mode deactivated");
        }
        else  {
            isStepMode = true;
            Interface.post("Step mode activated");
        }
    }

    /*
    * Displays help */
    private void help() {
        System.out.println("Help in regard to process operation:\n" +
                "cp <Name> <Assembly Source> <Shell.Shell.Process ID> <Shell.Shell.Process ID>\n" +
                "process -create <Name> <Assembly Source> <Shell.Shell.Process ID> <Shell.Shell.Process ID>" +
                "p -create <Name> <Assembly Source> <Shell.Shell.Process ID> <Shell.Shell.Process ID>\n" +
                "kp <Name>\n" +
                "process -kill <Name>\n" +
                "p -kill <Name>\n" +
                "debugp <Name> <Assembly Source> <Shell.Shell.Process ID> <Shell.Shell.Process ID>\n" +
                "process -debug <Name> <Assembly Source> <Shell.Shell.Process ID> <Shell.Shell.Process ID>\n" +
                "p -debug <Name> <Assembly Source> <Shell.Shell.Process ID> <Shell.Shell.Process ID>\n" +
                "helpp\n" +
                "process help\n" +
                "p help\n");
    }

    public static boolean getStepMode() {
        return isStepMode;
    }

}
