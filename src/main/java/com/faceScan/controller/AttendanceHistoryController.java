package com.faceScan.controller;

import com.faceScan.dao.AttendanceDAO;
import com.faceScan.model.Attendance;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class AttendanceHistoryController {

    @FXML
    TableView<Attendance> attendanceTable;
    @FXML private TableColumn<Attendance, String> dateCol;
    @FXML private TableColumn<Attendance, Boolean> presentCol;

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    public void loadHistory(int studentId, int groupId) {
        List<Attendance> history = attendanceDAO.getAttendanceForStudentInGroup(studentId, groupId);
        attendanceTable.getItems().setAll(history);
    }

    @FXML
    public void initialize() {
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        presentCol.setCellValueFactory(new PropertyValueFactory<>("present"));
    }
}
