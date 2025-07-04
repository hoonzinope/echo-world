package home.example.echoLog.controller.api;

import home.example.echoLog.model.dto.CommandRequestDTO;
import home.example.echoLog.model.type.ExecType;
import home.example.echoLog.service.cmd.CdService;
import home.example.echoLog.service.cmd.MkdirService;
import home.example.echoLog.service.cmd.LsService;
import home.example.echoLog.service.cmd.TreeService;
import home.example.echoLog.service.cmd.EchoService;
import home.example.echoLog.util.CommandParser;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CmdAPI {

    private final CommandParser commandParser;
    private final CdService cdService;
    private final MkdirService mkdirService;
    private final LsService lsService;
    private final TreeService treeService;
    private final EchoService echoService;
    public CmdAPI(CommandParser commandParser,
                  CdService cdService,
                  MkdirService mkdirService,
                  LsService lsService,
                  TreeService treeService,
                  EchoService echoService) {
        this.commandParser = commandParser;
        this.cdService = cdService;
        this.mkdirService = mkdirService;
        this.lsService = lsService;
        this.treeService = treeService;
        this.echoService = echoService;
    }

    @PostMapping("/api/cmd") // parameter path, command to execute
    public ResponseEntity<JSONObject> executeCommand(
            @RequestBody CommandRequestDTO commandRequestDTO
            ) {
        JSONObject response = new JSONObject();
        String path = commandRequestDTO.getPath();
        String command = commandRequestDTO.getCommand();
        Map<ExecType, String> parsedCommand = commandParser.parseCommand(path, command);
        if (parsedCommand.containsKey(ExecType.ECHO)) {
            boolean success = echoService.addLog(path, parsedCommand.get(ExecType.ECHO));
            response.put("type", ExecType.ECHO);
            response.put("message", success ? "Log added successfully" : "Failed to add log");
        }
        else if (parsedCommand.containsKey(ExecType.MKDIR)) {
            mkdirService.addDirectory(parsedCommand.get(ExecType.MKDIR));
            response.put("type", ExecType.MKDIR);
            response.put("message", "Directory created: " + parsedCommand.get(ExecType.MKDIR));
        }
        else if (parsedCommand.containsKey(ExecType.CD)) {
            response.put("currentPath", cdService.changeDirectory(parsedCommand.get(ExecType.CD)));
            response.put("type", ExecType.CD);
            response.put("message", "Changed directory to: " + parsedCommand.get(ExecType.CD));
        }
        else if (parsedCommand.containsKey(ExecType.LS)) {
            response.put("directories", lsService.listDirectories(parsedCommand.get(ExecType.LS)));
            response.put("type", ExecType.LS);
            response.put("message", "Listed directories at: " + parsedCommand.get(ExecType.LS));
        }
        else if (parsedCommand.containsKey(ExecType.TREE)) {
            response.put("tree", treeService.getTree(parsedCommand.get(ExecType.TREE)));
            response.put("type", ExecType.TREE);
            response.put("message", "Tree structure at: " + parsedCommand.get(ExecType.TREE));
        }
        else {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok().body(response);
    }
}
