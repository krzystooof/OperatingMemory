package shell;

import cpuscheduler.CpuScheduler;
import cpuscheduler.PCB;
import cpuscheduler.State;
import interpreter.Interpreter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Process implements Shell {
    private ArrayList<String> shellCommands;
    private static boolean isStepMode;
    private CpuScheduler cpuScheduler;
    private List<Interpreter> interpreters;

    public Process() {
        shellCommands = new ArrayList<String>();
        shellCommands.add("start");
        shellCommands.add("taskkill");
        shellCommands.add("step");
        shellCommands.add("process");
        shellCommands.add("p");
        shellCommands.add("tasklist");
        shellCommands.add("next");

        isStepMode = false;
        cpuScheduler = new CpuScheduler();
        interpreters = new ArrayList<>();
    }

    @Override
    public void getHelp() {
        help();
    }

    @Override
    public String getName() {
        return "semaphores.Process";
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
            case "taskkill": {
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
            case "tasklist": {
                TaskList taskList = new TaskList();
                break;
            }
            case "next":{
                next();
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
        if(param.size() > 2) {
            String name = param.get(0);
            String filePath = param.get(1);
            int pid = Integer.parseInt(param.get(2));
            int priority = Integer.parseInt(param.get(3));

            //TODO Search for ids
            // Check if user id is not 0

            PCB pcb = new PCB(pid, priority, State.NEW, name);
            cpuScheduler.addProcess(pcb);
            File file = Filesystem.getFile(filePath);
            if (file != null) {
                Interpreter interpreter = new Interpreter(file, pcb);
                interpreters.add(interpreter);
                run();
            }

        } else {
            Interface.post("Too few arguments");
        }
    }

    private void run(){
        PCB runningPcb = cpuScheduler.getRunningPCB();

        if(runningPcb.PID==0){
            Interface.post("There is only idle process");
        }
        else {
            for (Interpreter interpreter : interpreters) {
                if (interpreter.getPcb().PID == runningPcb.PID) {
                    interpreter.runInterpreter();
                    break;
                }
            }
        }

    }

    private void kill(ArrayList<String> param) {
        //TODO
    }

    private void next() {
        run();
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
                "start <Name> <Assembly Source> <PID> <Priority>\n" +
                "process -create <Name> <Assembly Source> <PID> <Priority>\n" +
                "p -create <Name> <Assembly Source> <PID> <Priority>\n" +
                "taskkill <Name>\n" +
                "process -kill <Name>\n" +
                "p -kill <Name>\n" +
                "step\n" +
                "process step\n" +
                "p step\n");
    }

    public static boolean getStepMode() {
        return isStepMode;
    }

}
