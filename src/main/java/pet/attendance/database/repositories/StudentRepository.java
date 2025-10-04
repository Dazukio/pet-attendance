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

    // Methods for pagination and filtration
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
        List<Student> students = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT * FROM students WHERE 1=1");

        // group filter
        if (params.containsKey("group_id")) {
            sql.append(" AND group_id = ?");
            parameters.add(Integer.parseInt(params.get("group_id")));
        }

        // Pagination
        int limit = 50; // default limit
        int offset = 0; // default offset

        if (params.containsKey("size")) {
            limit = Integer.parseInt(params.get("size"));
            // MAX 100
            if (limit > 100)
                limit = 100;
            if (limit < 1)
                limit = 1;
        }

        if (params.containsKey("page")) {
            int page = Integer.parseInt(params.get("page"));
            if (page < 0)
                page = 0;
            offset = page * limit;
        }

        sql.append(" LIMIT ? OFFSET ?");
        parameters.add(limit);
        parameters.add(offset);

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                students.add(mapResultSetToEntity(rs));
            }
        }
        return students;
    }
}