package home.example.echoLog.service.cmd;

import home.example.echoLog.mapper.DirectoryMapper;
import home.example.echoLog.model.Directory;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TreeService {
    private final DirectoryMapper directoryMapper;
    public TreeService(DirectoryMapper directoryMapper) {
        this.directoryMapper = directoryMapper;
    }

    public JSONObject getTree(String path) {
        Directory directory = resolvePath(path);
        return buildTreeJSON(directory.getDir_id(), 3);
    }

    private Directory resolvePath(String path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        String[] paths = path.split("/");
        Directory current = directoryMapper.getDirectoryByName(directoryMapper.getRootDirectory())
                .orElseThrow(() -> new IllegalArgumentException("Root directory does not exist"));
        for (int i = 1; i < paths.length - 1; i++) {
            String p = paths[i];
            Directory parameter = Directory.builder()
                    .name(p)
                    .parent_id(current.getDir_id())
                    .build();
            current = directoryMapper.getDirectoryByName(parameter)
                    .orElseThrow(() -> new IllegalArgumentException("Directory does not exist: " + p));
        }
        return current;
    }

    private JSONObject buildTreeJSON(Long dir_id, Integer limit) {
        if (dir_id == null) {
            throw new IllegalArgumentException("Directory ID cannot be null");
        }
        int paramLimit = limit != null ? limit : 3; // Default limit if not provided
        JSONObject node = new JSONObject();
        Directory directory = directoryMapper.getDirectoryById(dir_id)
                .orElseThrow(() -> new IllegalArgumentException("Directory does not exist: " + dir_id));
        List<Directory> childrenDirectories = directoryMapper.getChildrenDirectories(dir_id);
        if( childrenDirectories.isEmpty()) {
            node.put("name", directory.getName());
            node.put("dir_id", directory.getDir_id());
            node.put("children", childrenDirectories);
        }else{
            node.put("name", directory.getName());
            node.put("dir_id", directory.getDir_id());
            node.put("children", childrenDirectories.stream()
                    .map(child -> buildTreeJSON(child.getDir_id(), paramLimit-1))
                    .collect(Collectors.toList()));
        }
        return node;
    }
}
