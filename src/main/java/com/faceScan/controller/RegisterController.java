package com.faceScan.controller;

import com.faceScan.dao.UserDAO;
import com.faceScan.model.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField roleField;
    @FXML private Label messageLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void onRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String role = roleField.getText().trim().toLowerCase();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
                || firstName.isEmpty() || lastName.isEmpty() || role.isEmpty()) {
            showMessage("Fill in all fields!", "red");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showMessage("Passwords do not match!", "red");
            return;
        }

        if (!role.equals("student") && !role.equals("professor")) {
            showMessage("The role must be 'student' or 'professor", "red");
            return;
        }

        User user = new User(username, password, role, firstName, lastName);
        boolean success = userDAO.registerUser(user);

        if (success) {
            showMessage("Account created! Redirect to login...", "green");

            usernameField.clear();
            passwordField.clear();
            confirmPasswordField.clear();
            firstNameField.clear();
            lastNameField.clear();
            roleField.clear();

            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException ignored) {}
                Platform.runLater(this::goToLogin);
            }).start();
        } else {
            showMessage("\n" +
                    "Registration failed (login taken?)", "red");
        }
    }

    @FXML
    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login_view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("FaceScan - Login");
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Error loading login view", "red");
        }
    }

    private void showMessage(String text, String color) {
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
        messageLabel.setText(text);
    }
}
