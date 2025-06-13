package com.faceScan.controller;

import com.faceScan.dao.UserDAO;
import com.faceScan.model.User;
import com.faceScan.session.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField roleField;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void onLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Enter username and password!", "red");
            return;
        }

        User user = userDAO.loginUser(username, password);

        if (user != null) {
            SessionManager.login(user);
            showMessage("Logged in successfully!", "green");
            openDashboard(user);
        } else {
            showMessage("Wrong username or password!", "red");
        }
    }

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
            showMessage("Passwords do not match!","red");
            return;
        }

        if (!role.equals("student") && !role.equals("professor")) {
            showMessage("The role must be 'student' or 'professor'!", "red");
            return;
        }

        User user = new User(username, password, role, firstName, lastName);
        boolean success = userDAO.registerUser(user);

        if (success) {
            showMessage("Zarejestrowano! Możesz się teraz zalogować.", "green");
            clearRegisterFields();
        } else {
            showMessage("Rejestracja nie powiodła się.", "red");
        }
    }

    private void clearRegisterFields() {
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        firstNameField.clear();
        lastNameField.clear();
        roleField.clear();
    }

    private void openDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard_view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("FaceScan - Dashboard");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Dashboard Error");
        }
    }


    private void showMessage(String text, String color) {
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
        messageLabel.setText(text);
    }

    @FXML
    private void goToRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register_view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("FaceScan - Register");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
