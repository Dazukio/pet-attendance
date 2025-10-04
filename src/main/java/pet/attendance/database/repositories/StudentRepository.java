package pet.attendance.database.repositories;

import pet.attendance.models.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StudentRepository extends BaseRepository<Student, Integer> {

    public StudentRepository(Connection connection) {
        super(connection, "students", "id");
    }

    @Override
    public Student create(Student student) throws SQLException {
        String sql = "INSERT INTO students (first_name, last_name, student_id, group_id) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, student.getFirstName());
            stmt.setString(2, student.getLastName());
            stmt.setString(3, student.getStudentId());
            stmt.setInt(4, student.getGroupId());
            stmt.executeUpdate();

            Integer generatedId = getGeneratedKey(stmt);
            if (generatedId != null) {
                student.setId(generatedId);
            }
        }
        return student;
    }

    @Override
    public Student update(Student student) throws SQLException {
        String sql = "UPDATE students SET first_name = ?, last_name = ?, student_id = ?, group_id = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, student.getFirstName());
            stmt.setString(2, student.getLastName());
            stmt.setString(3, student.getStudentId());
            stmt.setInt(4, student.getGroupId());
            stmt.setInt(5, student.getId());
            stmt.executeUpdate();
        }
        return student;
    }

    @Override
    protected Student mapResultSetToEntity(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setFirstName(rs.getString("first_name"));
        student.setLastName(rs.getString("last_name"));
        student.setStudentId(rs.getString("student_id"));
        student.setGroupId(rs.getInt("group_id"));
        return student;
    }

    // Дополнительные методы для пагинации и фильтрации
    public List<Student> findByGroupId(int groupId) throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE group_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, groupId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                students.add(mapResultSetToEntity(rs));
            }
        }
        return students;
    }

    public List<Student> findAll(Map<String, String> params) throws SQLException {
        // Реализация пагинации и фильтрации
        // Можно добавить позже
        return findAll();
    }
}