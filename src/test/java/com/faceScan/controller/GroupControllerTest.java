package com.faceScan.controller;

import com.faceScan.dao.GroupMemberDAO;
import com.faceScan.dao.UserDAO;
import com.faceScan.model.User;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GroupControllerTest {

    private GroupController controller;
    private UserDAO userDAOMock;
    private GroupMemberDAO groupMemberDAOMock;

    @BeforeEach
    void setUp() {
        controller = new GroupController();

        userDAOMock = mock(UserDAO.class);
        groupMemberDAOMock = mock(GroupMemberDAO.class);

        controller.setUserDAO(userDAOMock);
        controller.setGroupMemberDAO(groupMemberDAOMock);

        controller.setGroupNameLabel(new Label());
        controller.setStudentComboBox(new ComboBox<>());
        controller.setStudentsTable(new TableView<>());
        controller.setColFirstName(new TableColumn<>());
        controller.setColLastName(new TableColumn<>());
        controller.setPhotoPathLabel(new Label());
        controller.setFirstNameField(new TextField());
        controller.setLastNameField(new TextField());
    }

    @Test
    void testSetGroupLoadsStudents() {
        int testGroupId = 1;
        String testGroupName = "Test Group";

        User student1 = new User(1, "u1", "student", "Anna", "Kowalska", null);
        User student2 = new User(2, "u2", "student", "Jan", "Nowak", null);

        when(userDAOMock.getAllStudents()).thenReturn(List.of(student1, student2));
        when(groupMemberDAOMock.getGroupsForStudent(1)).thenReturn(List.of(new com.faceScan.model.Group(testGroupId, "Test Group", 10)));
        when(groupMemberDAOMock.getGroupsForStudent(2)).thenReturn(List.of());

        controller.setGroup(testGroupId, testGroupName);

        assertEquals("Group: Test Group", controller.getGroupNameLabel().getText());

        ObservableList<User> loadedStudents = controller.getStudentsTable().getItems();
        assertEquals(1, loadedStudents.size());
        assertEquals(student1, loadedStudents.get(0));
    }

    @Test
    void testHandleAddStudentValidData() {
        controller.getFirstNameField().setText("Anna");
        controller.getLastNameField().setText("Kowalska");
        controller.getPhotoPathLabel().setText("/path/to/photo.png");

        when(userDAOMock.registerUser(any())).thenReturn(true);
        User createdUser = new User(10, "s123", "student", "Anna", "Kowalska", "/path/to/photo.png");
        when(userDAOMock.getUserByUsername(anyString())).thenReturn(createdUser);
        when(groupMemberDAOMock.addStudentToGroup(anyInt(), anyInt())).thenReturn(true);

        controller.setGroupId(5);

        controller.handleAddStudent();

        verify(userDAOMock).registerUser(any());
        verify(groupMemberDAOMock).addStudentToGroup(eq(createdUser.getId()), eq(5));
    }

    @Test
    void testHandleAddStudentMissingDataShowsAlert() {
        controller.getFirstNameField().setText("");
        controller.getLastNameField().setText("Kowalska");
        controller.getPhotoPathLabel().setText("Brak zdjęcia");
        controller.handleAddStudent();
    }

    @Test
    void testHandleDeleteStudentSuccess() {
        User user = new User(1, "user", "student", "Anna", "Kowalska", null);
        controller.getStudentsTable().getItems().add(user);
        controller.getStudentsTable().getSelectionModel().select(user);

        when(groupMemberDAOMock.removeStudentFromGroup(user.getId(), controller.getGroupId())).thenReturn(true);
        controller.handleDeleteStudent();
        verify(groupMemberDAOMock).removeStudentFromGroup(user.getId(), controller.getGroupId());
    }

    @Test
    void testHandleDeleteStudentNoSelectionShowsAlert() {
        controller.getStudentsTable().getSelectionModel().clearSelection();
        controller.handleDeleteStudent();
    }
}
