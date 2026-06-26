package com.apartment.dao;

import com.apartment.config.DatabaseConfig;
import com.apartment.model.Role;
import com.apartment.model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HexFormat;

public class UserDao {
    public User authenticate(String email, String password) throws SQLException {
        String sql = "SELECT id, name, email, password_hash, role, flat_id FROM users WHERE email = ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next() && hash(password).equals(resultSet.getString("password_hash"))) {
                    User user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setName(resultSet.getString("name"));
                    user.setEmail(resultSet.getString("email"));
                    user.setRole(Role.valueOf(resultSet.getString("role")));
                    int flatId = resultSet.getInt("flat_id");
                    user.setFlatId(resultSet.wasNull() ? null : flatId);
                    return user;
                }
            }
        }
        return null;
    }

    private String hash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(password.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}
