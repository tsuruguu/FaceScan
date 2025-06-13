package com.faceScan.dao;

import com.faceScan.model.Attendance;
import com.faceScan.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {


    public boolean addAttendance(Attendance attendance) {
        String sql = "INSERT INTO attendance (student_id,group_id,date,present) VALUES (?,?,?,?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, attendance.getStudentId());
            pstmt.setInt(2,attendance.getGroupId());
            pstmt.setString(3, attendance.getDate());
            pstmt.setBoolean(4,attendance.isPresent());

            pstmt.executeUpdate();
            return true;
        }catch (SQLException e){
            System.err.println("[ATTENDANCE] Error adding: " + e.getMessage());
            return false;
        }
    }

    public List<Attendance> getAttendanceByStudent(int studentId) {
        String sql = "SELECT * FROM attendance WHERE student_id=?";
        List<Attendance> list = new ArrayList<>();

        try(Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1,studentId);
            ResultSet rs =pstmt.executeQuery();

            while (rs.next()){
                list.add(new Attendance(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getInt("group_id"),
                        rs.getString("date"),
                        rs.getBoolean("present")
                ));
            }
        }catch (SQLException e){
            System.err.println("[ATTENDANCE] Error fetching for student: " + e.getMessage());
        }

        return list;
    }

    public List<Attendance> getAttendanceForStudentInGroup(int studentId,int groupId) {
        String sql = "SELECT * FROM attendance WHERE student_id=? AND group_id=?";
        List<Attendance> list = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, groupId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(new Attendance(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getInt("group_id"),
                        rs.getString("date"),
                        rs.getBoolean("present")
                ));
            }

        } catch (SQLException e) {
            System.err.println("[ATTENDANCE] Error fetching for student+group: " + e.getMessage());
        }

        return list;
    }

    public boolean updateAttendance(Attendance attendance) {
        String sql = "UPDATE attendance SET present=? WHERE id=?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, attendance.isPresent());
            pstmt.setInt(2, attendance.getId());

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("[ATTENDANCE] Error updating: " + e.getMessage());
            return false;
        }
    }
}
