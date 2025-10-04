package pet.attendance.database.repositories;

import pet.attendance.models.Group;
import java.sql.*;

public class GroupRepository extends BaseRepository<Group, Integer> {

    public GroupRepository(Connection connection) {
        super(connection, "groups", "id");
    }

    @Override
    public Group create(Group group) throws SQLException {
        String sql = "INSERT INTO groups (name) VALUES (?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, group.getName());
            stmt.executeUpdate();

            Integer generatedId = getGeneratedKey(stmt);
            if (generatedId != null) {
                group.setId(generatedId);
            }
        }
        return group;
    }

    @Override
    public Group update(Group group) throws SQLException {
        String sql = "UPDATE groups SET name = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, group.getName());
            stmt.setInt(2, group.getId());
            stmt.executeUpdate();
        }
        return group;
    }

    @Override
    protected Group mapResultSetToEntity(ResultSet rs) throws SQLException {
        Group group = new Group();
        group.setId(rs.getInt("id"));
        group.setName(rs.getString("name"));
        return group;
    }
}