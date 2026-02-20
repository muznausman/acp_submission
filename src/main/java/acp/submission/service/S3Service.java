package acp.submission.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class S3Service {

    private final S3Client s3Client;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String readOne(String bucket, String key) {
        GetObjectRequest req = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        ResponseBytes<GetObjectResponse> bytes =
                s3Client.getObject(req, ResponseTransformer.toBytes());

        return bytes.asString(StandardCharsets.UTF_8);
    }

    public List<String> readAll(String bucket) {
        ListObjectsV2Response list = s3Client.listObjectsV2(
                ListObjectsV2Request.builder().bucket(bucket).build()
        );

        List<String> results = new ArrayList<>();
        for (S3Object obj : list.contents()) {
            results.add(readOne(bucket, obj.key()));
        }
        return results;
    }
}

