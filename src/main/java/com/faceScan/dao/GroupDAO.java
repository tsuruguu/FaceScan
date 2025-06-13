package com.faceScan.dao;

import com.faceScan.model.Group;
import com.faceScan.model.User;
import com.faceScan.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupDAO {

    public boolean addGroup(Group group){
        String sql = "INSERT INTO groups (name,professor_id) VALUES (?,?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, group.getName());
            pstmt.setInt(2,group.getProfessorId());
            pstmt.executeUpdate();
            return true;
        }catch (SQLException e){
            System.err.println( "[GROUP] Error adding group: " + e.getMessage());
            return false;
        }

    }

    public List<User> getStudentsInGroup(int groupId) {
        List<User> students = new ArrayList<>();
        String sql = "SELECT u.* FROM users u " +
                "JOIN group_members gm ON u.id = gm.student_id " +
                "WHERE gm.group_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, groupId);
            ResultSet rs = pstmt.executeQuery();

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

//    public boolean addStudentToGroup(int groupId, int studentId) {
//        String sql = "INSERT OR IGNORE INTO group_members (group_id, student_id) VALUES (?, ?)";
//        try (Connection conn = DatabaseManager.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setInt(1, groupId);
//            pstmt.setInt(2, studentId);
//            pstmt.executeUpdate();
//            return true;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

    public static List<Group> getGroupsByUserId(int professorId) {
        List<Group> groups = new ArrayList<>();
        String sql = "SELECT id, name, professor_id FROM groups WHERE professor_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, professorId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                groups.add(new Group(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("professor_id")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[GROUP] Error fetching groups: " + e.getMessage());
        }
        return groups;
    }

    public boolean deleteGroup(int groupId) {
        String sql = "DELETE FROM groups WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, groupId);
            pstmt.executeUpdate();

            return true;

            // możesz dodać też usuwanie studentów powiązanych z tą grupą

        } catch (SQLException e) {
            System.err.println("[GROUP] Error deleting group: " + e.getMessage());
            return false;
        }
    }

}
