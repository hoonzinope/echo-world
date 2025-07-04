package home.example.echoLog.controller.api;

import home.example.echoLog.model.dto.AppendLogRequestDTO;
import home.example.echoLog.service.cmd.EchoService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppendLogAPI {
    private final EchoService echoService;
    public AppendLogAPI(EchoService echoService) {
        this.echoService = echoService;
    }

    @PostMapping("/api/appendLog")
    public void appendLog(
            @RequestBody AppendLogRequestDTO appendLogRequestDTO
            ) {
        String path = appendLogRequestDTO.getPath();
        String message = appendLogRequestDTO.getMessage();
        echoService.appendLog(path, message);
    }
}
