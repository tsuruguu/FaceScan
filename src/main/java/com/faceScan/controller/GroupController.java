package com.faceScan.controller;

import com.faceScan.dao.GroupMemberDAO;
import com.faceScan.dao.UserDAO;
import com.faceScan.model.User;

import javafx.beans.property.SimpleStringProperty;
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
import java.util.List;

public class GroupController {

    @FXML private Label groupNameLabel;
    @FXML private ComboBox<User> studentComboBox;
    @FXML private TableView<User> studentsTable;
    @FXML private TableColumn<User, String> colFirstName;
    @FXML private TableColumn<User, String> colLastName;
    @FXML private Label photoPathLabel;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;


    private int groupId;
    private final ObservableList<User> students = FXCollections.observableArrayList();

    private final GroupMemberDAO groupMemberDAO = new GroupMemberDAO();
    private final UserDAO userDAO = new UserDAO();

    public void setGroup(int groupId, String groupName) {
        this.groupId = groupId;
        groupNameLabel.setText("Group: " + groupName);
        loadStudents();
    }

    @FXML
    public void initialize() {
        colFirstName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFirstName()));
        colLastName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLastName()));
        studentsTable.setItems(students);

        studentsTable.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();

            row.setOnMouseClicked(evt -> {
                if (evt.getClickCount() == 2 && !row.isEmpty()) {
                    User u = row.getItem();
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student_details.fxml"));
                        Parent root = loader.load();
                        StudentDetailsController ctrl = loader.getController();
                        ctrl.setUser(u);

                        Stage dialog = new Stage();
                        dialog.initOwner(studentsTable.getScene().getWindow());
                        dialog.initModality(Modality.APPLICATION_MODAL);
                        dialog.setTitle("Student details.");
                        dialog.setScene(new Scene(root));
                        dialog.showAndWait();

                        loadStudents();
                    } catch (IOException e) {
                        e.printStackTrace();
                        new Alert(Alert.AlertType.ERROR, "Could not open details.").showAndWait();
                    }
                }
            });

            ContextMenu menu = new ContextMenu();
            MenuItem remove = new MenuItem("Delete student from group.");
            remove.setOnAction(e -> {
                User u = row.getItem();
                if (groupMemberDAO.removeStudentFromGroup(u.getId(), groupId)) {
                    loadStudents();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete student from group.").showAndWait();
                }
            });
            menu.getItems().add(remove);
            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(menu)
            );

            return row;
        });

        studentComboBox.setItems(FXCollections.observableArrayList(userDAO.getAllStudents()));
    }

    private void loadStudents() {
        List<User> list = userDAO.getAllStudents();
        List<User> inGroup = list.stream()
                .filter(u -> groupMemberDAO.getGroupsForStudent(u.getId())
                        .stream().anyMatch(g -> g.getId() == groupId))
                .toList();

        students.setAll(inGroup);
    }

    @FXML
    private void handleSelectPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz zdjęcie");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Obrazy", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(studentsTable.getScene().getWindow());
        if (file != null) {
            photoPathLabel.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void handleAddStudent() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String photoPath = photoPathLabel.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || photoPath.equals("Brak zdjęcia")) {
            new Alert(Alert.AlertType.WARNING, "Uzupełnij dane i wybierz zdjęcie.").showAndWait();
            return;
        }

        User newUser = new User("s" + System.currentTimeMillis(), "pass", "student", firstName, lastName);
        newUser.setPhotoPath(photoPath);
        if (!userDAO.registerUser(newUser)) {
            new Alert(Alert.AlertType.ERROR, "Nie udało się dodać użytkownika.").showAndWait();
            return;
        }

        User created = userDAO.getUserByUsername(newUser.getUsername());
        if (created != null && groupMemberDAO.addStudentToGroup(created.getId(), groupId)) {
            loadStudents();
        } else {
            new Alert(Alert.AlertType.ERROR, "Nie udało się dodać studenta do grupy.").showAndWait();
        }
    }

    @FXML
    private void handleDeleteStudent() {
        User selected = studentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.INFORMATION, "Zaznacz studenta do usunięcia.").showAndWait();
            return;
        }

        boolean success = groupMemberDAO.removeStudentFromGroup(selected.getId(), groupId);
        if (success) {
            loadStudents();
        } else {
            new Alert(Alert.AlertType.ERROR, "Nie udało się usunąć studenta z grupy.").showAndWait();
        }
    }

    @FXML
    private void handleAddExistingStudent() {
        User selected = studentComboBox.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Wybierz studenta z listy.").showAndWait();
            return;
        }

        if (groupMemberDAO.addStudentToGroup(selected.getId(), groupId)) {
            loadStudents();
        } else {
            new Alert(Alert.AlertType.ERROR, "Nie udało się dodać studenta do grupy.").showAndWait();
        }
    }

    @FXML
    private void handleCheckAttendance() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_view.fxml"));
            Parent root = loader.load();

            MainController mc = loader.getController();
            mc.setCurrentGroupId(groupId);

            Stage stage = (Stage) studentsTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Checking attendance – Group");
            stage.show();

            mc.onStartClicked();

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to open presence view.").showAndWait();
        }
    }
}
