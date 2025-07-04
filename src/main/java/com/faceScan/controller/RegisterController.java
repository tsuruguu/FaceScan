package com.faceScan.controller;

import com.faceScan.dao.IUserDAO;
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

    private IUserDAO userDAO = new UserDAO();

    public RegisterController() {
        this.userDAO = new UserDAO();
    }

    public RegisterController(IUserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @FXML
    void onRegister() {
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

    public void setUserDAO(IUserDAO userDAO) { this.userDAO = userDAO; }

    public TextField getUsernameField() {return usernameField;}
    public void setUsernameField(TextField usernameField) {this.usernameField = usernameField;}

    public PasswordField getPasswordField() {return passwordField;}
    public void setPasswordField(PasswordField passwordField) {this.passwordField = passwordField;}

    public PasswordField getConfirmPasswordField() {return confirmPasswordField;}
    public void setConfirmPasswordField(PasswordField confirmPasswordField) {this.confirmPasswordField = confirmPasswordField;}

    public TextField getFirstNameField() {return firstNameField;}
    public void setFirstNameField(TextField firstNameField) {this.firstNameField = firstNameField;}

    public TextField getLastNameField() {return lastNameField;}
    public void setLastNameField(TextField lastNameField) {this.lastNameField = lastNameField;}

    public TextField getRoleField() {return roleField;}
    public void setRoleField(TextField roleField) {this.roleField = roleField;}

    public Label getMessageLabel() {return messageLabel;}
    public void setMessageLabel(Label messageLabel) {this.messageLabel = messageLabel;}

}
