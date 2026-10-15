
package com.helpdesk.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.helpdesk.model.User;
import com.helpdesk.util.DBConnection;

public class UserDAOImpl implements UserDAO {

    @Override
    public boolean registerUser(User user) throws SQLException {
        String sql = "INSERT INTO users "
                + "(full_name, email, password_hash, role) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getRole());

            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT user_id, full_name, email, password_hash, role "
                + "FROM users WHERE email = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("password_hash"),
                            rs.getString("role"));

                    user.setUserId(rs.getInt("user_id"));
                    return user;
                }
            }
        }

        return null;
    }

    @Override
    public List<User> findUsersByRole(String role) throws SQLException {
        List<User> users = new ArrayList<>();

        String sql = "SELECT user_id, full_name, email, password_hash, role "
                + "FROM users WHERE role = ? ORDER BY user_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, role);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = new User(
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("password_hash"),
                            rs.getString("role"));

                    user.setUserId(rs.getInt("user_id"));
                    users.add(user);
                }
            }
        }

        return users;
    }
}
