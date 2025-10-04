package pet.attendance.server.handlers;

import pet.attendance.database.repositories.SubjectRepository;
import pet.attendance.models.Subject;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SubjectHandler implements ApiHandler {
    private final SubjectRepository repository;

    public SubjectHandler(SubjectRepository repository) {
        this.repository = repository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("OPTIONS".equals(method)) {
            handleCorsPreflight(exchange);
            return;
        }

        try {
            switch (method) {
                case "GET":
                    handleGet(exchange, path);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "PUT":
                    handlePut(exchange, path);
                    break;
                case "DELETE":
                    handleDelete(exchange, path);
                    break;
                default:
                    sendError(exchange, 405, "Method not allowed");
            }
        } catch (SQLException e) {
            sendError(exchange, 500, "Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            sendError(exchange, 500, "Internal server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleGet(HttpExchange exchange, String path) throws IOException, SQLException {
        if (path.equals("/api/subjects")) {
            // GET /api/subjects
            List<Subject> subjects = repository.findAll();
            sendJsonResponse(exchange, 200, subjects);
        } else if (path.startsWith("/api/subjects/")) {
            // GET /api/subjects/123
            Integer id = extractIdFromPath(path, "/api/subjects/");
            if (id != null) {
                Optional<Subject> subjectOpt = repository.findById(id);
                if (subjectOpt.isPresent()) {
                    sendJsonResponse(exchange, 200, subjectOpt.get());
                } else {
                    sendError(exchange, 404, "Subject not found");
                }
            } else {
                sendError(exchange, 400, "Invalid subject ID");
            }
        } else {
            sendError(exchange, 404, "Endpoint not found");
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException, SQLException {
        if (exchange.getRequestURI().getPath().equals("/api/subjects")) {
            // POST /api/subjects
            Subject subject = parseJsonBody(exchange, Subject.class);
            repository.create(subject);
            sendJsonResponse(exchange, 201, subject);
        } else {
            sendError(exchange, 404, "Endpoint not found");
        }
    }

    private void handlePut(HttpExchange exchange, String path) throws IOException, SQLException {
        Integer id = extractIdFromPath(path, "/api/subjects/");
        if (id != null) {
            // PUT /api/subjects/123
            Subject subject = parseJsonBody(exchange, Subject.class);
            subject.setId(id);
            repository.update(subject);
            sendJsonResponse(exchange, 200, subject);
        } else {
            sendError(exchange, 400, "Invalid subject ID");
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException, SQLException {
        Integer id = extractIdFromPath(path, "/api/subjects/");
        if (id != null) {
            // DELETE /api/subjects/123
            boolean deleted = repository.delete(id);
            if (deleted) {
                sendJsonResponse(exchange, 200, Map.of("message", "Subject deleted"));
            } else {
                sendError(exchange, 404, "Subject not found");
            }
        } else {
            sendError(exchange, 400, "Invalid subject ID");
        }
    }
}