package com.faceScan.controller;

import com.faceScan.dao.GroupDAO;
import com.faceScan.model.Group;
import com.faceScan.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public class DashboardController {

    @FXML private ListView<Group> groupListView;
    @FXML private TextField groupNameField;
    @FXML private Button addGroupButton;
    @FXML private Button deleteGroupButton;

    private User currentUser;
    private final GroupDAO groupDAO = new GroupDAO();

    private final ObservableList<Group> groups = FXCollections.observableArrayList();

    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadGroups();
    }

    private void loadGroups() {
        groups.clear();
        groups.addAll(groupDAO.getGroupsByUserId(currentUser.getId()));
        groupListView.setItems(groups);
    }

    @FXML
    private void onAddGroup() {
        String name = groupNameField.getText().trim();
        if (name.isEmpty()) return;

        Group group = new Group(name, currentUser.getId());
        groupDAO.save(group);
        loadGroups();
        groupNameField.clear();
    }

    @FXML
    private void onDeleteGroup() {
        Group selected = groupListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            groupDAO.delete(selected.getId());
            loadGroups();
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
        }
    }
}
