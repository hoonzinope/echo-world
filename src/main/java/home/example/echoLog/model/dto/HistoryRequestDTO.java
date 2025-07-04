package home.example.echoLog.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HistoryRequestDTO {
    private Long dir_id;
    private Long log_seq;
    private int limit;
}
