public interface Shell {
    /* getShellCommands allows Interface class
     * to read all commands from class implemeting
     * Shell interface
     * @return ArrayList<String> type field
     * containing all commands accesible by user
     */
    public ArrayList<String> getShellCommands();
    /* Is used to pass requests to
     * class implementing Shell interface
     * by Interface class
     * @param params - ArrayList<String> type field
     * containing full request collected from user
     */
    public void pass (ArrayList<String> params);
    /* Public funtion displaing help
     */
    public void getHelp();
    /* Private function displaing help
    */
    private void help();
}
