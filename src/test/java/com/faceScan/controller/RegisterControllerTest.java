package com.faceScan.controller;

import com.faceScan.dao.IUserDAO;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class RegisterControllerTest {

    private RegisterController controller;
    private IUserDAO userDAOMock;

    @BeforeAll
    public static void initJavaFX() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(() -> {
            new JFXPanel();
            latch.countDown();
        });
        latch.await();
    }

    @BeforeEach
    void setup() {
        userDAOMock = mock(IUserDAO.class);
        controller = new RegisterController(userDAOMock);
        controller.setUserDAO(userDAOMock);

        Platform.runLater(() -> {
            controller.setUsernameField(new TextField());
            controller.setPasswordField(new PasswordField());
            controller.setConfirmPasswordField(new PasswordField());
            controller.setFirstNameField(new TextField());
            controller.setLastNameField(new TextField());
            controller.setRoleField(new TextField());
            controller.setMessageLabel(new Label());
        });

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testEmptyFieldsShowsError() {
        Platform.runLater(() -> {
            controller.getUsernameField().setText("");
            controller.onRegister();
            assertEquals("Fill in all fields!", controller.getMessageLabel().getText());
        });
    }

    @Test
    void testPasswordMismatchShowsError() {
        Platform.runLater(() -> {
            controller.getUsernameField().setText("user");
            controller.getPasswordField().setText("pass1");
            controller.getConfirmPasswordField().setText("pass2");
            controller.getFirstNameField().setText("A");
            controller.getLastNameField().setText("B");
            controller.getRoleField().setText("student");

            controller.onRegister();
            assertEquals("Passwords do not match!", controller.getMessageLabel().getText());
        });
    }

    @Test
    void testInvalidRoleShowsError() {
        Platform.runLater(() -> {
            controller.getUsernameField().setText("user");
            controller.getPasswordField().setText("pass");
            controller.getConfirmPasswordField().setText("pass");
            controller.getFirstNameField().setText("A");
            controller.getLastNameField().setText("B");
            controller.getRoleField().setText("invalidRole");

            controller.onRegister();
            assertEquals("The role must be 'student' or 'professor", controller.getMessageLabel().getText());
        });
    }

    @Test
    void testSuccessfulRegistration() {
        when(userDAOMock.registerUser(any())).thenReturn(true);

        Platform.runLater(() -> {
            controller.getUsernameField().setText("user");
            controller.getPasswordField().setText("pass");
            controller.getConfirmPasswordField().setText("pass");
            controller.getFirstNameField().setText("A");
            controller.getLastNameField().setText("B");
            controller.getRoleField().setText("student");

            controller.onRegister();

            verify(userDAOMock).registerUser(any());
            assertEquals("Account created! Redirect to login...", controller.getMessageLabel().getText());
        });
    }

    @Test
    void testFailedRegistration() {
        when(userDAOMock.registerUser(any())).thenReturn(false);

        Platform.runLater(() -> {
            controller.getUsernameField().setText("user");
            controller.getPasswordField().setText("pass");
            controller.getConfirmPasswordField().setText("pass");
            controller.getFirstNameField().setText("A");
            controller.getLastNameField().setText("B");
            controller.getRoleField().setText("student");

            controller.onRegister();

            verify(userDAOMock).registerUser(any());
            assertEquals("Registration failed (login taken?)", controller.getMessageLabel().getText());
        });
    }
}
