package pet.attendance.server.handlers;

import pet.attendance.database.repositories.ProfessorRepository;
import pet.attendance.models.Professor;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProfessorHandler implements ApiHandler {
    private final ProfessorRepository repository;

    public ProfessorHandler(ProfessorRepository repository) {
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
        if (path.equals("/api/professors")) {
            // GET /api/professors
            List<Professor> professors = repository.findAll();
            sendJsonResponse(exchange, 200, professors);
        } else if (path.startsWith("/api/professors/")) {
            // GET /api/professors/123
            Integer id = extractIdFromPath(path, "/api/professors/");
            if (id != null) {
                Optional<Professor> professorOpt = repository.findById(id);
                if (professorOpt.isPresent()) {
                    sendJsonResponse(exchange, 200, professorOpt.get());
                } else {
                    sendError(exchange, 404, "Professor not found");
                }
            } else {
                sendError(exchange, 400, "Invalid professor ID");
            }
        } else {
            sendError(exchange, 404, "Endpoint not found");
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException, SQLException {
        if (exchange.getRequestURI().getPath().equals("/api/professors")) {
            // POST /api/professors
            Professor professor = parseJsonBody(exchange, Professor.class);
            repository.create(professor);
            sendJsonResponse(exchange, 201, professor);
        } else {
            sendError(exchange, 404, "Endpoint not found");
        }
    }

    private void handlePut(HttpExchange exchange, String path) throws IOException, SQLException {
        Integer id = extractIdFromPath(path, "/api/professors/");
        if (id != null) {
            // PUT /api/professors/123
            Professor professor = parseJsonBody(exchange, Professor.class);
            professor.setId(id);
            repository.update(professor);
            sendJsonResponse(exchange, 200, professor);
        } else {
            sendError(exchange, 400, "Invalid professor ID");
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException, SQLException {
        Integer id = extractIdFromPath(path, "/api/professors/");
        if (id != null) {
            // DELETE /api/professors/123
            boolean deleted = repository.delete(id);
            if (deleted) {
                sendJsonResponse(exchange, 200, Map.of("message", "Professor deleted"));
            } else {
                sendError(exchange, 404, "Professor not found");
            }
        } else {
            sendError(exchange, 400, "Invalid professor ID");
        }
    }
}