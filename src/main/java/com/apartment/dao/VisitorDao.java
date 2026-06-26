package com.apartment.dao;

import com.apartment.config.DatabaseConfig;
import com.apartment.model.VisitorRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VisitorDao {
    public void create(int residentId, String visitorName, String phone, String purpose, LocalDateTime visitTime) throws SQLException {
        String sql = "INSERT INTO visitor_requests (resident_id, visitor_name, phone, purpose, visit_time, status) VALUES (?, ?, ?, ?, ?, 'PENDING')";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, residentId);
            statement.setString(2, visitorName);
            statement.setString(3, phone);
            statement.setString(4, purpose);
            statement.setTimestamp(5, Timestamp.valueOf(visitTime));
            statement.executeUpdate();
        }
    }

    public List<VisitorRequest> findForResident(int residentId) throws SQLException {
        String sql = """
                SELECT v.*, f.flat_number
                FROM visitor_requests v
                JOIN users u ON v.resident_id = u.id
                LEFT JOIN flats f ON u.flat_id = f.id
                WHERE v.resident_id = ?
                ORDER BY v.visit_time DESC
                """;
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, residentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return mapRequests(resultSet);
            }
        }
    }

    public List<VisitorRequest> findAll() throws SQLException {
        String sql = """
                SELECT v.*, f.flat_number
                FROM visitor_requests v
                JOIN users u ON v.resident_id = u.id
                LEFT JOIN flats f ON u.flat_id = f.id
                ORDER BY v.visit_time DESC
                """;
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return mapRequests(resultSet);
        }
    }

    public void decide(int requestId, String status, String decisionNote) throws SQLException {
        String sql = "UPDATE visitor_requests SET status = ?, decision_note = ? WHERE id = ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setString(2, decisionNote);
            statement.setInt(3, requestId);
            statement.executeUpdate();
        }
    }

    private List<VisitorRequest> mapRequests(ResultSet resultSet) throws SQLException {
        List<VisitorRequest> requests = new ArrayList<>();
        while (resultSet.next()) {
            VisitorRequest request = new VisitorRequest();
            request.setId(resultSet.getInt("id"));
            request.setVisitorName(resultSet.getString("visitor_name"));
            request.setPhone(resultSet.getString("phone"));
            request.setPurpose(resultSet.getString("purpose"));
            request.setStatus(resultSet.getString("status"));
            request.setDecisionNote(resultSet.getString("decision_note"));
            request.setFlatNumber(resultSet.getString("flat_number"));
            Timestamp visitTime = resultSet.getTimestamp("visit_time");
            if (visitTime != null) {
                request.setVisitTime(visitTime.toLocalDateTime());
            }
            requests.add(request);
        }
        return requests;
    }
}
