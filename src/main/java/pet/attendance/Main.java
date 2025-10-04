package pet.attendance;

import pet.attendance.database.DatabaseConnection;
import pet.attendance.database.repositories.*;
import pet.attendance.server.Server;
import pet.attendance.server.handlers.*;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws IOException {
        Server server = new Server();

        try {
            GroupRepository groupRepo = new GroupRepository(DatabaseConnection.getConnection());
            ProfessorRepository professorRepo = new ProfessorRepository(DatabaseConnection.getConnection());
            SubjectRepository subjectRepo = new SubjectRepository(DatabaseConnection.getConnection());
            StudentRepository studentRepo = new StudentRepository(DatabaseConnection.getConnection());
            AttendanceRepository attendanceRepo = new AttendanceRepository(DatabaseConnection.getConnection());

            server.addHandler("/api/status", new StatusHandler());
            server.addHandler("/api/groups", new GroupHandler(groupRepo));
            server.addHandler("/api/professors", new ProfessorHandler(professorRepo));
            server.addHandler("/api/subjects", new SubjectHandler(subjectRepo));
            server.addHandler("/api/students", new StudentHandler(studentRepo));
            server.addHandler("/api/attendance", new AttendanceHandler(attendanceRepo));

            server.addHandler("/api/swagger.json", new SwaggerHandler());
            server.addHandler("/docs", new StaticHandler());

            // Static files (if needed)
            server.addHandler("/static/", new StaticHandler());

            server.start();

            System.out.println("   API Documentation: http://localhost:8080/docs/swagger.html");
            System.out.println("   Available endpoints:");
            System.out.println("   GET  /api/groups");
            System.out.println("   GET  /api/professors");
            System.out.println("   GET  /api/subjects");
            System.out.println("   GET  /api/students");
            System.out.println("   GET  /api/attendance");
            System.out.println("   POST /api/attendance");
            System.out.println("   GET  /api/swagger.json");

        } catch (SQLException e) {
            System.err.println("Failed to initialize database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}