package com.apartment.dao;

import com.apartment.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public Map<String, String> paymentAnalytics() throws SQLException {
        String sql = """
                SELECT
                    COUNT(*) AS invoice_count,
                    COALESCE(SUM(amount), 0) AS total_amount,
                    COALESCE(SUM(CASE WHEN status = 'PAID' THEN amount ELSE 0 END), 0) AS paid_amount,
                    COALESCE(SUM(CASE WHEN status = 'DUE' THEN amount ELSE 0 END), 0) AS due_amount,
                    SUM(CASE WHEN status = 'PAID' THEN 1 ELSE 0 END) AS paid_count,
                    SUM(CASE WHEN status = 'DUE' THEN 1 ELSE 0 END) AS due_count
                FROM maintenance_payments
                """;
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            BigDecimal totalAmount = resultSet.getBigDecimal("total_amount");
            BigDecimal paidAmount = resultSet.getBigDecimal("paid_amount");
            BigDecimal dueAmount = resultSet.getBigDecimal("due_amount");
            int paidPercent = percent(paidAmount, totalAmount);
            int duePercent = 100 - paidPercent;

            Map<String, String> analytics = new LinkedHashMap<>();
            analytics.put("invoiceCount", String.valueOf(resultSet.getInt("invoice_count")));
            analytics.put("paidCount", String.valueOf(resultSet.getInt("paid_count")));
            analytics.put("dueCount", String.valueOf(resultSet.getInt("due_count")));
            analytics.put("totalAmount", money(totalAmount));
            analytics.put("paidAmount", money(paidAmount));
            analytics.put("dueAmount", money(dueAmount));
            analytics.put("paidPercent", String.valueOf(paidPercent));
            analytics.put("duePercent", String.valueOf(duePercent));
            analytics.put("ringStyle", "conic-gradient(var(--success) 0 " + paidPercent + "%, var(--danger) " + paidPercent + "% 100%)");
            return analytics;
        }
    }

    public Map<String, String> occupancyAnalytics() throws SQLException {
        String sql = """
                SELECT
                    COUNT(*) AS total_flats,
                    SUM(CASE WHEN u.id IS NULL THEN 1 ELSE 0 END) AS vacant_flats,
                    SUM(CASE WHEN u.id IS NOT NULL THEN 1 ELSE 0 END) AS occupied_flats
                FROM flats f
                LEFT JOIN users u ON u.flat_id = f.id AND u.role = 'RESIDENT'
                """;
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            int total = resultSet.getInt("total_flats");
            int occupied = resultSet.getInt("occupied_flats");
            int vacant = resultSet.getInt("vacant_flats");
            int occupiedPercent = total == 0 ? 0 : Math.round((occupied * 100f) / total);

            Map<String, String> analytics = new LinkedHashMap<>();
            analytics.put("total", String.valueOf(total));
            analytics.put("occupied", String.valueOf(occupied));
            analytics.put("vacant", String.valueOf(vacant));
            analytics.put("occupiedPercent", String.valueOf(occupiedPercent));
            analytics.put("ringStyle", "conic-gradient(var(--success) 0 " + occupiedPercent + "%, var(--accent) " + occupiedPercent + "% 100%)");
            return analytics;
        }
    }

    public Map<String, String> complaintStatusAnalytics() throws SQLException {
        String sql = """
                SELECT
                    COUNT(*) AS total_complaints,
                    SUM(CASE WHEN status = 'OPEN' THEN 1 ELSE 0 END) AS open_count,
                    SUM(CASE WHEN status IN ('ASSIGNED', 'IN_PROGRESS') THEN 1 ELSE 0 END) AS working_count,
                    SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed_count
                FROM complaints
                """;
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            int total = resultSet.getInt("total_complaints");
            int open = resultSet.getInt("open_count");
            int working = resultSet.getInt("working_count");
            int completed = resultSet.getInt("completed_count");
            int openPercent = total == 0 ? 0 : Math.round((open * 100f) / total);
            int workingPercent = total == 0 ? 0 : Math.round((working * 100f) / total);
            int completedPercent = Math.max(0, 100 - openPercent - workingPercent);

            Map<String, String> analytics = new LinkedHashMap<>();
            analytics.put("total", String.valueOf(total));
            analytics.put("open", String.valueOf(open));
            analytics.put("working", String.valueOf(working));
            analytics.put("completed", String.valueOf(completed));
            analytics.put("ringStyle", "conic-gradient(var(--warning) 0 " + openPercent + "%, var(--blue) " + openPercent + "% " + (openPercent + workingPercent) + "%, var(--success) " + (openPercent + workingPercent) + "% 100%)");
            analytics.put("openPercent", String.valueOf(openPercent));
            analytics.put("workingPercent", String.valueOf(workingPercent));
            analytics.put("completedPercent", String.valueOf(completedPercent));
            return analytics;
        }
    }

    public List<Map<String, String>> complaintCategoryAnalytics() throws SQLException {
        Map<String, Integer> counts = new LinkedHashMap<>();
        counts.put("Water", 0);
        counts.put("Lift", 0);
        counts.put("Repair", 0);
        counts.put("Cleaning", 0);
        counts.put("Electrical", 0);
        counts.put("Other", 0);

        String sql = """
                SELECT category, COUNT(*) AS total
                FROM complaints
                GROUP BY category
                """;
        int max = 0;
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String category = normalizeCategory(resultSet.getString("category"));
                int total = resultSet.getInt("total");
                counts.put(category, counts.get(category) + total);
            }
        }
        for (int count : counts.values()) {
            max = Math.max(max, count);
        }

        List<Map<String, String>> rows = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            int height = max == 0 ? 6 : Math.max(8, Math.round((entry.getValue() * 100f) / max));
            Map<String, String> row = new LinkedHashMap<>();
            row.put("category", entry.getKey());
            row.put("count", String.valueOf(entry.getValue()));
            row.put("height", String.valueOf(height));
            rows.add(row);
        }
        return rows;
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

    private String normalizeCategory(String category) {
        if (category == null) {
            return "Other";
        }
        String normalized = category.toLowerCase();
        if (normalized.contains("water") || normalized.contains("plumb")) {
            return "Water";
        }
        if (normalized.contains("lift")) {
            return "Lift";
        }
        if (normalized.contains("repair")) {
            return "Repair";
        }
        if (normalized.contains("clean")) {
            return "Cleaning";
        }
        if (normalized.contains("electric")) {
            return "Electrical";
        }
        return "Other";
    }

    private int percent(BigDecimal value, BigDecimal total) {
        if (total == null || BigDecimal.ZERO.compareTo(total) == 0) {
            return 0;
        }
        return value.multiply(BigDecimal.valueOf(100)).divide(total, 0, RoundingMode.HALF_UP).intValue();
    }

    private String money(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
