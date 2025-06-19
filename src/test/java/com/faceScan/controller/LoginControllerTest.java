package com.faceScan.controller;

import com.faceScan.controller.LoginController;
import com.faceScan.dao.UserDAO;
import com.faceScan.model.User;
import com.faceScan.session.SessionManager;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class LoginControllerTest {

    private LoginController controller;
    private UserDAO userDAOMock;

    @BeforeEach
    void setup() {
        controller = new LoginController();
        userDAOMock = mock(UserDAO.class);

        controller.setUserDAO(userDAOMock);

        controller.setUsernameField(new TextField());
        controller.setPasswordField(new PasswordField());
        controller.setMessageLabel(new Label());
    }

    @Test
    void testEmptyFieldsShowsError() {
        controller.getUsernameField().setText("");
        controller.getPasswordField().setText("");
        controller.onLogin();
        assertEquals("Enter username and password!", controller.getMessageLabel().getText());
    }

    @Test
    void testLoginSuccess() {
        controller.getUsernameField().setText("user");
        controller.getPasswordField().setText("pass");

        User user = new User(1, "user", "student", "Anna", "Kowalska", null);

        when(userDAOMock.loginUser("user", "pass")).thenReturn(user);

        try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {
            controller.onLogin();
            sessionMock.verify(() -> SessionManager.login(user));
        }

        assertEquals("Logged in successfully!", controller.getMessageLabel().getText());
    }

    @Test
    void testLoginFail() {
        controller.getUsernameField().setText("user");
        controller.getPasswordField().setText("wrong");

        when(userDAOMock.loginUser("user", "wrong")).thenReturn(null);

        controller.onLogin();

        assertEquals("Wrong username or password!", controller.getMessageLabel().getText());
    }
}
