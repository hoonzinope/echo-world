package home.example.echoLog.mapper;

import home.example.echoLog.model.Directory;
import home.example.echoLog.model.EchoLog;
import home.example.echoLog.model.dto.HistoryRequestDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface EchoLogMapper {
    void insertEchoLog(EchoLog echoLog);
    List<EchoLog> getEchoLogsByDirId(HistoryRequestDTO historyRequestDTO);
}
