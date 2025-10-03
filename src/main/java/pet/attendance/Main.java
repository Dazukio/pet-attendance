package pet.attendance;

import pet.attendance.database.DatabaseConnection;
import pet.attendance.database.repositories.ProfessorRepository;
import pet.attendance.database.repositories.SubjectRepository;
import pet.attendance.models.Professor;
import pet.attendance.models.Subject;
import pet.attendance.server.Server;
import pet.attendance.server.handlers.StatusHandler;
import java.io.IOException;
import java.sql.SQLException;

import java.sql.Connection;
import java.sql.DriverManager;

public class Main {
    public static void main(String[] args) throws IOException {
        // Server server = new Server();
        // server.addHandler("/api/status", new StatusHandler());
        // server.start();

        try {
            ProfessorRepository pf = new ProfessorRepository(DatabaseConnection.getConnection());

            System.out.println("Professors");
            pf.findAll().forEach(p -> System.out
                    .println(p.getId() + ": " + p.getFirstName() + " " + p.getLastName() + " at " + p.getEmail()));
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
