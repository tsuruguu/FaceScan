package com.faceScan.dao;

import com.faceScan.model.Student;
import com.faceScan.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    private final Connection conn;

    public StudentDAO() {
        try {
            conn = DatabaseManager.getInstance().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Database connection error", e);
        }
    }


    public List<Student> getStudentsByGroupId(int groupId) throws SQLException {
        String sql = "SELECT id, first_name, last_name, photo_path FROM students WHERE group_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            ResultSet rs = ps.executeQuery();
            List<Student> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new Student(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("photo_path")
                ));
            }
            return list;
        }
    }

    public void addStudent(int groupId, String firstName, String lastName, String photoPath) throws SQLException {
        String sql = "INSERT INTO students(group_id, first_name, last_name, photo_path) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            ps.setString(4, photoPath);
            ps.executeUpdate();
        }
    }

    public void deleteStudent(int studentId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM students WHERE id = ?")) {
            ps.setInt(1, studentId);
            ps.executeUpdate();
        }
    }
}
