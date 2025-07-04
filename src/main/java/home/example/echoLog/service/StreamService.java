package home.example.echoLog.service;

import home.example.echoLog.mapper.DirectoryMapper;
import home.example.echoLog.model.Directory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class StreamService {

    //  path 별로 SSE 연결들을 관리
    private final Map<String, List<SseEmitter>> emitterMap = new ConcurrentHashMap<>();

    private final DirectoryMapper directoryMapper;
    public StreamService(DirectoryMapper directoryMapper) {
        this.directoryMapper = directoryMapper;
    }

    public SseEmitter addEmitter(String path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        String pathId = getDirectoryIdFromPath(path);
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        emitterMap.computeIfAbsent(pathId, pid -> new CopyOnWriteArrayList<>()).add(emitter);
        // SSE 연결이 끊어지면 리스트에서 제거
        emitter.onCompletion(() -> {
            emitterMap.get(pathId).remove(emitter);
            System.out.println("SSE completed for path: " + path + " - emitter removed");
        });
        emitter.onTimeout(() -> {
            emitter.complete();
            System.out.println("SSE timeout for path: " + path + " - emitter completed");
        });

        return emitter;
    }

    // 예시: echo가 insert 된 후 이 메소드로 push
    public void pushLogToPath(String path, String message) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        if (message == null || message.isEmpty()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }
        String pathId = getDirectoryIdFromPath(path);
        List<SseEmitter> emitters = emitterMap.getOrDefault(pathId, new CopyOnWriteArrayList<>());
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("message")
                        .data(message));
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }
    }


    private String getDirectoryIdFromPath(String path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        Directory current = directoryMapper.getRootDirectory();
        String[] paths = path.split("/");
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
        return current.getDir_id().toString();
    }
}
