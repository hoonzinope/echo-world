package home.example.echoLog.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EchoLog {
    private Long log_seq;
    private Long dir_id;
    private String message;
    private LocalDateTime created_at;
}
