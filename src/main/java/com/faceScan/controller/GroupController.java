package com.faceScan.controller;

import com.faceScan.dao.StudentDAO;
import com.faceScan.model.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
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
        studentsTable.setRowFactory(tv -> {
            TableRow<Student> row = new TableRow<>();
            row.setOnMouseClicked(evt -> {
                if (evt.getClickCount() == 2 && !row.isEmpty()) {
                    Student s = row.getItem();
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/StudentDetails.fxml"));
                        Parent root = loader.load();
                        StudentDetailsController ctrl = loader.getController();
                        ctrl.setStudent(s);

                        Stage dialog = new Stage();
                        dialog.initOwner(studentsTable.getScene().getWindow());
                        dialog.initModality(Modality.APPLICATION_MODAL);
                        dialog.setTitle("Szczegóły studenta");
                        dialog.setScene(new Scene(root));
                        dialog.showAndWait();

                        loadStudents();
                    } catch (IOException e) {
                        e.printStackTrace();
                        new Alert(Alert.AlertType.ERROR, "Nie udało się otworzyć szczegółów").showAndWait();
                    }
                }
            });
            return row;
        });

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

    @FXML
    private void handleCheckAttendance() {
        System.out.println(">>> handleCheckAttendance() wywołane!");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_view.fxml"));
            Parent root = loader.load();

            System.out.println(">>> main_view.fxml załadowane, pobieram controller");
            MainController mc = loader.getController();
            mc.setCurrentGroupId(groupId);  // przekazujemy tylko groupId

            Stage stage = (Stage) studentsTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Sprawdzanie obecności — " + groupNameLabel.getText());
            stage.show();

            System.out.println(">>> scena main_view ustawiona, uruchamiam kamerę");
            mc.onStartClicked();

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Nie udało się otworzyć ekranu obecności.").showAndWait();
        }
    }

}

