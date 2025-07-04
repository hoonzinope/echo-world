package home.example.echoLog.controller.api;

import home.example.echoLog.service.HistoryGetService;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HistoryAPI {

    private final HistoryGetService historyGetService;
    public HistoryAPI(HistoryGetService historyGetService) {
        this.historyGetService = historyGetService;
    }

    @GetMapping("/api/history") // Endpoint to retrieve command history
    public ResponseEntity<JSONObject> getHistory(
        @RequestParam (value = "path", required = false) String path,
        @RequestParam (value = "limit", required = false, defaultValue = "10") Integer limit,
        @RequestParam (value = "log_seq", required = false, defaultValue = "-1") Long log_seq
    ){
        JSONObject response = new JSONObject();
        try {
            response = historyGetService.getHistory(path, log_seq, limit);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
        return ResponseEntity.ok(response);
    }
}
