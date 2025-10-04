package pet.attendance.server.handlers;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import io.swagger.v3.core.util.Json;
import pet.attendance.config.SwaggerConfig;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SwaggerHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String openApiJson = Json.pretty(SwaggerConfig.createOpenAPI());

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, openApiJson.getBytes(StandardCharsets.UTF_8).length);
            exchange.getResponseBody().write(openApiJson.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            String error = "{\"error\": \"Failed to generate Swagger JSON: " + e.getMessage() + "\"}";
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(500, error.length());
            exchange.getResponseBody().write(error.getBytes());
        } finally {
            exchange.close();
        }
    }
}