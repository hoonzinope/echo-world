package home.example.echoLog.mapper;

import home.example.echoLog.model.EchoLog;
import home.example.echoLog.model.dto.HistoryRequestDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EchoLogMapper {
    void insertEchoLog(EchoLog log);
    List<EchoLog> getEchoLogsLastN(HistoryRequestDTO historyRequestDTO);
}
