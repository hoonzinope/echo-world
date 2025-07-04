package home.example.echoLog.util;

import home.example.echoLog.model.type.ExecType;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CommandParser {

    // /echo "hello world"
    // /mkdir "/home/user/newfolder"
    // /cd "/home/user/documents"
    public Map<ExecType, String> parseCommand(String path, String command) {
        if (command == null || command.isEmpty()) {
            throw new IllegalArgumentException("Command cannot be null or empty");
        }
        command = command.trim();
        Map<ExecType, String> result = new HashMap<>();
        String[] commands = command.split("\\s+");
        String exec = commands[0];
        String arg = String.join(" ", java.util.Arrays.copyOfRange(commands, 1, commands.length));
        if (arg.startsWith("\"") && arg.endsWith("\"")) {
            arg = arg.substring(1, arg.length() - 1); // remove quotes
        }
        if (arg.startsWith("'") && arg.endsWith("'")) {
            arg = arg.substring(1, arg.length() - 1); // remove single quotes
        }


        if(!exec.startsWith("/")) { // default command = "echo"
            arg = String.join(" ", commands);
            result.put(ExecType.ECHO, arg);
        }
        else if (exec.equals("/echo")) {
            result.put(ExecType.ECHO, arg);
        } else if(directoryCommands().contains(exec)) {
            arg = getArgs(path, arg); // prepend path to argument

            if (exec.equals("/mkdir")) {
                result.put(ExecType.MKDIR, arg);
            } else if (exec.equals("/cd")) {
                result.put(ExecType.CD, arg);
            } else if (exec.equals("/ls")) {
                result.put(ExecType.LS, path);
            } else if (exec.equals("/tree")) {
                result.put(ExecType.TREE, path);
            }
        } else {
            throw new IllegalArgumentException("Unknown command: " + exec);
        }
        return result;
    }

    private Set<String> directoryCommands() {
        Set<String> commandSet = new HashSet<>();
        commandSet.add("/echo");
        commandSet.add("/mkdir");
        commandSet.add("/cd");
        commandSet.add("/ls");
        commandSet.add("/tree");

        return commandSet;
    }

    public String getArgs(String path, String arg){
        if(path.endsWith("/")){
            if(arg.startsWith("/")) {
                return path + arg.substring(1); // prepend path if argument starts with "/"
            }else{
                return path + arg; // prepend path if argument does not start with "/"
            }
        }else{
            if(arg.startsWith("/")) {
                return path + arg; // prepend path if argument starts with "/"
            }else{
                return path + "/" + arg; // prepend path if argument does not start with "/"
            }
        }
    }
}
