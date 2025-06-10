package com.faceScan.controller;

import com.faceScan.dao.StudentDAO;
import com.faceScan.model.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;

public class GroupController {
    @FXML private Label groupNameLabel;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private Label photoPathLabel;
    @FXML private TableView<Student> studentsTable;
    @FXML private TableColumn<Student, Number> colId;
    @FXML private TableColumn<Student, String> colFirstName;
    @FXML private TableColumn<Student, String> colLastName;
    @FXML private TableColumn<Student, String> colPhoto;

    private int groupId;
    private String photoFilePath;
    private final ObservableList<Student> students = FXCollections.observableArrayList();
    private final StudentDAO studentDAO = new StudentDAO();

    public void setGroup(int groupId, String groupName) {
        this.groupId = groupId;
        groupNameLabel.setText("Grupa: " + groupName);
        loadStudents();
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> c.getValue().idProperty());
        colFirstName.setCellValueFactory(c -> c.getValue().firstNameProperty());
        colLastName.setCellValueFactory(c -> c.getValue().lastNameProperty());
        colPhoto.setCellValueFactory(c -> c.getValue().photoPathProperty());
        studentsTable.setItems(students);
        photoPathLabel.setText("Brak zdjęcia");
    }

    private void loadStudents() {
        try {
            List<Student> list = studentDAO.getStudentsByGroupId(groupId);
            students.setAll(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSelectPhoto() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Obrazy", "*.jpg","*.png","*.jpeg"));
        File sel = fc.showOpenDialog(studentsTable.getScene().getWindow());
        if (sel != null) {
            photoFilePath = sel.getAbsolutePath();
            photoPathLabel.setText(sel.getName());
        }
    }

    @FXML
    private void handleAddStudent() {
        String fn = firstNameField.getText().trim();
        String ln = lastNameField.getText().trim();
        if (fn.isEmpty() || ln.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Podaj imię i nazwisko").showAndWait();
            return;
        }

        String dst = null;
        if (photoFilePath != null) {
            try {
                File src = new File(photoFilePath);
                File dir = new File("student_photos");
                if (!dir.exists()) dir.mkdir();
                File t = new File(dir, System.currentTimeMillis()+"_"+src.getName());
                Files.copy(src.toPath(), t.toPath(), StandardCopyOption.REPLACE_EXISTING);
                dst = t.getAbsolutePath();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Błąd zapisu zdjęcia").showAndWait();
                return;
            }
        }

        try {
            studentDAO.addStudent(groupId, fn, ln, dst);
            firstNameField.clear(); lastNameField.clear();
            photoPathLabel.setText("Brak zdjęcia");
            photoFilePath = null;
            loadStudents();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteStudent() {
        Student sel = studentsTable.getSelectionModel().getSelectedItem();
        if (sel != null) {
            try {
                studentDAO.deleteStudent(sel.getId());
                loadStudents();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
