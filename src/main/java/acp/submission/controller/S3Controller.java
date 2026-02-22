package acp.submission.controller;

import acp.submission.service.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/acp")
public class S3Controller {

    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @GetMapping("/single/s3/{bucket}/{key}")
    public ResponseEntity<?> single(@PathVariable String bucket, @PathVariable String key) {
        try {
            return ResponseEntity.ok(s3Service.readOne(bucket, key));
        } catch (Exception e) {
            e.printStackTrace();   // 👈 ADD THIS LINE
            return ResponseEntity.status(404).body(e.toString());
        }
    }

    @GetMapping("/all/s3/{bucket}")
    public ResponseEntity<?> all(@PathVariable String bucket) {
        try {
            List<String> all = s3Service.readAll(bucket);
            return ResponseEntity.ok(all);
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }
}
