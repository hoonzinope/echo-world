package home.example.echoLog.model.dto;

import lombok.Data;

@Data
public class AppendLogRequestDTO {
    private String path;
    private String message;
}
