package com.faceScan.dao;

import com.faceScan.model.User;
import com.faceScan.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements IUserDAO  {

    public boolean registerUser(User user) {
        System.out.println("[REGISTER] " +
                "username=" + user.getUsername() +
                ", role=" + user.getRole() +
                ", name=" + user.getFirstName() + " " + user.getLastName() +
                ", photoPath=" + user.getPhotoPath());

        String sql = "INSERT INTO users (username, password, role, first_name, last_name, photo_path) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getFirstName());
            pstmt.setString(5, user.getLastName());
            pstmt.setString(6, user.getPhotoPath());

            pstmt.executeUpdate();

            System.out.println("[REGISTER] Success");
            return true;

        } catch (SQLException e) {
            System.err.println("[REGISTER] Registration Error: " + e.getMessage());
            return false;
        }
    }

    public User loginUser(String username, String password) {
        System.out.println("[LOGIN] Username: " + username);

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("[LOGIN] Logged in as: " + rs.getString("role"));


                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("photo_path")
                );
            } else {
                System.out.println("[LOGIN] Wrong username or password");
            }
        } catch (SQLException e) {
            System.err.println("[LOGIN] Login Error: " + e.getMessage());
        }
        return null;
    }

    public List<User> getAllStudents() {
        List<User> students = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'student'";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                students.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("photo_path")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("photo_path")
                );
            }
        } catch (SQLException e) {
            System.err.println("[USER] getUserById error: " + e.getMessage());
        }
        return null;
    }

    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("photo_path")
                );
            }
        } catch (SQLException e) {
            System.err.println("[USERDAO] getUserByUsername error: " + e.getMessage());
        }
        return null;
    }
}
