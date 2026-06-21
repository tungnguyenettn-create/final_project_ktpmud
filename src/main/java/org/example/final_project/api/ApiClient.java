package org.example.final_project.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ApiClient {

    private static final String BASE_URL = "https://citation-spinal-freebase.ngrok-free.dev";

    public static final ObjectMapper JSON = new ObjectMapper();

    // Phải set TRƯỚC khi HttpClient được khởi tạo
    // Cho phép Java gửi body trong GET request (mặc định bị JDK chặn)
    static {
        System.setProperty("jdk.httpclient.allowRestrictedHeaders", "content-length,transfer-encoding");
    }

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static HttpRequest.Builder base(String path) {
        return HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "application/json")
                .header("ngrok-skip-browser-warning", "true");
    }

    public static JsonNode get(String path, String token) throws Exception {
        HttpRequest.Builder b = base(path).GET();
        if (token != null && !token.isBlank())
            b.header("Authorization", "Bearer " + token);
        return send(b.build());
    }

    // GET với JSON body — đúng cách Python requests(json=...) hoạt động
    public static JsonNode getWithBody(String path, String body, String token) throws Exception {
        HttpRequest.Builder b = base(path)
                .method("GET", HttpRequest.BodyPublishers.ofString(body));
        if (token != null && !token.isBlank())
            b.header("Authorization", "Bearer " + token);
        return send(b.build());
    }

    public static JsonNode post(String path, Object bodyObj, String token) throws Exception {
        String body = JSON.writeValueAsString(bodyObj);
        HttpRequest.Builder b = base(path)
                .POST(HttpRequest.BodyPublishers.ofString(body));
        if (token != null && !token.isBlank())
            b.header("Authorization", "Bearer " + token);
        return send(b.build());
    }

    public static JsonNode put(String path, Object bodyObj, String token) throws Exception {
        String body = JSON.writeValueAsString(bodyObj);
        HttpRequest.Builder b = base(path)
                .PUT(HttpRequest.BodyPublishers.ofString(body));
        if (token != null && !token.isBlank())
            b.header("Authorization", "Bearer " + token);
        return send(b.build());
    }

    private static JsonNode send(HttpRequest request) throws Exception {
        HttpResponse<String> resp = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
        return JSON.readTree(resp.body());
    }

    public static boolean isSuccess(JsonNode node) {
        return node != null
                && node.has("status")
                && "success".equals(node.get("status").asText());
    }
}