package pet.attendance.database.repositories;

import pet.attendance.models.Subject;
import java.sql.*;

public class SubjectRepository extends BaseRepository<Subject, Integer> {

    public SubjectRepository(Connection connection) {
        super(connection, "subjects", "id");
    }

    @Override
    public Subject create(Subject subject) throws SQLException {
        String sql = "INSERT INTO subjects (name, professor_id) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, subject.getName());
            stmt.setInt(2, subject.getProfessorId());
            stmt.executeUpdate();

            Integer generatedId = getGeneratedKey(stmt);
            if (generatedId != null) {
                subject.setId(generatedId);
            }
        }
        return subject;
    }

    @Override
    public Subject update(Subject subject) throws SQLException {
        String sql = "UPDATE subjects SET name = ?, professor_id = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, subject.getName());
            stmt.setInt(2, subject.getProfessorId());
            stmt.setInt(3, subject.getId());
            stmt.executeUpdate();
        }
        return subject;
    }

    @Override
    protected Subject mapResultSetToEntity(ResultSet rs) throws SQLException {
        Subject subject = new Subject();
        subject.setId(rs.getInt("id"));
        subject.setName(rs.getString("name"));
        subject.setProfessorId(rs.getInt("professor_id"));
        return subject;
    }
}