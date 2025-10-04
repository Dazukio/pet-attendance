package pet.attendance.database.repositories;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class BaseRepository<T, ID> {
    protected final Connection connection;
    protected final String tableName;
    protected final String idColumn;

    public BaseRepository(Connection connection, String tableName, String idColumn) {
        this.connection = connection;
        this.tableName = tableName;
        this.idColumn = idColumn;
    }

    public List<T> findAll() throws SQLException {
        List<T> entities = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName;

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                entities.add(mapResultSetToEntity(rs));
            }
        }
        return entities;
    }

    public Optional<T> findById(ID id) throws SQLException {
        String sql = "SELECT * FROM " + tableName + " WHERE " + idColumn + " = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToEntity(rs));
            }
        }
        return Optional.empty();
    }

    public boolean delete(ID id) throws SQLException {
        String sql = "DELETE FROM " + tableName + " WHERE " + idColumn + " = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // get generated id (id SERIAL PRIMARY KEY)
    protected Integer getGeneratedKey(PreparedStatement stmt) throws SQLException {
        try (ResultSet rs = stmt.getGeneratedKeys()) {
            return rs.next() ? rs.getInt(1) : null;
        }
    }

    protected abstract T mapResultSetToEntity(ResultSet rs) throws SQLException;

    public abstract T create(T entity) throws SQLException;

    public abstract T update(T entity) throws SQLException;
}