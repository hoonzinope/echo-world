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
        Directory current = rootDirectory();
        String resultPath = "";
        for(int i = 1; i < paths.length; i++) {
            String p = paths[i];
            if(p.equals("..")){
                if(current.getParent_id() == null) {
                    throw new IllegalArgumentException("Already at root directory, cannot go up: " + p);
                }
                current = directoryMapper.getDirectoryById(current.getParent_id())
                        .orElseThrow(() -> new IllegalArgumentException("Parent directory does not exist"));
                resultPath = resultPath.substring(0, resultPath.lastIndexOf('/'));
                continue;
            }
            Directory parameter = Directory.builder()
                    .name(p)
                    .parent_id(current.getDir_id())
                    .build();
            Optional<Directory> next = directoryMapper.getDirectoryByName(parameter);
            if (!next.isPresent()) {
                throw new IllegalArgumentException("Directory does not exist: " + p);
            }
            resultPath += "/" + p;
            System.out.println(resultPath);
            current = next.get();
        }
        current.setName(resultPath);
        return current;
    }

    private Directory rootDirectory() {
        return directoryMapper.getRootDirectory();
    }
}
