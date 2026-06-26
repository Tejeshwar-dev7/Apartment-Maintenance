package com.apartment.dao;

import com.apartment.config.DatabaseConfig;
import com.apartment.model.Payment;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PaymentDao {
    public List<Payment> findForResident(int residentId) throws SQLException {
        String sql = """
                SELECT p.*, f.flat_number
                FROM maintenance_payments p
                JOIN users u ON p.flat_id = u.flat_id
                JOIN flats f ON p.flat_id = f.id
                WHERE u.id = ?
                ORDER BY p.due_month DESC
                """;
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, residentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return mapPayments(resultSet);
            }
        }
    }

    public List<Payment> findAll() throws SQLException {
        String sql = """
                SELECT p.*, f.flat_number
                FROM maintenance_payments p
                JOIN flats f ON p.flat_id = f.id
                ORDER BY p.due_month DESC, f.flat_number
                """;
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return mapPayments(resultSet);
        }
    }

    public void generateMonthlyBills(String dueMonth, BigDecimal amount, String description, Integer flatId) throws SQLException {
        String sql;
        if (flatId == null) {
            sql = """
                    INSERT INTO maintenance_payments (flat_id, due_month, amount, description, status)
                    SELECT id, ?, ?, ?, 'DUE' FROM flats
                    ON DUPLICATE KEY UPDATE amount = VALUES(amount), description = VALUES(description)
                    """;
        } else {
            sql = """
                    INSERT INTO maintenance_payments (flat_id, due_month, amount, description, status)
                    VALUES (?, ?, ?, ?, 'DUE')
                    ON DUPLICATE KEY UPDATE amount = VALUES(amount), description = VALUES(description)
                    """;
        }
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (flatId == null) {
                statement.setString(1, dueMonth);
                statement.setBigDecimal(2, amount);
                statement.setString(3, description);
            } else {
                statement.setInt(1, flatId);
                statement.setString(2, dueMonth);
                statement.setBigDecimal(3, amount);
                statement.setString(4, description);
            }
            statement.executeUpdate();
        }
    }

    public void markPaid(int paymentId, int residentId) throws SQLException {
        String sql = """
                UPDATE maintenance_payments p
                JOIN users u ON p.flat_id = u.flat_id
                SET p.status = 'PAID', p.transaction_id = ?, p.paid_on = CURRENT_DATE
                WHERE p.id = ? AND u.id = ? AND p.status = 'DUE'
                """;
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            statement.setInt(2, paymentId);
            statement.setInt(3, residentId);
            statement.executeUpdate();
        }
    }

    public Payment findPaidReceipt(int paymentId, int residentId) throws SQLException {
        String sql = """
                SELECT p.*, f.flat_number
                FROM maintenance_payments p
                JOIN users u ON p.flat_id = u.flat_id
                JOIN flats f ON p.flat_id = f.id
                WHERE p.id = ? AND u.id = ? AND p.status = 'PAID'
                """;
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, paymentId);
            statement.setInt(2, residentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Payment> payments = mapPayments(resultSet);
                return payments.isEmpty() ? null : payments.get(0);
            }
        }
    }

    private List<Payment> mapPayments(ResultSet resultSet) throws SQLException {
        List<Payment> payments = new ArrayList<>();
        while (resultSet.next()) {
            Payment payment = new Payment();
            payment.setId(resultSet.getInt("id"));
            payment.setFlatNumber(resultSet.getString("flat_number"));
            payment.setMonth(resultSet.getString("due_month"));
            payment.setAmount(resultSet.getBigDecimal("amount"));
            payment.setDescription(resultSet.getString("description"));
            payment.setStatus(resultSet.getString("status"));
            payment.setTransactionId(resultSet.getString("transaction_id"));
            if (resultSet.getDate("paid_on") != null) {
                payment.setPaidOn(resultSet.getDate("paid_on").toLocalDate());
            }
            payments.add(payment);
        }
        return payments;
    }
}
