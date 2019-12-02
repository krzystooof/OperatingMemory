public interface Shell {
    public String[] getShellCommands();
    public void pass (String[] params);
    public void getHelp();
    private void help() {

    }
}
