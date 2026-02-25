package acp.submission.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class ProcessService {

    private final String baseUrl;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public ProcessService(@Value("${ACP_URL_ENDPOINT}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public JsonNode fetchAndProcess(String urlPath)
            throws IOException, InterruptedException {

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + urlPath))
                .GET()
                .build();

        HttpResponse<String> res =
                httpClient.send(req, HttpResponse.BodyHandlers.ofString());

        if (res.statusCode() != 200) {
            throw new RuntimeException("External API returned " + res.statusCode());
        }

        JsonNode root = mapper.readTree(res.body());

        for (JsonNode drone : root) {

            JsonNode capability = drone.get("capability");

            double costInitial = safe(capability.get("costInitial"));
            double costFinal = safe(capability.get("costFinal"));
            double costPerMove = safe(capability.get("costPerMove"));

            double costPer100Moves =
                    costInitial + costFinal + costPerMove * 100;

            ((ObjectNode) drone)
                    .put("costPer100Moves", costPer100Moves);
        }

        return root;
    }

    private double safe(JsonNode node) {
        if (node == null || node.isNull()) return 0.0;

        // if it's numeric, read it; if it becomes NaN, treat as 0
        if (node.isNumber()) {
            double v = node.asDouble();
            return Double.isNaN(v) ? 0.0 : v;
        }

        // sometimes numbers come as strings
        if (node.isTextual()) {
            try {
                double v = Double.parseDouble(node.asText());
                return Double.isNaN(v) ? 0.0 : v;
            } catch (Exception ignored) {
                return 0.0;
            }
        }

        return 0.0;
    }
}