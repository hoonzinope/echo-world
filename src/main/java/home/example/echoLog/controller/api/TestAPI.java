package home.example.echoLog.controller.api;

import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestAPI {

    @GetMapping("/api/test")
    public ResponseEntity<JSONObject> test() {
        JSONObject response = new JSONObject();
        response.put("message", "Hello, World!");
        return ResponseEntity.ok(response);
    }

}
