package home.example.echoLog.model.dto;

import lombok.Data;

@Data
public class CommandRequestDTO {
    private String path;
    private String command;
}
