package com.faceScan.dao;

import com.faceScan.model.Group;
import com.faceScan.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupDAO {

    public static List<Group> getGroupsByUserId(int userId) {
        List<Group> groups = new ArrayList<>();
        String sql = "SELECT id, name, user_id FROM groups WHERE user_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                groups.add(new Group(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("user_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }

    public void addGroup(Group group) {
        String sql = "INSERT INTO groups (name, user_id) VALUES (?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, group.getName());
            pstmt.setInt(2, group.getUserId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteGroup(int groupId) {
        String sql = "DELETE FROM groups WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, groupId);
            pstmt.executeUpdate();

            // możesz dodać też usuwanie studentów powiązanych z tą grupą

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void save(Group group) {
        String sql = "INSERT INTO groups(name, user_id) VALUES(?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, group.getName());
            pstmt.setInt(2, group.getUserId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void delete(int groupId) {
        String sql = "DELETE FROM groups WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
