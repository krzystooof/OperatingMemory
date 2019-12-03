import java.util.ArrayList;

public class Process implements Shell {
    private ArrayList<String> shellCommands;

    public Process() {
        shellCommands = new ArrayList<String>();
        shellCommands.add("cp");
        shellCommands.add("kp");
        shellCommands.add("debugp");
        shellCommands.add("helpp");
        shellCommands.add("process");
        shellCommands.add("p");
    }

    @Override
    public void getHelp() {
        help();
    }

    @Override
    public ArrayList<String> getShellCommands() {
        return shellCommands;
    }

    @Override
    public void pass(ArrayList<String> params) {
        switch (params.get(0)) {
            case "cp": {
                params.remove(0);
                create(params);
                break;
            }
            case "kp": {
                params.remove(0);
                kill(params);
                break;
            }
            case "debugp": {
                params.remove(0);
                debug(params);
                break;
            }
            case "helpp": {
                help();
                break;
            }
            case "p":
            case "process": {
                params.remove(0);
                processPass(params);
                break;
            }
        }
    }

    private void processPass(ArrayList<String> params) {
        switch (params.get(0)) {
            case "create": {
                params.remove(0);
                create(params);
                break;
            }
            case "kill": {
                params.remove(0);
                kill(params);
                break;
            }
            case "debug": {
                params.remove(0);
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

    }

    /*
    * Displays help */
    private void help() {
        System.out.println("Help in regard to process operation:\n" +
                "cp <Name> <Assembly Source> <Process ID> <Process ID>\n" +
                "process -create <Name> <Assembly Source> <Process ID> <Process ID>" +
                "p -create <Name> <Assembly Source> <Process ID> <Process ID>\n" +
                "kp <Name>\n" +
                "process -kill <Name>\n" +
                "p -kill <Name>\n" +
                "debugp <Name> <Assembly Source> <Process ID> <Process ID>\n" +
                "process -debug <Name> <Assembly Source> <Process ID> <Process ID>\n" +
                "p -debug <Name> <Assembly Source> <Process ID> <Process ID>\n" +
                "helpp\n" +
                "process help\n" +
                "p help\n");
    }

}
