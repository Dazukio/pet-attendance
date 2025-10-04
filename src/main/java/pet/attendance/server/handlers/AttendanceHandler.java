package pet.attendance.server.handlers;

import pet.attendance.database.repositories.AttendanceRepository;
import pet.attendance.models.AttendanceRecord;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class AttendanceHandler implements ApiHandler {
    private final AttendanceRepository repository;

    public AttendanceHandler(AttendanceRepository repository) {
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
        if (path.equals("/api/attendance")) {
            // GET /api/attendance?date=2024-01-15&subject_id=1&group_id=1
            Map<String, String> params = getQueryParams(exchange);

            String dateParam = params.get("date");
            String subjectIdParam = params.get("subject_id");
            String groupIdParam = params.get("group_id");

            if (dateParam != null && subjectIdParam != null && groupIdParam != null) {
                LocalDate date = LocalDate.parse(dateParam);
                int subjectId = Integer.parseInt(subjectIdParam);
                int groupId = Integer.parseInt(groupIdParam);

                List<AttendanceRecord> records = repository.findByDateAndSubject(date, subjectId, groupId);
                sendJsonResponse(exchange, 200, records);
            } else {
                sendError(exchange, 400, "Missing parameters: date, subject_id, group_id are required");
            }
        } else {
            sendError(exchange, 404, "Endpoint not found");
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException, SQLException {
        if (exchange.getRequestURI().getPath().equals("/api/attendance")) {
            // POST /api/attendance - massive retention of attendance
            AttendanceRequest request = parseJsonBody(exchange, AttendanceRequest.class);

            // Deleting old records for this date+subject+group
            repository.deleteByDateAndSubject(request.date, request.subjectId, request.groupId);

            // Saving new entries
            repository.saveAttendanceBatch(request.records);

            sendJsonResponse(exchange, 201, Map.of("message", "Attendance saved successfully"));
        } else {
            sendError(exchange, 404, "Endpoint not found");
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException, SQLException {
        // DELETE /api/attendance?date=2024-01-15&subject_id=1&group_id=1
        if (path.equals("/api/attendance")) {
            Map<String, String> params = getQueryParams(exchange);

            String dateParam = params.get("date");
            String subjectIdParam = params.get("subject_id");
            String groupIdParam = params.get("group_id");

            if (dateParam != null && subjectIdParam != null && groupIdParam != null) {
                LocalDate date = LocalDate.parse(dateParam);
                int subjectId = Integer.parseInt(subjectIdParam);
                int groupId = Integer.parseInt(groupIdParam);

                repository.deleteByDateAndSubject(date, subjectId, groupId);
                sendJsonResponse(exchange, 200, Map.of("message", "Attendance records deleted"));
            } else {
                sendError(exchange, 400, "Missing parameters: date, subject_id, group_id are required");
            }
        } else {
            sendError(exchange, 404, "Endpoint not found");
        }
    }

    // Aux class for parsing a request to save attendance
    private static class AttendanceRequest {
        public LocalDate date;
        public int subjectId;
        public int groupId;
        public List<AttendanceRecord> records;
    }
}