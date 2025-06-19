package com.faceScan.controller;

import com.faceScan.controller.RegisterController;
import com.faceScan.dao.UserDAO;
import javafx.scene.control.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RegisterControllerTest {

    private RegisterController controller;
    private UserDAO userDAOMock;

    @BeforeEach
    void setup() {
        controller = new RegisterController(userDAOMock);

        userDAOMock = mock(UserDAO.class);
        controller.setUserDAO(userDAOMock);

        controller.setUsernameField(new TextField());
        controller.setPasswordField(new PasswordField());
        controller.setConfirmPasswordField(new PasswordField());
        controller.setFirstNameField(new TextField());
        controller.setLastNameField(new TextField());
        controller.setRoleField(new TextField());
        controller.setMessageLabel(new Label());
    }

    @Test
    void testEmptyFieldsShowsError() {
        controller.getUsernameField().setText("");
        controller.onRegister();
        assertEquals("Fill in all fields!", controller.getMessageLabel().getText());
    }

    @Test
    void testPasswordMismatchShowsError() {
        controller.getUsernameField().setText("user");
        controller.getPasswordField().setText("pass1");
        controller.getConfirmPasswordField().setText("pass2");
        controller.getFirstNameField().setText("A");
        controller.getLastNameField().setText("B");
        controller.getRoleField().setText("student");
        controller.onRegister();
        assertEquals("Passwords do not match!", controller.getMessageLabel().getText());
    }

    @Test
    void testInvalidRoleShowsError() {
        // ustaw poprawne pozostałe pola
        controller.getUsernameField().setText("user");
        controller.getPasswordField().setText("pass");
        controller.getConfirmPasswordField().setText("pass");
        controller.getFirstNameField().setText("A");
        controller.getLastNameField().setText("B");
        controller.getRoleField().setText("invalidRole");
        controller.onRegister();
        assertEquals("The role must be 'student' or 'professor", controller.getMessageLabel().getText());
    }

    @Test
    void testSuccessfulRegistration() {
        when(userDAOMock.registerUser(any())).thenReturn(true);

        controller.getUsernameField().setText("user");
        controller.getPasswordField().setText("pass");
        controller.getConfirmPasswordField().setText("pass");
        controller.getFirstNameField().setText("A");
        controller.getLastNameField().setText("B");
        controller.getRoleField().setText("student");

        controller.onRegister();

        verify(userDAOMock).registerUser(any());
        assertEquals("Account created! Redirect to login...", controller.getMessageLabel().getText());
    }

    @Test
    void testFailedRegistration() {
        when(userDAOMock.registerUser(any())).thenReturn(false);

        controller.getUsernameField().setText("user");
        controller.getPasswordField().setText("pass");
        controller.getConfirmPasswordField().setText("pass");
        controller.getFirstNameField().setText("A");
        controller.getLastNameField().setText("B");
        controller.getRoleField().setText("student");

        controller.onRegister();

        verify(userDAOMock).registerUser(any());
        assertEquals("Registration failed (login taken?)", controller.getMessageLabel().getText());
    }
}
