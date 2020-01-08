package shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class Notepad implements Shell{
    ArrayList<String> shellCommands;

    Notepad() {
        shellCommands = new ArrayList<String>();
        shellCommands.add("notepad");
    }

    @Override
    public ArrayList<String> getShellCommands() {
        return shellCommands;
    }

    @Override
    public void pass(ArrayList<String> params) {
        if (params.size() > 0 ) {
            File file = Filesystem.getFile(params.get(1));
            try {
                String line = null;
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                while ((line = bufferedReader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (Exception e) {
                Interface.post(e.getMessage());
            }

        } else Interface.post("Too few arguments");
    }

    @Override
    public void getHelp() {
        System.out.println("Help regarding reading text files:\n" +
                "notepad <filename>");
    }

    @Override
    public String getName() {
        return null;
    }
}
