package home.example.echoLog.service.cmd;

import home.example.echoLog.mapper.DirectoryMapper;
import home.example.echoLog.model.Directory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LsService {
    private final DirectoryMapper directoryMapper;
    public LsService(DirectoryMapper directoryMapper) {
        this.directoryMapper = directoryMapper;
    }

    public List<Directory> listDirectories(String path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        String[] paths = path.split("/");
        Directory current = rootDirectory();
        for(int i = 1; i < paths.length; i++) {
            String p = paths[i];
            if(p.equals("..")){
                if(current.getParent_id() == null) {
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
            Optional<Directory> next = directoryMapper.getDirectoryByName(parameter);
            if (!next.isPresent()) {
                throw new IllegalArgumentException("Directory does not exist: " + p);
            }
            current = next.get();
        }
        return  directoryMapper.getChildrenDirectories(current.getDir_id());
    }

    private Directory rootDirectory() {
        return directoryMapper.getRootDirectory();
    }
}
