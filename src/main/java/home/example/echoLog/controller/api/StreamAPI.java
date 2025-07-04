package home.example.echoLog.controller.api;

import home.example.echoLog.service.StreamService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class StreamAPI {
    // server sent events (SSE) endpoint for streaming logs
    // This is a placeholder for the actual implementation of the streaming API.
    // You can implement methods to handle streaming logs, such as:
    // - @GetMapping("/api/stream/logs") to stream logs in real-time

    private final StreamService streamService;
    public StreamAPI(StreamService streamService) {
        this.streamService = streamService;
    }

    @GetMapping(value="/api/stream/logs", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamLogs(
            @RequestParam(name = "path") String path) {
        System.out.println("StreamAPI.streamLogs called with path: " + path);
        // path decode
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        // Add the emitter to the stream service
        return streamService.addEmitter(path);
    }
}
