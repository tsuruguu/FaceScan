package com.faceScan.controller;

import com.faceScan.dao.UserDAO;
import com.faceScan.model.User;
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

    private final UserDAO userDAO = new UserDAO();


    @FXML
    private void onLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Wprowadź login i hasło.");
            return;
        }

        User user = userDAO.loginUser(username, password);

        if (user != null) {
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Zalogowano pomyślnie!");

            // Przejdź do dashboardu
            openDashboard(user);
        } else {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Niepoprawny login lub hasło.");
        }
    }

    private void openDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard_view.fxml"));
            Parent root = loader.load();

//             DashboardController controller = loader.getController();
//             controller.setUser(currentUser);

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("FaceScan - Dashboard");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Błąd ładowania dashboardu.");
        }
    }




    @FXML
    private void onRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("Wypełnij wszystkie pola!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Hasła nie są takie same!");
            return;
        }

        User user = new User(username, password);
        boolean success = userDAO.registerUser(user);

        if (success) {
            messageLabel.setText("Zarejestrowano! Możesz się teraz zalogować.");
            clearRegisterFields();
        } else {
            messageLabel.setText("Rejestracja nie powiodła się.");
        }
    }

    private void clearRegisterFields() {
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }

    private void openDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard_view.fxml"));
            Parent root = loader.load();

            DashboardController dashboardController = loader.getController();
            dashboardController.setCurrentUser(user);

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("FaceScan - Dashboard");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Błąd ładowania dashboardu.");
        }
    }


    public void setMessage(String message) {
        messageLabel.setStyle("-fx-text-fill: green;");
        messageLabel.setText(message);
    }

    @FXML
    private void goToRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register_view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("FaceScan - Rejestracja");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
