package pet.attendance.database.repositories;

import pet.attendance.models.Subject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectRepository {
    private Connection connection;

    public SubjectRepository(Connection connection) {
        this.connection = connection;
    }

    public void create(Subject subject) throws SQLException {
        String sql = "INSERT INTO subjects (name, professor_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, subject.getName());
            stmt.setInt(2, subject.getProfessorId());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                subject.setId(rs.getInt(1));
            }
        }
    }

    public List<Subject> findAll() throws SQLException {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT * FROM subjects";
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                subjects.add(mapSubject(rs));
            }
        }
        return subjects;
    }

    public Subject findById(int id) throws SQLException {
        String sql = "SELECT * FROM subjects WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapSubject(rs);
            }
        }
        return null;
    }

    public void update(Subject subject) throws SQLException {
        String sql = "UPDATE subjects SET name = ?, professor_id = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, subject.getName());
            stmt.setInt(2, subject.getProfessorId());
            stmt.setInt(3, subject.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM subjects WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Subject mapSubject(ResultSet rs) throws SQLException {
        Subject subject = new Subject();
        subject.setId(rs.getInt("id"));
        subject.setName(rs.getString("name"));
        subject.setProfessorId(rs.getInt("professor_id"));
        return subject;
    }
}