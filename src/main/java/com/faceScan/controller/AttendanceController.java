package com.faceScan.controller;

import com.faceScan.dao.AttendanceDAO;
import com.faceScan.dao.GroupMemberDAO;
import com.faceScan.model.Attendance;
import com.faceScan.model.Student;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AttendanceController {

    @FXML private TableView<Attendance> attendanceTable;
    @FXML private TableColumn<Attendance, String> colStudentName;
    @FXML private TableColumn<Attendance, String> colDate;
    @FXML private TableColumn<Attendance, Boolean> colPresent;

    @FXML private DatePicker datePicker;
    @FXML private Button saveButton;
    @FXML private Label groupLabel;

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final GroupMemberDAO groupMemberDAO = new GroupMemberDAO();
    private final ObservableList<Attendance> attendanceList = FXCollections.observableArrayList();

    private int groupId;
    private String groupName;

    public void setGroup(int groupId, String groupName) {
        this.groupId = groupId;
        this.groupName = groupName;
        groupLabel.setText("Attendance – group: " + groupName);
        datePicker.setValue(LocalDate.now());
        loadAttendance();
    }

    @FXML
    public void initialize() {
        colStudentName.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getStudentName()));
        colDate.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getDate()));
        colPresent.setCellValueFactory(cellData -> {
            SimpleBooleanProperty property = new SimpleBooleanProperty(cellData.getValue().isPresent());
            property.addListener((obs, oldVal, newVal) ->
                    cellData.getValue().setPresent(newVal));
            return property;
        });

        attendanceTable.setItems(attendanceList);
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> loadAttendance());
    }

    private void loadAttendance() {
        LocalDate date = datePicker.getValue();
        List<Student> students = groupMemberDAO.getStudentsInGroup(groupId);
        List<Attendance> loaded = new ArrayList<>();

        for (Student s : students) {
            List<Attendance> records = attendanceDAO.getAttendanceForStudentInGroup(s.getId(), groupId);
            Attendance record = records.stream()
                    .filter(a -> a.getDate().equals(date.toString()))
                    .findFirst()
                    .orElse(new Attendance(0, s.getId(), groupId, date.toString(), false));

            record.setStudentName(s.toString());
            loaded.add(record);
        }

        attendanceList.setAll(loaded);
    }

    @FXML
    void handleSaveAttendance() {
        int successes = 0;

        for (Attendance a : attendanceList) {
            if (a.getId() == 0) {
                if (attendanceDAO.addAttendance(a)) successes++;
            } else {
                if (attendanceDAO.updateAttendance(a)) successes++;
            }
        }

        new Alert(Alert.AlertType.INFORMATION,
                "Attendance recorded for " + successes + " students.")
                .showAndWait();

        loadAttendance();
    }

    public TableView<Attendance> getAttendanceTable() {return attendanceTable;}
}
