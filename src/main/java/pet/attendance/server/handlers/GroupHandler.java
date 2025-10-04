package pet.attendance.server.handlers;

import pet.attendance.database.repositories.GroupRepository;
import pet.attendance.models.Group;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GroupHandler implements ApiHandler {
    private final GroupRepository repository;

    public GroupHandler(GroupRepository repository) {
        this.repository = repository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        // CORS preflight
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
        if (path.equals("/api/groups")) {
            // GET /api/groups
            List<Group> groups = repository.findAll();
            sendJsonResponse(exchange, 200, groups);
        } else if (path.startsWith("/api/groups/")) {
            // GET /api/groups/123
            Integer id = extractIdFromPath(path, "/api/groups/");
            if (id != null) {
                Optional<Group> groupOpt = repository.findById(id); // Теперь Optional!
                if (groupOpt.isPresent()) {
                    sendJsonResponse(exchange, 200, groupOpt.get());
                } else {
                    sendError(exchange, 404, "Group not found");
                }
            } else {
                sendError(exchange, 400, "Invalid group ID");
            }
        } else {
            sendError(exchange, 404, "Endpoint not found");
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException, SQLException {
        if (exchange.getRequestURI().getPath().equals("/api/groups")) {
            // POST /api/groups
            Group group = parseJsonBody(exchange, Group.class);
            repository.create(group);
            sendJsonResponse(exchange, 201, group); // 201 Created
        } else {
            sendError(exchange, 404, "Endpoint not found");
        }
    }

    private void handlePut(HttpExchange exchange, String path) throws IOException, SQLException {
        Integer id = extractIdFromPath(path, "/api/groups/");
        if (id != null) {
            // PUT /api/groups/123
            Group group = parseJsonBody(exchange, Group.class);
            group.setId(id);
            repository.update(group);
            sendJsonResponse(exchange, 200, group);
        } else {
            sendError(exchange, 400, "Invalid group ID");
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException, SQLException {
        Integer id = extractIdFromPath(path, "/api/groups/");
        if (id != null) {
            // DELETE /api/groups/123
            repository.delete(id);
            sendJsonResponse(exchange, 200, Map.of("message", "Group deleted"));
        } else {
            sendError(exchange, 400, "Invalid group ID");
        }
    }
}