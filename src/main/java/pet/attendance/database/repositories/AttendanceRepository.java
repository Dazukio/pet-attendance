package pet.attendance.database.repositories;

import pet.attendance.models.AttendanceRecord;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AttendanceRepository extends BaseRepository<AttendanceRecord, Integer> {

    public AttendanceRepository(Connection connection) {
        super(connection, "attendance", "id");
    }

    @Override
    public AttendanceRecord create(AttendanceRecord record) throws SQLException {
        String sql = "INSERT INTO attendance (student_id, subject_id, date, status) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, record.getStudentId());
            stmt.setInt(2, record.getSubjectId());
            stmt.setDate(3, Date.valueOf(record.getDate()));
            stmt.setString(4, record.getStatus());
            stmt.executeUpdate();

            Integer generatedId = getGeneratedKey(stmt);
            if (generatedId != null) {
                record.setId(generatedId);
            }
        }
        return record;
    }

    @Override
    public AttendanceRecord update(AttendanceRecord record) throws SQLException {
        String sql = "UPDATE attendance SET status = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, record.getStatus());
            stmt.setInt(2, record.getId());
            stmt.executeUpdate();
        }
        return record;
    }

    @Override
    protected AttendanceRecord mapResultSetToEntity(ResultSet rs) throws SQLException {
        AttendanceRecord record = new AttendanceRecord();
        record.setId(rs.getInt("id"));
        record.setStudentId(rs.getInt("student_id"));
        record.setSubjectId(rs.getInt("subject_id"));
        record.setDate(rs.getDate("date").toLocalDate());
        record.setStatus(rs.getString("status"));
        return record;
    }

    public void saveAttendanceBatch(List<AttendanceRecord> records) throws SQLException {
        String sql = "INSERT INTO attendance (student_id, subject_id, date, status) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (AttendanceRecord record : records) {
                stmt.setInt(1, record.getStudentId());
                stmt.setInt(2, record.getSubjectId());
                stmt.setDate(3, Date.valueOf(record.getDate()));
                stmt.setString(4, record.getStatus());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    public List<AttendanceRecord> findByDateAndSubject(LocalDate date, int subjectId, int groupId) throws SQLException {
        List<AttendanceRecord> records = new ArrayList<>();
        String sql = """
                SELECT a.* FROM attendance a
                JOIN students s ON a.student_id = s.id
                WHERE a.date = ? AND a.subject_id = ? AND s.group_id = ?
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(date));
            stmt.setInt(2, subjectId);
            stmt.setInt(3, groupId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                records.add(mapResultSetToEntity(rs));
            }
        }
        return records;
    }

    public void deleteByDateAndSubject(LocalDate date, int subjectId, int groupId) throws SQLException {
        String sql = """
                DELETE FROM attendance
                WHERE date = ? AND subject_id = ?
                AND student_id IN (SELECT id FROM students WHERE group_id = ?)
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(date));
            stmt.setInt(2, subjectId);
            stmt.setInt(3, groupId);
            stmt.executeUpdate();
        }
    }
}