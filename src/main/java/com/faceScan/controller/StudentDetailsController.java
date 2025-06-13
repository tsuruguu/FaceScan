package com.faceScan.controller;

import com.faceScan.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class StudentDetailsController {

    @FXML private Label nameLabel;
    @FXML private Label usernameLabel;
    @FXML private Label roleLabel;

    public void setUser(User user) {
        nameLabel.setText(user.getFirstName() + " " + user.getLastName());
        usernameLabel.setText(user.getUsername());
        roleLabel.setText(user.getRole());
    }
}
