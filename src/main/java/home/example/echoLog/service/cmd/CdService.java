package home.example.echoLog.service.cmd;

import home.example.echoLog.mapper.DirectoryMapper;
import home.example.echoLog.model.Directory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CdService {
    private final DirectoryMapper directoryMapper;
    public CdService(DirectoryMapper directoryMapper) {
        this.directoryMapper = directoryMapper;
    }

    public Directory changeDirectory(String path) {
        if(path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        String[] paths = path.split("/");
        Directory current = directoryMapper.getDirectoryByName(rootDirectory())
                .orElseThrow(() -> new IllegalArgumentException("Root directory does not exist"));
        for(int i = 1; i < paths.length - 1; i++) {
            String p = paths[i];
            Directory parameter = Directory.builder()
                    .name(p)
                    .parent_id(current.getDir_id())
                    .build();
            Optional<Directory> next = directoryMapper.getDirectoryByName(parameter);
            if (!next.isPresent()) {
                throw new IllegalArgumentException("Directory does not exist: " + p);
            }
            current = next.get();
        }
        return current;
    }

    private Directory rootDirectory() {
        return directoryMapper.getRootDirectory();
    }
}
