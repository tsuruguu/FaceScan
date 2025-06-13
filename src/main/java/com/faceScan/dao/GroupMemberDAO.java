package com.faceScan.dao;

import com.faceScan.model.Student;
import com.faceScan.model.User;
import com.faceScan.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupMemberDAO {

    public boolean addStudentToGroup(int studentId, int groupId) {
        String sql = "INSERT INTO group_members (group_id, student_id) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, groupId);
            pstmt.setInt(2, studentId);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("[GROUP_MEMBER] Add failed: " + e.getMessage());
            return false;
        }
    }

    public List<com.faceScan.model.Group> getGroupsForStudent(int studentId) {
        List<com.faceScan.model.Group> groups = new ArrayList<>();
        String sql = """
        SELECT g.id, g.name, g.professor_id
        FROM groups g
        JOIN group_members gm ON g.id = gm.group_id
        WHERE gm.student_id = ?
    """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                groups.add(new com.faceScan.model.Group(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("professor_id")
                ));
            }

        } catch (SQLException e) {
            System.err.println("[GROUP_MEMBER] Error fetching groups for student: " + e.getMessage());
        }

        return groups;
    }


    public List<Student> getStudentsInGroup(int groupId) {
        List<Student> students = new ArrayList<>();
        String sql = """
            SELECT u.id, u.first_name, u.last_name, u.photo_path
            FROM users u
            JOIN group_members gm ON u.id = gm.student_id
            WHERE gm.group_id = ? AND u.role = 'student'
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, groupId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                students.add(new Student(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("photo_path")
                ));
            }

        } catch (SQLException e) {
            System.err.println("[GROUP_MEMBER] Fetch failed: " + e.getMessage());
        }

        return students;
    }

    public boolean removeStudentFromGroup(int studentId, int groupId) {
        String sql = "DELETE FROM group_members WHERE student_id = ? AND group_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, groupId);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("[GROUP_MEMBER] Remove failed: " + e.getMessage());
            return false;
        }
    }
}
