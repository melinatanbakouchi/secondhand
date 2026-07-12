package ir.secondhand.frontend.client;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ir.secondhand.frontend.config.ApiConfig;
import ir.secondhand.frontend.session.SessionManager;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.util.UUID;

/**
 * تنها نقطه ارتباط Frontend با Backend. تمام درخواست‌ها از طریق HttpClient
 * استاندارد جاوا و بر پایه JSON (به‌جز آپلود تصویر که multipart است) ارسال
 * می‌شوند و پاسخ‌ها طبق قالب استاندارد ApiResponse سرور تفسیر می‌گردند.
 */
public final class ApiClient {

    private static final ApiClient INSTANCE = new ApiClient();

    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    private ApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    public static ApiClient getInstance() {
        return INSTANCE;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public <T> T get(String path, Class<T> type) throws ApiException {
        return send("GET", path, null, mapper.constructType(type));
    }

    public <T> T get(String path, JavaType type) throws ApiException {
        return send("GET", path, null, type);
    }

    public <T> T post(String path, Object body, Class<T> type) throws ApiException {
        return send("POST", path, body, mapper.constructType(type));
    }

    public <T> T post(String path, Object body, JavaType type) throws ApiException {
        return send("POST", path, body, type);
    }

    public void post(String path, Object body) throws ApiException {
        send("POST", path, body, mapper.constructType(Void.class));
    }

    public <T> T put(String path, Object body, Class<T> type) throws ApiException {
        return send("PUT", path, body, mapper.constructType(type));
    }

    public <T> T patch(String path, Object body, Class<T> type) throws ApiException {
        return send("PATCH", path, body, mapper.constructType(type));
    }

    public void delete(String path) throws ApiException {
        send("DELETE", path, null, mapper.constructType(Void.class));
    }

    public <T> T uploadFile(String path, File file, Class<T> type) throws ApiException {
        try {
            String boundary = "----SecondHandBoundary" + UUID.randomUUID();
            byte[] multipartBody = buildMultipartBody(file, boundary);

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(ApiConfig.BASE_URL + path))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(multipartBody));
            attachAuthHeader(builder);

            HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            return parseResponse(response, mapper.constructType(type));
        } catch (IOException | InterruptedException ex) {
            throw new ApiException("امکان اتصال به سرور وجود ندارد. لطفا اتصال شبکه را بررسی کنید.", ex);
        }
    }

    private byte[] buildMultipartBody(File file, String boundary) throws IOException {
        String fileName = file.getName();
        String contentType = guessContentType(fileName);
        byte[] fileBytes = Files.readAllBytes(file.toPath());

        StringBuilder header = new StringBuilder();
        header.append("--").append(boundary).append("\r\n");
        header.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(fileName).append("\"\r\n");
        header.append("Content-Type: ").append(contentType).append("\r\n\r\n");

        String footer = "\r\n--" + boundary + "--\r\n";

        byte[] headerBytes = header.toString().getBytes(StandardCharsets.UTF_8);
        byte[] footerBytes = footer.getBytes(StandardCharsets.UTF_8);

        byte[] result = new byte[headerBytes.length + fileBytes.length + footerBytes.length];
        System.arraycopy(headerBytes, 0, result, 0, headerBytes.length);
        System.arraycopy(fileBytes, 0, result, headerBytes.length, fileBytes.length);
        System.arraycopy(footerBytes, 0, result, headerBytes.length + fileBytes.length, footerBytes.length);
        return result;
    }

    private String guessContentType(String fileName) {
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".png")) {
            return "image/png";
        }
        if (lower.endsWith(".webp")) {
            return "image/webp";
        }
        return "image/jpeg";
    }

    private <T> T send(String method, String path, Object body, JavaType type) throws ApiException {
        try {
            HttpRequest.BodyPublisher bodyPublisher = body == null
                    ? HttpRequest.BodyPublishers.noBody()
                    : HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body), StandardCharsets.UTF_8);

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(ApiConfig.BASE_URL + path))
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .method(method, bodyPublisher);
            attachAuthHeader(builder);

            HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            return parseResponse(response, type);
        } catch (IOException | InterruptedException ex) {
            throw new ApiException("امکان اتصال به سرور وجود ندارد. لطفا اتصال شبکه را بررسی کنید.", ex);
        }
    }

    private void attachAuthHeader(HttpRequest.Builder builder) {
        String token = SessionManager.getInstance().getToken();
        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T parseResponse(HttpResponse<String> response, JavaType type) throws ApiException {
        String bodyText = response.body();
        JsonNode root;
        try {
            root = (bodyText == null || bodyText.isBlank()) ? mapper.createObjectNode() : mapper.readTree(bodyText);
        } catch (IOException ex) {
            throw new ApiException("پاسخ سرور قابل تفسیر نبود.", ex);
        }

        boolean success = root.path("success").asBoolean(response.statusCode() < 400);
        String message = root.path("message").asText("");

        if (!success || response.statusCode() >= 400) {
            throw new ApiException(message.isBlank() ? "خطایی در سرور رخ داده است." : message);
        }

        JsonNode dataNode = root.get("data");
        if (dataNode == null || dataNode.isNull()) {
            return null;
        }
        if (type.getRawClass().equals(Void.class)) {
            return null;
        }
        try {
            return mapper.convertValue(dataNode, type);
        } catch (IllegalArgumentException ex) {
            throw new ApiException("ساختار داده دریافتی نامعتبر است.", ex);
        }
    }
}
