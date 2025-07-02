package home.example.echoLog.controller.api;

import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HistoryAPI {

    @GetMapping("/api/history") // Endpoint to retrieve command history
    public ResponseEntity<JSONObject> getHistory(
        @RequestParam (value = "path", required = false) String path,
        @RequestParam (value = "limit", required = false, defaultValue = "10") int limit,
        @RequestParam (value = "offset", required = false, defaultValue = "0") int offset
    ){
        JSONObject response = new JSONObject();
        try {
            // Simulate fetching command history
            String history = "Command history for path: " + path + ", limit: " + limit + ", offset: " + offset; // Replace with actual history retrieval logic
            response.put("status", "success");
            response.put("history", history);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}
