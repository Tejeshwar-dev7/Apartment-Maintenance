package com.apartment.dao;

import com.apartment.config.DatabaseConfig;
import com.apartment.model.Complaint;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ComplaintDao {
    public void create(int residentId, String title, String category, String description, String photoUrl) throws SQLException {
        String sql = "INSERT INTO complaints (resident_id, title, category, description, photo_url, status) VALUES (?, ?, ?, ?, ?, 'OPEN')";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, residentId);
            statement.setString(2, title);
            statement.setString(3, category);
            statement.setString(4, description);
            statement.setString(5, photoUrl);
            statement.executeUpdate();
        }
    }

    public List<Complaint> findForResident(int residentId) throws SQLException {
        String sql = """
                SELECT c.*, f.flat_number, s.name AS staff_name
                FROM complaints c
                JOIN users r ON c.resident_id = r.id
                LEFT JOIN flats f ON r.flat_id = f.id
                LEFT JOIN users s ON c.assigned_staff_id = s.id
                WHERE c.resident_id = ?
                ORDER BY c.created_at DESC
                """;
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, residentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return mapComplaints(resultSet);
            }
        }
    }

    public List<Complaint> findAssignedToStaff(int staffId) throws SQLException {
        String sql = """
                SELECT c.*, f.flat_number, r.name AS resident_name, s.name AS staff_name
                FROM complaints c
                JOIN users r ON c.resident_id = r.id
                LEFT JOIN flats f ON r.flat_id = f.id
                LEFT JOIN users s ON c.assigned_staff_id = s.id
                WHERE c.assigned_staff_id = ?
                ORDER BY c.created_at DESC
                """;
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, staffId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return mapComplaints(resultSet);
            }
        }
    }

    public List<Complaint> findAll() throws SQLException {
        String sql = """
                SELECT c.*, f.flat_number, r.name AS resident_name, s.name AS staff_name
                FROM complaints c
                JOIN users r ON c.resident_id = r.id
                LEFT JOIN flats f ON r.flat_id = f.id
                LEFT JOIN users s ON c.assigned_staff_id = s.id
                ORDER BY c.created_at DESC
                """;
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return mapComplaints(resultSet);
        }
    }

    public void assign(int complaintId, int staffId) throws SQLException {
        String sql = """
                UPDATE complaints c
                JOIN users s ON s.id = ? AND s.role = 'STAFF'
                SET c.assigned_staff_id = s.id, c.status = 'ASSIGNED'
                WHERE c.id = ?
                """;
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, staffId);
            statement.setInt(2, complaintId);
            statement.executeUpdate();
        }
    }

    public void updateProgress(int complaintId, int staffId, String status, String completionPhotoUrl) throws SQLException {
        String sql = """
                UPDATE complaints
                SET status = ?, completion_photo_url = NULLIF(?, ''), completed_at = CASE WHEN ? = 'COMPLETED' THEN NOW() ELSE completed_at END
                WHERE id = ? AND assigned_staff_id = ?
                """;
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setString(2, completionPhotoUrl);
            statement.setString(3, status);
            statement.setInt(4, complaintId);
            statement.setInt(5, staffId);
            statement.executeUpdate();
        }
    }

    private List<Complaint> mapComplaints(ResultSet resultSet) throws SQLException {
        List<Complaint> complaints = new ArrayList<>();
        while (resultSet.next()) {
            Complaint complaint = new Complaint();
            complaint.setId(resultSet.getInt("id"));
            complaint.setTitle(resultSet.getString("title"));
            complaint.setCategory(resultSet.getString("category"));
            complaint.setDescription(resultSet.getString("description"));
            complaint.setStatus(resultSet.getString("status"));
            complaint.setFlatNumber(resultSet.getString("flat_number"));
            complaint.setResidentName(readOptional(resultSet, "resident_name"));
            complaint.setAssignedStaffName(readOptional(resultSet, "staff_name"));
            complaint.setPhotoUrl(resultSet.getString("photo_url"));
            complaint.setCompletionPhotoUrl(resultSet.getString("completion_photo_url"));
            Timestamp createdAt = resultSet.getTimestamp("created_at");
            if (createdAt != null) {
                complaint.setCreatedAt(createdAt.toLocalDateTime());
            }
            complaints.add(complaint);
        }
        return complaints;
    }

    private String readOptional(ResultSet resultSet, String column) {
        try {
            return resultSet.getString(column);
        } catch (SQLException exception) {
            return null;
        }
    }
}
