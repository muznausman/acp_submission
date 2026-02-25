package acp.submission.controller;

import acp.submission.service.ProcessService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/acp")
public class ProcessController {

    private final ProcessService processService;

    public ProcessController(ProcessService processService) {
        this.processService = processService;
    }

    @PostMapping("/process/dump")
    public ResponseEntity<?> dump(@RequestBody Map<String, String> body) {

        try {
            String urlPath = body.get("urlPath");

            return ResponseEntity.ok(
                    processService.fetchAndProcess(urlPath)
            );

        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }
}