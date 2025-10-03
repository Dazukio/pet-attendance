package pet.attendance.database.repositories;

import pet.attendance.models.Professor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfessorRepository {
    private Connection connection;

    public ProfessorRepository(Connection connection) {
        this.connection = connection;
    }

    public void create(Professor professor) throws SQLException {
        String sql = "INSERT INTO professors (first_name, last_name, email) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, professor.getFirstName());
            stmt.setString(2, professor.getLastName());
            stmt.setString(3, professor.getEmail());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                professor.setId(rs.getInt(1));
            }
        }
    }

    public List<Professor> findAll() throws SQLException {
        List<Professor> professors = new ArrayList<>();
        String sql = "SELECT * FROM professors";
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                professors.add(mapProfessor(rs));
            }
        }
        return professors;
    }

    public Professor findById(int id) throws SQLException {
        String sql = "SELECT * FROM professors WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapProfessor(rs);
            }
        }
        return null;
    }

    public void update(Professor professor) throws SQLException {
        String sql = "UPDATE professors SET first_name = ?, last_name = ?, email = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, professor.getFirstName());
            stmt.setString(2, professor.getLastName());
            stmt.setString(3, professor.getEmail());
            stmt.setInt(4, professor.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM professors WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Professor mapProfessor(ResultSet rs) throws SQLException {
        Professor professor = new Professor();
        professor.setId(rs.getInt("id"));
        professor.setFirstName(rs.getString("first_name"));
        professor.setLastName(rs.getString("last_name"));
        professor.setEmail(rs.getString("email"));
        return professor;
    }
}