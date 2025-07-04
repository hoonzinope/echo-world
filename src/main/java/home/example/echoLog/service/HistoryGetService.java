package home.example.echoLog.service;

import home.example.echoLog.mapper.DirectoryMapper;
import home.example.echoLog.mapper.EchoLogMapper;
import home.example.echoLog.model.Directory;
import home.example.echoLog.model.EchoLog;
import home.example.echoLog.model.dto.HistoryRequestDTO;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoryGetService {

    private final EchoLogMapper echoLogMapper;
    private final DirectoryMapper directoryMapper;
    public HistoryGetService(EchoLogMapper echoLogMapper,
                             DirectoryMapper directoryMapper) {
        this.echoLogMapper = echoLogMapper;
        this.directoryMapper = directoryMapper;
    }

    public JSONObject getHistory(String path, Long log_seq, Integer limit) {
        try {
            if (path == null || path.isEmpty()) {
                throw new IllegalArgumentException("Path cannot be null or empty");
            }
            if (log_seq < 0) {
                log_seq = null;
            }
            String[] paths = path.split("/");
            Directory current = directoryMapper.getRootDirectory();
            for (int i = 1; i < paths.length; i++) {
                String p = paths[i];
                if (p.equals("..")) {
                    if (current.getParent_id() == null) {
                        throw new IllegalArgumentException("Already at root directory, cannot go up: " + p);
                    }
                    current = directoryMapper.getDirectoryById(current.getParent_id())
                            .orElseThrow(() -> new IllegalArgumentException("Parent directory does not exist"));
                    continue;
                }
                Directory parameter = Directory.builder()
                        .name(p)
                        .parent_id(current.getDir_id())
                        .build();
                current = directoryMapper.getDirectoryByName(parameter)
                        .orElseThrow(() -> new IllegalArgumentException("Directory does not exist: " + p));
            }
            // Assuming echoLogMapper is injected and available
            HistoryRequestDTO request = HistoryRequestDTO.builder()
                    .dir_id(current.getDir_id())
                    .log_seq(log_seq)
                    .limit(limit)
                    .build();
            return createResponse(echoLogMapper.getEchoLogsByDirId(request));
        }catch (Exception e) {
            return createErrorResponse(e.getMessage());
        }
    }

    private JSONObject createResponse(List<EchoLog> logs) {
        JSONObject response = new JSONObject();

        long min_log_seq = logs.isEmpty() ? 0 : logs.get(logs.size() - 1).getLog_seq();
        JSONArray logsArray = new JSONArray();
        logs.forEach(log -> {
            JSONObject logJson = new JSONObject();
            logJson.put("log_seq", log.getLog_seq());
            logJson.put("message", log.getMessage());
            logJson.put("created_at", log.getCreated_at());
            logsArray.add(logJson);
        });

        response.put("status", "success");
        response.put("logs", logsArray);
        response.put("min_log_seq", min_log_seq);
        return response;
    }

    private JSONObject createErrorResponse(String message) {
        JSONObject response = new JSONObject();
        response.put("status", "error");
        response.put("message", message);
        return response;
    }
}
