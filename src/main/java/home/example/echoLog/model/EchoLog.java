package home.example.echoLog.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EchoLog {
    private Long log_seq;
    private Long dir_id;
    private String message;
    private LocalDateTime created_at;
}
