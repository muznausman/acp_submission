package acp.submission.controller;

import acp.submission.service.PostgresService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/acp")
public class PostgresController {

    private final PostgresService postgresService;

    public PostgresController(PostgresService postgresService) {
        this.postgresService = postgresService;
    }

    @GetMapping("/all/postgres/{table}")
    public ResponseEntity<?> allPostgres(@PathVariable String table) {
        try {
            List<Map<String, Object>> rows = postgresService.readAll(table);
            return ResponseEntity.ok(rows);
        } catch (Exception e) {
            String msg = e.getMessage() == null ? "" : e.getMessage().toLowerCase();

            if (msg.contains("does not exist") || msg.contains("relation")) {
                return ResponseEntity.status(404).build();
            }

            return ResponseEntity.status(404).build();
        }
    }
}
