package com.faceScan.controller;

import com.faceScan.dao.GroupMemberDAO;
import com.faceScan.dao.UserDAO;
import com.faceScan.model.StudentPresence;
import com.faceScan.model.User;
import com.faceScan.util.AlertFactory;
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
import javafx.scene.control.Alert.AlertType;

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

    private GroupMemberDAO groupMemberDAO = new GroupMemberDAO();
    private UserDAO userDAO = new UserDAO();

    private AlertFactory alertFactory = new AlertFactory();

    public void setAlertFactory(AlertFactory factory) {
        this.alertFactory = factory;
    }

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
                        alertFactory.createAlert(Alert.AlertType.ERROR, "Błąd", "Could not open details.").showAndWait();
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
                    alertFactory.createAlert(Alert.AlertType.ERROR, "Błąd", "Failed to delete student from group.").showAndWait();
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
    void handleAddStudent() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String photoPath = photoPathLabel.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || photoPath.equals("Brak zdjęcia")) {
            alertFactory.createAlert(AlertType.WARNING, "Message", "Uzupełnij dane i wybierz zdjęcie.").showAndWait();
            return;
        }

        User newUser = new User("s" + System.currentTimeMillis(), "pass", "student", firstName, lastName);
        newUser.setPhotoPath(photoPath);
        if (!userDAO.registerUser(newUser)) {
            alertFactory.createAlert(AlertType.ERROR, "Message", "Nie udało się dodać użytkownika.").showAndWait();
            return;
        }

        User created = userDAO.getUserByUsername(newUser.getUsername());
        if (created != null && groupMemberDAO.addStudentToGroup(created.getId(), groupId)) {
            loadStudents();
        } else {
            alertFactory.createAlert(AlertType.ERROR, "Message", "Nie udało się dodać studenta do grupy.").showAndWait();
        }
    }

    @FXML
    void handleDeleteStudent() {
        User selected = studentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alertFactory.createAlert(Alert.AlertType.INFORMATION, "Message", "Zaznacz studenta do usunięcia.").showAndWait();
            return;
        }

        boolean success = groupMemberDAO.removeStudentFromGroup(selected.getId(), groupId);
        if (success) {
            loadStudents();
        } else {
            alertFactory.createAlert(Alert.AlertType.ERROR, "Message", "Nie udało się usunąć studenta z grupy.").showAndWait();
        }
    }

    @FXML
    private void handleAddExistingStudent() {
        User selected = studentComboBox.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alertFactory.createAlert(Alert.AlertType.WARNING, "Message", "Wybierz studenta z listy.").showAndWait();
            return;
        }

        if (groupMemberDAO.addStudentToGroup(selected.getId(), groupId)) {
            loadStudents();
        } else {
            alertFactory.createAlert(Alert.AlertType.ERROR, "Message", "Nie udało się dodać studenta do grupy.").showAndWait();
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
            alertFactory.createAlert(Alert.AlertType.ERROR, "Błąd", "Failed to open presence view.").showAndWait();
        }
    }

    @FXML
    private void onShowHistory() {
        User selected = studentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alertFactory.createAlert(Alert.AlertType.WARNING, "Błąd", "Zaznacz studenta.").showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/attendance_history.fxml"));
            Parent root = loader.load();

            AttendanceHistoryController controller = loader.getController();
            controller.loadHistory(selected.getId(), groupId);

            Stage stage = new Stage();
            stage.setTitle("Historia obecności: " + selected.getFullName());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            alertFactory.createAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się otworzyć historii.").showAndWait();
        }
    }

    // Gettery i settery
    public Label getGroupNameLabel() { return groupNameLabel; }
    public void setGroupNameLabel(Label groupNameLabel) { this.groupNameLabel = groupNameLabel; }

    public ComboBox<User> getStudentComboBox() { return studentComboBox; }
    public void setStudentComboBox(ComboBox<User> studentComboBox) { this.studentComboBox = studentComboBox; }

    public TableView<User> getStudentsTable() { return studentsTable; }
    public void setStudentsTable(TableView<User> studentsTable) { this.studentsTable = studentsTable; }

    public TableColumn<User, String> getColFirstName() { return colFirstName; }
    public void setColFirstName(TableColumn<User, String> colFirstName) { this.colFirstName = colFirstName; }

    public TableColumn<User, String> getColLastName() { return colLastName; }
    public void setColLastName(TableColumn<User, String> colLastName) { this.colLastName = colLastName; }

    public Label getPhotoPathLabel() { return photoPathLabel; }
    public void setPhotoPathLabel(Label photoPathLabel) { this.photoPathLabel = photoPathLabel; }

    public TextField getFirstNameField() { return firstNameField; }
    public void setFirstNameField(TextField firstNameField) { this.firstNameField = firstNameField; }

    public TextField getLastNameField() { return lastNameField; }
    public void setLastNameField(TextField lastNameField) { this.lastNameField = lastNameField; }

    public int getGroupId() { return groupId; }
    public void setGroupId(int groupId) { this.groupId = groupId; }

    public GroupMemberDAO getGroupMemberDAO() { return groupMemberDAO; }
    public void setGroupMemberDAO(GroupMemberDAO groupMemberDAO) { this.groupMemberDAO = groupMemberDAO; }

    public UserDAO getUserDAO() { return userDAO; }
    public void setUserDAO(UserDAO userDAO) { this.userDAO = userDAO; }
}
