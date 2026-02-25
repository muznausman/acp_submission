package acp.submission.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.util.List;
import java.util.Map;

@Service
public class DynamoService {

    private final DynamoDbClient dynamoDbClient;

    public DynamoService(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public List<Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue>> readAll(String table) {

        ScanRequest request = ScanRequest.builder()
                .tableName(table)
                .build();

        ScanResponse response = dynamoDbClient.scan(request);

        return response.items();
    }
}