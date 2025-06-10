package com.faceScan.controller;

import com.faceScan.dao.UserDAO;
import com.faceScan.model.User;
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
    @FXML private Label messageLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void onRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Wypełnij wszystkie pola!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Hasła się nie zgadzają!");
            return;
        }

        boolean success = userDAO.registerUser(new User(username, password));
        if (success) {
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Konto utworzone! Przechodzę do logowania...");

            // Mała pauza na przeczytanie komunikatu (opcjonalnie)
            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException ignored) {}
                javafx.application.Platform.runLater(() -> goToLogin());
            }).start();

        } else {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Rejestracja nie powiodła się - sprawdź nazwę użytkownika.");
        }
    }

    @FXML
    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login_view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("FaceScan - Logowanie");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
