import java.util.ArrayList;

public interface Shell {
    /* getShellCommands allows Interface class
     * to read all commands from class implementing
     * Shell interface
     * @return ArrayList<String> type field
     * containing all commands accessible by user
     */
    public ArrayList<String> getShellCommands();
    /* Is used to pass requests to
     * class implementing Shell interface
     * by Interface class
     * @param params - ArrayList<String> type field
     * containing full request collected from user
     */
    public void pass (ArrayList<String> params);
    /* Public function displaying help
     */
    public void getHelp();

}
