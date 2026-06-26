package com.apartment.dao;

import com.apartment.config.DatabaseConfig;
import com.apartment.model.Announcement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementDao {
    public void create(String title, String message, int createdBy) throws SQLException {
        String sql = "INSERT INTO announcements (title, message, created_by) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, title);
            statement.setString(2, message);
            statement.setInt(3, createdBy);
            statement.executeUpdate();
        }
    }

    public List<Announcement> findLatest() throws SQLException {
        String sql = """
                SELECT a.*, u.name AS created_by_name
                FROM announcements a
                JOIN users u ON a.created_by = u.id
                ORDER BY a.created_at DESC
                LIMIT 5
                """;
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<Announcement> announcements = new ArrayList<>();
            while (resultSet.next()) {
                Announcement announcement = new Announcement();
                announcement.setId(resultSet.getInt("id"));
                announcement.setTitle(resultSet.getString("title"));
                announcement.setMessage(resultSet.getString("message"));
                announcement.setCreatedBy(resultSet.getString("created_by_name"));
                Timestamp createdAt = resultSet.getTimestamp("created_at");
                if (createdAt != null) {
                    announcement.setCreatedAt(createdAt.toLocalDateTime());
                }
                announcements.add(announcement);
            }
            return announcements;
        }
    }
}
