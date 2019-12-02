public class Process implements Shell {
    private String[] ShellCommands;

    public Process() {
        ShellCommands = new String[] {
                "cp",
                "kp",
                "debugp",
                "helpp",
                "process",
                "p"
        };
    }
    public void getHelp() {
        help();
    }
    public String[] getShellCommands() {
        return ShellCommands;
    }

    public void pass(String[] params) {
        String[] pass = new String[1];
        switch (params[0]) {
            case "cp": {

            }
            case "kp": {

            }
            case "debugp": {

            }
            case "helpp": {

            }
            case "p": {

            }
        }
    }

    private void create(String[] param) {

    }
    private void kill(String[] param) {

    }
    private void debug(String[] param) {

    }
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
