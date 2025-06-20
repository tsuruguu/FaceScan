package com.faceScan.controller;

import com.faceScan.dao.UserDAO;
import com.faceScan.model.User;
import com.faceScan.session.SessionManager;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class LoginControllerTest {

    private LoginController controller;
    private UserDAO userDAOMock;

    @BeforeAll
    static void initToolkit() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);
        latch.await();
    }

    @BeforeEach
    void setup() {
        controller = new LoginController();
        controller.setTestMode(true);
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
        String username = "user";
        String password = "pass";
        User testUser = new User(1, username, "student", "Anna", "Kowalska", null);

        when(userDAOMock.loginUser(username, password)).thenReturn(testUser);
        controller.getUsernameField().setText(username);
        controller.getPasswordField().setText(password);

        try (MockedStatic<SessionManager> sessionMock = Mockito.mockStatic(SessionManager.class)) {
            controller.onLogin();
            sessionMock.verify(() -> SessionManager.login(testUser));
        }

        assertEquals("Logged in successfully!", controller.getMessageLabel().getText());
    }

    @Test
    void testLoginFail() {
        when(userDAOMock.loginUser("user", "wrong")).thenReturn(null);

        controller.getUsernameField().setText("user");
        controller.getPasswordField().setText("wrong");

        controller.onLogin();

        assertEquals("Wrong username or password!", controller.getMessageLabel().getText());
    }
}
