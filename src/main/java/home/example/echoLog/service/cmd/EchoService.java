package home.example.echoLog.service.cmd;

import home.example.echoLog.mapper.DirectoryMapper;
import home.example.echoLog.mapper.EchoLogMapper;
import home.example.echoLog.model.Directory;
import home.example.echoLog.model.EchoLog;
import home.example.echoLog.service.StreamService;
import org.springframework.stereotype.Service;

@Service
public class EchoService {

    private final EchoLogMapper echoLogMapper;
    private final DirectoryMapper directoryMapper;
    private final StreamService streamService;
    public EchoService(EchoLogMapper echoLogMapper, DirectoryMapper directoryMapper, StreamService streamService) {
        this.echoLogMapper = echoLogMapper;
        this.directoryMapper = directoryMapper;
        this.streamService = streamService;
    }

    public boolean addLog(String path, String message) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        if (message == null || message.isEmpty()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }
        try {
            String[] paths = path.split("/");
            Directory current = directoryMapper.getRootDirectory();
            for (int i = 1; i < paths.length; i++) {
                String pathSegment = paths[i].trim();
                if (pathSegment.equals("..")) {
                    if (current.getParent_id() == null) {
                        throw new IllegalArgumentException("Already at root directory, cannot go up: " + pathSegment);
                    }
                    current = directoryMapper.getDirectoryById(current.getParent_id())
                            .orElseThrow(() -> new IllegalArgumentException("Parent directory does not exist"));
                    continue;
                }
                Directory parameter = Directory.builder()
                        .name(pathSegment)
                        .parent_id(current.getDir_id())
                        .build();
                current = directoryMapper.getDirectoryByName(parameter)
                        .orElseThrow(() -> new IllegalArgumentException("Directory does not exist: " + pathSegment));
            }
            EchoLog echoLog = EchoLog.builder()
                    .dir_id(current.getDir_id())
                    .message(message)
                    .build();
            echoLogMapper.insertEchoLog(echoLog);
        }catch (Exception e) {
            System.err.println("Error adding log: " + e.getMessage());
            return false; // Indicating that the log was not successfully added
        }
        return true; // Indicating that the log was successfully added
    }


    public boolean appendLog(String path, String message) {
        this.addLog(path, message);
        // Optionally, you can also push the log to a stream service if needed
        streamService.pushLogToPath(path, message);
        return true; // Indicating that the log was successfully appended
    }
}
