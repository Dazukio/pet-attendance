package pet.attendance.database.repositories;

import pet.attendance.models.Professor;
import java.sql.*;

public class ProfessorRepository extends BaseRepository<Professor, Integer> {

    public ProfessorRepository(Connection connection) {
        super(connection, "professors", "id");
    }

    @Override
    public Professor create(Professor professor) throws SQLException {
        String sql = "INSERT INTO professors (first_name, last_name, email) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, professor.getFirstName());
            stmt.setString(2, professor.getLastName());
            stmt.setString(3, professor.getEmail());
            stmt.executeUpdate();

            Integer generatedId = getGeneratedKey(stmt);
            if (generatedId != null) {
                professor.setId(generatedId);
            }
        }
        return professor;
    }

    @Override
    public Professor update(Professor professor) throws SQLException {
        String sql = "UPDATE professors SET first_name = ?, last_name = ?, email = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, professor.getFirstName());
            stmt.setString(2, professor.getLastName());
            stmt.setString(3, professor.getEmail());
            stmt.setInt(4, professor.getId());
            stmt.executeUpdate();
        }
        return professor;
    }

    @Override
    protected Professor mapResultSetToEntity(ResultSet rs) throws SQLException {
        Professor professor = new Professor();
        professor.setId(rs.getInt("id"));
        professor.setFirstName(rs.getString("first_name"));
        professor.setLastName(rs.getString("last_name"));
        professor.setEmail(rs.getString("email"));
        return professor;
    }
}