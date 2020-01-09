package shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class Script implements Shell {
    ArrayList<String> shellCommands;

    Script() {
        shellCommands = new ArrayList<String>();
        shellCommands.add("script");
    }

    @Override
    public ArrayList<String> getShellCommands() {
        return shellCommands;
    }

    @Override
    public void pass(ArrayList<String> params) {
        if (params.size() > 0 ) {
            File script = Filesystem.getFile(params.get(1));
            try {
                String line = null;
                FileReader scriptReader = new FileReader(script);
                BufferedReader bufferedReader = new BufferedReader(scriptReader);
                while ((line = bufferedReader.readLine()) != null) {
                    Interface.pass(line);
                }
                bufferedReader.close();
            } catch (Exception e) {
                Interface.post(e.getMessage());
            }

        } else Interface.post("Too few arguments");
    }

    @Override
    public void getHelp() {
        System.out.println("Help regarding scrpits:\n" +
                "script <filename>");
    }

    @Override
    public String getName() {
        return null;
    }
}
