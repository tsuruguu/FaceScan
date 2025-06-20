package com.faceScan.controller;

import com.faceScan.dao.GroupDAO;
import com.faceScan.dao.GroupMemberDAO;
import com.faceScan.model.Group;
import com.faceScan.model.User;
import com.faceScan.session.SessionManager;
import com.faceScan.util.AlertFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.List;

public class DashboardController {

    @FXML private ListView<Group> groupListView;
    @FXML private TextField groupNameField;
    @FXML private Button addGroupButton;
    @FXML private Button deleteGroupButton;
    @FXML private Label roleLabel;

    private GroupDAO groupDAO = new GroupDAO();
    private GroupMemberDAO groupMemberDAO = new GroupMemberDAO();
    private AlertFactory alertFactory = new AlertFactory(); // domyślna

    public void setAlertFactory(AlertFactory alertFactory) {
        this.alertFactory = alertFactory;
    }

    @FXML
    public void initialize() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            System.err.println("[DASHBOARD] Brak użytkownika w sesji");
            return;
        }

        roleLabel.setText("Zalogowano jako: " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");

        if (currentUser.isProfessor()) {
            loadGroupsForProfessor(currentUser.getId());
            enableProfessorControls(true);
        } else {
            loadGroupsForStudent(currentUser.getId());
            enableProfessorControls(false);
        }
    }

    private void enableProfessorControls(boolean enabled) {
        groupNameField.setDisable(!enabled);
        addGroupButton.setDisable(!enabled);
        deleteGroupButton.setDisable(!enabled);
    }

    private void loadGroupsForProfessor(int professorId) {
        groups.clear();
        List<Group> professorGroups = groupDAO.getGroupsByUserId(professorId);
        groups.addAll(professorGroups);
        groupListView.setItems(groups);
    }

    private void loadGroupsForStudent(int studentId) {
        groups.clear();
        List<Group> studentGroups = groupMemberDAO.getGroupsForStudent(studentId);
        groups.addAll(studentGroups);
        groupListView.setItems(groups);
    }

    @FXML
    void onAddGroup() {
        User currentUser = SessionManager.getCurrentUser();
        String name = groupNameField.getText().trim();
        if (name.isEmpty()) {
            alertFactory.createAlert(Alert.AlertType.WARNING, "Uwaga", "Wprowadź nazwę grupy!").showAndWait();
            return;
        }
        Group group = new Group(name, currentUser.getId());
        if (groupDAO.addGroup(group)) {
            loadGroupsForProfessor(currentUser.getId());
            groupNameField.clear();
        } else {
            alertFactory.createAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się dodać grupy.").showAndWait();
        }
    }

    @FXML
    void onDeleteGroup() {
        User currentUser = SessionManager.getCurrentUser();
        Group selected = groupListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alertFactory.createAlert(Alert.AlertType.WARNING, "Uwaga", "Wybierz grupę do usunięcia!").showAndWait();
            return;
        }
        if (groupDAO.deleteGroup(selected.getId())) {
            loadGroupsForProfessor(currentUser.getId());
        } else {
            alertFactory.createAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się usunąć grupy.").showAndWait();
        }
    }

    @FXML
    private void onGroupSelected() {
        Group selected = groupListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/group_view.fxml"));
            Parent root = loader.load();

            GroupController controller = loader.getController();
            controller.setGroup(selected.getId(), selected.getName());

            Stage stage = new Stage();
            stage.setTitle("Grupa: " + selected.getName());
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            alertFactory.createAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się otworzyć widoku grupy.").showAndWait();
        }
    }

    private final ObservableList<Group> groups = FXCollections.observableArrayList();

    public void setGroupListView(ListView<Group> groupListView) { this.groupListView = groupListView; }
    public void setGroupNameField(TextField groupNameField) { this.groupNameField = groupNameField; }
    public void setAddGroupButton(Button addGroupButton) { this.addGroupButton = addGroupButton; }
    public void setDeleteGroupButton(Button deleteGroupButton) { this.deleteGroupButton = deleteGroupButton; }
    public void setRoleLabel(Label roleLabel) { this.roleLabel = roleLabel; }
    public void setGroupDAO(GroupDAO groupDAO) { this.groupDAO = groupDAO; }
    public void setGroupMemberDAO(GroupMemberDAO groupMemberDAO) { this.groupMemberDAO = groupMemberDAO; }

    public ListView<Group> getGroupListView() { return groupListView; }
    public TextField getGroupNameField() { return groupNameField; }
}
