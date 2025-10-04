package pet.attendance.server.handlers;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class StaticHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        try {
            // Determining which file is being requested
            String resourcePath;
            if ("/docs".equals(path) || "/docs/".equals(path)) {
                resourcePath = "swagger.html"; // /docs -> swagger.html
            } else if (path.startsWith("/docs/")) {
                resourcePath = path.substring(6); // removing "/docs/" from path
                if (resourcePath.isEmpty())
                    resourcePath = "swagger.html";
            } else if (path.startsWith("/static/")) {
                resourcePath = path.substring(8); // removing "/static/"from path
            } else {
                resourcePath = path.substring(1); // removing /
            }

            // Looking for classpath in static
            InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("static/" + resourcePath);

            if (resourceStream != null) {
                byte[] fileBytes = resourceStream.readAllBytes();
                String contentType = getContentType(resourcePath);

                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(200, fileBytes.length);
                exchange.getResponseBody().write(fileBytes);
            } else {
                sendError(exchange, 404, "File not found: static/" + resourcePath);
            }
        } catch (Exception e) {
            sendError(exchange, 500, "Internal server error: " + e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private String getContentType(String filename) {
        if (filename.endsWith(".html"))
            return "text/html";
        if (filename.endsWith(".css"))
            return "text/css";
        if (filename.endsWith(".js"))
            return "application/javascript";
        if (filename.endsWith(".json"))
            return "application/json";
        return "text/plain";
    }

    private void sendError(HttpExchange exchange, int code, String message) throws IOException {
        String response = "{\"error\":\"" + message + "\"}";
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(code, response.length());
        exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
    }
}