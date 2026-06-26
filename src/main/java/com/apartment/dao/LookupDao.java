package com.apartment.dao;

import com.apartment.config.DatabaseConfig;
import com.apartment.model.Flat;
import com.apartment.model.User;
import com.apartment.model.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LookupDao {
    public List<User> findStaffUsers() throws SQLException {
        String sql = "SELECT id, name, email, role, flat_id FROM users WHERE role = 'STAFF' ORDER BY name";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<User> users = new ArrayList<>();
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setEmail(resultSet.getString("email"));
                user.setRole(Role.valueOf(resultSet.getString("role")));
                users.add(user);
            }
            return users;
        }
    }

    public List<Flat> findFlats() throws SQLException {
        String sql = "SELECT id, flat_number, tower, floor_number FROM flats ORDER BY tower, floor_number, flat_number";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<Flat> flats = new ArrayList<>();
            while (resultSet.next()) {
                Flat flat = new Flat();
                flat.setId(resultSet.getInt("id"));
                flat.setFlatNumber(resultSet.getString("flat_number"));
                flat.setTower(resultSet.getString("tower"));
                flat.setFloorNumber(resultSet.getInt("floor_number"));
                flats.add(flat);
            }
            return flats;
        }
    }
}
