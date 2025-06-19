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

    private UserDAO userDAO = new UserDAO();

    @FXML
    void onLogin() {
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

    public TextField getUsernameField() {return usernameField;}
    public void setUsernameField(TextField usernameField) {this.usernameField = usernameField;}

    public PasswordField getPasswordField() {return passwordField;}
    public void setPasswordField(PasswordField passwordField) {this.passwordField = passwordField;}

    public Label getMessageLabel() {return messageLabel;}
    public void setMessageLabel(Label messageLabel) {this.messageLabel = messageLabel;}

    public PasswordField getConfirmPasswordField() {return confirmPasswordField;}
    public void setConfirmPasswordField(PasswordField confirmPasswordField) {this.confirmPasswordField = confirmPasswordField;}

    public TextField getFirstNameField() {return firstNameField;}
    public void setFirstNameField(TextField firstNameField) {this.firstNameField = firstNameField;}

    public TextField getLastNameField() {return lastNameField;}
    public void setLastNameField(TextField lastNameField) {this.lastNameField = lastNameField;}

    public TextField getRoleField() {return roleField;}
    public void setRoleField(TextField roleField) {this.roleField = roleField;}

    public UserDAO getUserDAO() {return userDAO;}
    public void setUserDAO(UserDAO userDAO) {this.userDAO = userDAO;}


}
