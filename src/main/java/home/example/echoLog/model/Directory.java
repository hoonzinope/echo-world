package home.example.echoLog.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Directory {
    private Long dir_id;
    private String name;
    private Long parent_id;
    private LocalDateTime created_at;
}
