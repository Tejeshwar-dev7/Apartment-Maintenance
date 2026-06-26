package com.apartment.dao;

import com.apartment.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AdminDao {
    public Map<String, Integer> dashboardStats() throws SQLException {
        Map<String, Integer> stats = new LinkedHashMap<>();
        try (Connection connection = DatabaseConfig.getConnection()) {
            stats.put("Open Complaints", count(connection, "SELECT COUNT(*) FROM complaints WHERE status <> 'COMPLETED'"));
            stats.put("Completed Complaints", count(connection, "SELECT COUNT(*) FROM complaints WHERE status = 'COMPLETED'"));
            stats.put("Pending Visitors", count(connection, "SELECT COUNT(*) FROM visitor_requests WHERE status = 'PENDING'"));
            stats.put("Due Payments", count(connection, "SELECT COUNT(*) FROM maintenance_payments WHERE status = 'DUE'"));
            stats.put("Registered Flats", count(connection, "SELECT COUNT(*) FROM flats"));
        }
        return stats;
    }

    public void addFlat(String flatNumber, String tower, int floorNumber) throws SQLException {
        String sql = "INSERT INTO flats (flat_number, tower, floor_number) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, flatNumber);
            statement.setString(2, tower);
            statement.setInt(3, floorNumber);
            statement.executeUpdate();
        }
    }

    public List<Map<String, String>> openComplaintDetails() throws SQLException {
        return rows("""
                SELECT c.id, c.title, c.category, c.status, c.description, f.flat_number, r.name AS resident, COALESCE(s.name, 'Unassigned') AS staff
                FROM complaints c
                JOIN users r ON c.resident_id = r.id
                LEFT JOIN flats f ON r.flat_id = f.id
                LEFT JOIN users s ON c.assigned_staff_id = s.id
                WHERE c.status <> 'COMPLETED'
                ORDER BY c.created_at DESC
                """);
    }

    public List<Map<String, String>> completedComplaintDetails() throws SQLException {
        return rows("""
                SELECT c.id, c.title, c.category, c.status, c.description, f.flat_number, r.name AS resident, COALESCE(s.name, 'Unassigned') AS staff
                FROM complaints c
                JOIN users r ON c.resident_id = r.id
                LEFT JOIN flats f ON r.flat_id = f.id
                LEFT JOIN users s ON c.assigned_staff_id = s.id
                WHERE c.status = 'COMPLETED'
                ORDER BY c.completed_at DESC, c.created_at DESC
                """);
    }

    public List<Map<String, String>> pendingVisitorDetails() throws SQLException {
        return rows("""
                SELECT v.id, v.visitor_name, v.phone, v.purpose, v.status, f.flat_number, u.name AS resident
                FROM visitor_requests v
                JOIN users u ON v.resident_id = u.id
                LEFT JOIN flats f ON u.flat_id = f.id
                WHERE v.status = 'PENDING'
                ORDER BY v.visit_time DESC
                """);
    }

    public List<Map<String, String>> duePaymentDetails() throws SQLException {
        return rows("""
                SELECT p.id, f.flat_number, p.due_month, p.amount, p.description, p.status
                FROM maintenance_payments p
                JOIN flats f ON p.flat_id = f.id
                WHERE p.status = 'DUE'
                ORDER BY p.due_month DESC, f.flat_number
                """);
    }

    public List<Map<String, String>> flatDetails() throws SQLException {
        return rows("""
                SELECT f.id, f.flat_number, f.tower, f.floor_number, COALESCE(u.name, 'Vacant') AS resident
                FROM flats f
                LEFT JOIN users u ON u.flat_id = f.id AND u.role = 'RESIDENT'
                ORDER BY f.tower, f.floor_number, f.flat_number
                """);
    }

    public List<Map<String, String>> workingStaffDetails() throws SQLException {
        return rows("""
                SELECT c.id, c.title, c.status, f.flat_number, s.id AS staff_id, s.name AS staff
                FROM complaints c
                JOIN users s ON c.assigned_staff_id = s.id
                JOIN users r ON c.resident_id = r.id
                LEFT JOIN flats f ON r.flat_id = f.id
                WHERE c.status IN ('ASSIGNED', 'IN_PROGRESS')
                ORDER BY c.created_at DESC
                """);
    }

    private List<Map<String, String>> rows(String sql) throws SQLException {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<Map<String, String>> rows = new ArrayList<>();
            int columnCount = resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {
                Map<String, String> row = new LinkedHashMap<>();
                for (int index = 1; index <= columnCount; index++) {
                    String column = resultSet.getMetaData().getColumnLabel(index);
                    row.put(column, resultSet.getString(index));
                }
                rows.add(row);
            }
            return rows;
        }
    }

    private int count(Connection connection, String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return resultSet.getInt(1);
        }
    }
}
