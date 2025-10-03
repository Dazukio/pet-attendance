package pet.attendance.database.repositories;

import pet.attendance.models.AttendanceRecord;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AttendanceRepository {
    private Connection connection;

    public AttendanceRepository(Connection connection) {
        this.connection = connection;
    }

    public void create(AttendanceRecord record) throws SQLException {
        String sql = "INSERT INTO attendance (student_id, subject_id, date, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, record.getStudentId());
            stmt.setInt(2, record.getSubjectId());
            stmt.setDate(3, Date.valueOf(record.getDate()));
            stmt.setString(4, record.getStatus());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                record.setId(rs.getInt(1));
            }
        }
    }

    //
    public void saveAll(List<AttendanceRecord> records) throws SQLException {
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

    public List<AttendanceRecord> findByDateAndSubject(LocalDate date, int subjectId) throws SQLException {
        List<AttendanceRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM attendance WHERE date = ? AND subject_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(date));
            stmt.setInt(2, subjectId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                records.add(mapAttendance(rs));
            }
        }
        return records;
    }

    public List<AttendanceRecord> findByStudentAndDateRange(int studentId, LocalDate start, LocalDate end)
            throws SQLException {
        List<AttendanceRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM attendance WHERE student_id = ? AND date BETWEEN ? AND ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setDate(2, Date.valueOf(start));
            stmt.setDate(3, Date.valueOf(end));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                records.add(mapAttendance(rs));
            }
        }
        return records;
    }

    public void update(AttendanceRecord record) throws SQLException {
        String sql = "UPDATE attendance SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, record.getStatus());
            stmt.setInt(2, record.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteByDateAndSubject(LocalDate date, int subjectId) throws SQLException {
        String sql = "DELETE FROM attendance WHERE date = ? AND subject_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(date));
            stmt.setInt(2, subjectId);
            stmt.executeUpdate();
        }
    }

    private AttendanceRecord mapAttendance(ResultSet rs) throws SQLException {
        AttendanceRecord record = new AttendanceRecord();
        record.setId(rs.getInt("id"));
        record.setStudentId(rs.getInt("student_id"));
        record.setSubjectId(rs.getInt("subject_id"));
        record.setDate(rs.getDate("date").toLocalDate());
        record.setStatus(rs.getString("status"));
        return record;
    }
}