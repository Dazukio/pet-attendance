package pet.attendance.server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public interface ApiHandler extends HttpHandler {

    Gson gson = new Gson();

    default void sendJsonResponse(HttpExchange exchange, int statusCode, Object data) throws IOException {
        String json = gson.toJson(data);

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

        exchange.sendResponseHeaders(statusCode, json.length());

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }
    }

    default void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        Map<String, String> errorResponse = Map.of("error", message);
        sendJsonResponse(exchange, statusCode, errorResponse);
    }

    default Map<String, String> getQueryParams(HttpExchange exchange) {
        Map<String, String> params = new HashMap<>();
        String query = exchange.getRequestURI().getQuery();

        if (query != null) {
            for (String param : query.split("&")) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    params.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return params;
    }

    default Integer extractIdFromPath(String path, String basePath) {
        try {
            if (path.startsWith(basePath)) {
                String idStr = path.substring(basePath.length());
                return Integer.parseInt(idStr);
            }
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            // If the ID is not a number or the path does not match the format
        }
        return null;
    }

    default String getPathSegment(HttpExchange exchange, int segmentIndex) {
        String path = exchange.getRequestURI().getPath();
        String[] segments = path.split("/");

        if (segmentIndex < segments.length) {
            return segments[segmentIndex];
        }
        return null;
    }

    default <T> T parseJsonBody(HttpExchange exchange, Class<T> type) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        return gson.fromJson(body, type);
    }

    default void handleCorsPreflight(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.getResponseHeaders().set("Access-Control-Max-Age", "86400");
        exchange.sendResponseHeaders(200, -1);
    }
}