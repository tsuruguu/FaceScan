package com.faceScan.controller;

import com.faceScan.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

public class StudentDetailsController {

    @FXML private Label nameLabel;
    @FXML private Label usernameLabel;
    @FXML private Label roleLabel;
    @FXML private ImageView photoView;

    public void setUser(User user) {
        nameLabel.setText(user.getFirstName() + " " + user.getLastName());
        usernameLabel.setText(user.getUsername());
        roleLabel.setText(user.getRole());

        if (user.getPhotoPath() != null && !user.getPhotoPath().isEmpty()) {
            File file = new File(user.getPhotoPath());
            if (file.exists()) {
                photoView.setImage(new Image(file.toURI().toString()));
            } else {
                System.out.println("Plik nie istnieje: " + user.getPhotoPath());
            }
        }
    }
}
