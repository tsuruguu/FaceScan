package com.faceScan.controller;

import com.faceScan.model.User;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class StudentDetailsControllerTest extends ApplicationTest {

    private StudentDetailsController controller;

    @BeforeEach
    void setUp() {
        controller = new StudentDetailsController();
        controller.setNameLabel(new Label());
        controller.setUsernameLabel(new Label());
        controller.setRoleLabel(new Label());
        controller.setPhotoView(new ImageView());
    }

    @Test
    void testSetUser_withPhoto() {
        User user = new User(1, "user123", "student", "Anna", "Kowalska", "src/test/resources/one_face.jpg");
        controller.setUser(user);

        assertEquals("Anna Kowalska", controller.getNameLabel().getText());
        assertEquals("user123", controller.getUsernameLabel().getText());
        assertEquals("student", controller.getRoleLabel().getText());
        assertNotNull(controller.getPhotoView().getImage());
    }

    @Test
    void testSetUser_withoutPhoto() {
        User user = new User(1, "user456", "student", "Jan", "Nowak", null);
        controller.setUser(user);

        assertEquals("Jan Nowak", controller.getNameLabel().getText());
        assertEquals("user456", controller.getUsernameLabel().getText());
        assertEquals("student", controller.getRoleLabel().getText());
        assertNull(controller.getPhotoView().getImage());
    }
}
