package home.example.echoLog.model.dto;

import lombok.Data;

@Data
public class HistoryRequestDTO {
    private Long dir_id;
    private Long log_seq;
    private Integer limit;
}
