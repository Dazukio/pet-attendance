package pet.attendance.database.repositories;

import pet.attendance.models.Group;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupRepository {
    private Connection connection;

    public GroupRepository(Connection connection) {
        this.connection = connection;
    }

    // CREATE
    public void create(Group group) throws SQLException {
        String sql = "INSERT INTO groups (name) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, group.getName());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                group.setId(rs.getInt(1));
            }
        }
    }

    // READ ALL
    public List<Group> findAll() throws SQLException {
        List<Group> groups = new ArrayList<>();
        String sql = "SELECT * FROM groups";
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                groups.add(mapGroup(rs));
            }
        }
        return groups;
    }

    // READ BY ID
    public Group findById(int id) throws SQLException {
        String sql = "SELECT * FROM groups WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapGroup(rs);
            }
        }
        return null;
    }

    // UPDATE
    public void update(Group group) throws SQLException {
        String sql = "UPDATE groups SET name = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, group.getName());
            stmt.setInt(2, group.getId());
            stmt.executeUpdate();
        }
    }

    // DELETE
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM groups WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Group mapGroup(ResultSet rs) throws SQLException {
        Group group = new Group();
        group.setId(rs.getInt("id"));
        group.setName(rs.getString("name"));
        return group;
    }
}