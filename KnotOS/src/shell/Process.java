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
                TaskList taskList = new TaskList(cpuScheduler);
                break;
            }
            case "next":{
                next();
                break;
            }

        }
    }

    private void processPass(ArrayList<String> params) {
        if (params.size() > 0) {
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
    }

    private void create(ArrayList<String> param) {
        try {

            //Checking if params has inappropriate length
            if (param.size() < 4)
                throw new IllegalArgumentException("Too few arguments");

            // Checking if pid and priority are not integers
            if(!isInteger(param.get(2)) || !isInteger(param.get(3)))
                throw new IllegalArgumentException("Pid and Priority should be integers");

            int pid = Integer.parseInt(param.get(2));
            int priority = Integer.parseInt(param.get(3));

            // Checking if given pid is different than 0
            if (pid == 0)
                throw new IllegalArgumentException("pid should be greater than 0");

            // Checking if pcb with given id exists
            for (Interpreter interpreter : interpreters) {
                if (interpreter.getPcb().PID == pid) {
                    throw new IllegalArgumentException("Give pid is already used");
                }
            }

            String name = param.get(0);
            String filePath = param.get(1);

            PCB pcb = new PCB(pid, priority, State.NEW, name);
            cpuScheduler.addProcess(pcb);
            File file = Filesystem.getFile(filePath);

            if (file == null)
                throw new IllegalArgumentException("Can not found file");

            Interpreter interpreter = new Interpreter(file, pcb);
            interpreters.add(interpreter);
            run();

        }
        catch (IllegalArgumentException e){
            Interface.post(e.getMessage());
        }
    }

    private void run(){
        try {

            PCB runningPcb = cpuScheduler.getRunningPCB();

            if (runningPcb.PID == 0)
                throw new IllegalArgumentException("There is only Idle Process");

            for (Interpreter interpreter : interpreters) {
                if (interpreter.getPcb().PID == runningPcb.PID) {
                    interpreter.runInterpreter();
                    if(!isStepMode) {
                        cpuScheduler.removeProcess(interpreter.getPcb().NAME);
                        interpreters.remove(interpreter);
                    }
                    break;
                }

            }
        }catch (IllegalArgumentException exc){
            Interface.post(exc.getMessage());
        }

    }

    private void kill(ArrayList<String> param) {
        try{
            if (param.size() < 1) throw new IllegalArgumentException("Too few arguments");

            String name = param.get(0);
            boolean removed = false;

            if(cpuScheduler.getRunningPCB().NAME.equals(name)) {
                cpuScheduler.removeProcess(cpuScheduler.getRunningPCB().NAME);
                removed = true;
                run();
            }

            for(Interpreter interpreter:interpreters){
                if(interpreter.getPcb().NAME.equals(name)){
                    interpreters.remove(interpreter);
                    removed = true;
                    break;
                }
            }

            if(cpuScheduler.getReadyPCB()==null)
                throw new IllegalArgumentException("Process with specified name doesn't exist");

            for(PCB pcb:cpuScheduler.getReadyPCB()){
                if(pcb.NAME.equals(name)){
                    cpuScheduler.removeProcess(name);
                    removed = true;
                    break;
                }
            }

            if(!removed)
                throw new IllegalArgumentException("Process with specified name doesn't exist");

        }
        catch (IllegalArgumentException exc){
            Interface.post(exc.getMessage());
        }
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

    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        return true;
    }

    public static boolean getStepMode() {
        return isStepMode;
    }

}
