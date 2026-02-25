package acp.submission.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DynamoService {

    private final DynamoDbClient dynamoDbClient;

    public DynamoService(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    // ✅ This now returns normal JSON-friendly maps
    public List<Map<String, Object>> readAll(String table) {
        ScanRequest request = ScanRequest.builder()
                .tableName(table)
                .build();

        ScanResponse response = dynamoDbClient.scan(request);

        return response.items().stream()
                .map(this::convertItem)
                .collect(Collectors.toList());
    }
    // ✅ Step 2B: Read ONE item by id
    public Map<String, Object> readOne(String table, String id) {

        GetItemRequest req = GetItemRequest.builder()
                .tableName(table)
                .key(Map.of("id", AttributeValue.builder().s(id).build()))
                .build();

        GetItemResponse res = dynamoDbClient.getItem(req);

        // if not found, return empty map (controller can turn this into 404)
        if (res.item() == null || res.item().isEmpty()) {
            return Collections.emptyMap();
        }

        return convertItem(res.item());
    }

    private Map<String, Object> convertItem(Map<String, AttributeValue> item) {
        Map<String, Object> out = new LinkedHashMap<>();
        for (Map.Entry<String, AttributeValue> entry : item.entrySet()) {
            out.put(entry.getKey(), attrToObject(entry.getValue()));
        }
        return out;
    }

    private Object attrToObject(AttributeValue av) {
        if (av == null) return null;

        // String
        if (av.s() != null) return av.s();

        // Number (return BigDecimal so JSON becomes number)
        if (av.n() != null) return new BigDecimal(av.n());

        // Boolean
        if (av.bool() != null) return av.bool();

        // Null
        if (Boolean.TRUE.equals(av.nul())) return null;

        // Map
        if (av.m() != null && !av.m().isEmpty()) {
            Map<String, Object> m = new LinkedHashMap<>();
            for (Map.Entry<String, AttributeValue> e : av.m().entrySet()) {
                m.put(e.getKey(), attrToObject(e.getValue()));
            }
            return m;
        }

        // List
        if (av.l() != null && !av.l().isEmpty()) {
            List<Object> list = new ArrayList<>();
            for (AttributeValue v : av.l()) list.add(attrToObject(v));
            return list;
        }

        // String Set
        if (av.ss() != null && !av.ss().isEmpty()) return av.ss();

        // Number Set
        if (av.ns() != null && !av.ns().isEmpty()) {
            return av.ns().stream().map(BigDecimal::new).collect(Collectors.toList());
        }

        // Binary sets etc (rare for your assignment)
        // Boolean
        if (av.bool() != null) return av.bool();

// Binary (rare for your assignment)
        if (av.b() != null) return av.b().asByteArray();
        if (av.bs() != null && !av.bs().isEmpty()) {
            return av.bs().stream().map(b -> b.asByteArray()).collect(Collectors.toList());
        }

        // Fallback: empty map rather than {}
        return null;
    }
}