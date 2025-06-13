package com.faceScan.controller;

import com.faceScan.dao.GroupDAO;
import com.faceScan.dao.GroupMemberDAO;
import com.faceScan.model.Group;
import com.faceScan.model.User;
import com.faceScan.session.SessionManager;
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

    private final GroupDAO groupDAO = new GroupDAO();
    private final GroupMemberDAO groupMemberDAO = new GroupMemberDAO();

    private final ObservableList<Group> groups = FXCollections.observableArrayList();

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
        } else{
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
    private void onAddGroup() {
        User currentUser = SessionManager.getCurrentUser();
        String name = groupNameField.getText().trim();
        if (name.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Enter group's name!").showAndWait();
            return;
        }
        Group group = new Group(name, currentUser.getId());
        if (groupDAO.addGroup(group)) {
            loadGroupsForProfessor(currentUser.getId());
            groupNameField.clear();
        } else {
            new Alert(Alert.AlertType.ERROR, "Failed to add group.").showAndWait();
        }
    }

    @FXML
    private void onDeleteGroup() {
        User currentUser = SessionManager.getCurrentUser();
        Group selected = groupListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Choose group to delete!").showAndWait();
            return;
        }
        if (groupDAO.deleteGroup(selected.getId())) {
            loadGroupsForProfessor(currentUser.getId());
        } else {
            new Alert(Alert.AlertType.ERROR, "Failed to delete group.").showAndWait();
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
            stage.setTitle("Group: " + selected.getName());
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error opening group view.").showAndWait();
        }
    }
}
