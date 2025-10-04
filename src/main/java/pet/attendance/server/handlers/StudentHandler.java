package pet.attendance.server.handlers;

import pet.attendance.database.repositories.StudentRepository;
import pet.attendance.models.Student;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StudentHandler implements ApiHandler {
    private final StudentRepository repository;

    public StudentHandler(StudentRepository repository) {
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
        if (path.equals("/api/students")) {
            // GET /api/students
            Map<String, String> params = getQueryParams(exchange);
            List<Student> students;

            // Фильтрация по группе если указан group_id
            String groupIdParam = params.get("group_id");
            if (groupIdParam != null) {
                int groupId = Integer.parseInt(groupIdParam);
                students = repository.findByGroupId(groupId);
            } else {
                students = repository.findAll();
            }

            sendJsonResponse(exchange, 200, students);
        } else if (path.startsWith("/api/students/")) {
            // GET /api/students/123
            Integer id = extractIdFromPath(path, "/api/students/");
            if (id != null) {
                Optional<Student> studentOpt = repository.findById(id);
                if (studentOpt.isPresent()) {
                    sendJsonResponse(exchange, 200, studentOpt.get());
                } else {
                    sendError(exchange, 404, "Student not found");
                }
            } else {
                sendError(exchange, 400, "Invalid student ID");
            }
        } else {
            sendError(exchange, 404, "Endpoint not found");
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException, SQLException {
        if (exchange.getRequestURI().getPath().equals("/api/students")) {
            // POST /api/students
            Student student = parseJsonBody(exchange, Student.class);
            repository.create(student);
            sendJsonResponse(exchange, 201, student);
        } else {
            sendError(exchange, 404, "Endpoint not found");
        }
    }

    private void handlePut(HttpExchange exchange, String path) throws IOException, SQLException {
        Integer id = extractIdFromPath(path, "/api/students/");
        if (id != null) {
            // PUT /api/students/123
            Student student = parseJsonBody(exchange, Student.class);
            student.setId(id);
            repository.update(student);
            sendJsonResponse(exchange, 200, student);
        } else {
            sendError(exchange, 400, "Invalid student ID");
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException, SQLException {
        Integer id = extractIdFromPath(path, "/api/students/");
        if (id != null) {
            // DELETE /api/students/123
            boolean deleted = repository.delete(id);
            if (deleted) {
                sendJsonResponse(exchange, 200, Map.of("message", "Student deleted"));
            } else {
                sendError(exchange, 404, "Student not found");
            }
        } else {
            sendError(exchange, 400, "Invalid student ID");
        }
    }
}