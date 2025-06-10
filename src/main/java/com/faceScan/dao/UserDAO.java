package com.faceScan.dao;

import com.faceScan.model.User;
import java.sql.*;

public class UserDAO {

    public boolean registerUser(User user) {
        System.out.println("[REGISTER] Username: " + user.getUsername() + ", Password: " + user.getPassword());

        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.executeUpdate();

            System.out.println("[REGISTER] Success");
            return true;

        } catch (SQLException e) {
            System.err.println("[REGISTER] Błąd rejestracji: " + e.getMessage());
            return false;
        }
    }

    public User loginUser(String username, String password) {
        System.out.println("[LOGIN] Username: " + username + ", Password: " + password);

        String sql = "SELECT id, username FROM users WHERE username = ? AND password = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("[LOGIN] Success");
                return new User(rs.getInt("id"), rs.getString("username"));
            } else {
                System.out.println("[LOGIN] Niepoprawny login lub hasło");
            }
        } catch (SQLException e) {
            System.err.println("[LOGIN] Błąd logowania: " + e.getMessage());
        }
        return null;
    }
}
