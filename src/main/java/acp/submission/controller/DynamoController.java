package acp.submission.controller;

import acp.submission.service.DynamoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/acp")
public class DynamoController {

    private final DynamoService dynamoService;

    public DynamoController(DynamoService dynamoService) {
        this.dynamoService = dynamoService;
    }

    @GetMapping("/all/dynamo/{table}")
    public ResponseEntity<?> all(@PathVariable String table) {
        try {
            List<Map<String, Object>> items = (List) dynamoService.readAll(table);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }
}