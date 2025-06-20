package com.faceScan.controller;

import com.faceScan.dao.GroupMemberDAO;
import com.faceScan.dao.UserDAO;
import com.faceScan.model.Group;
import com.faceScan.model.User;
import com.faceScan.util.AlertFactory;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GroupControllerTest {

    private GroupController controller;
    private UserDAO userDAOMock;
    private GroupMemberDAO groupMemberDAOMock;
    private AlertFactory alertFactoryMock;

    @BeforeAll
    static void initToolkit() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);
        latch.await();
    }

    @BeforeEach
    void setUp() {
        controller = new GroupController();

        userDAOMock = mock(UserDAO.class);
        groupMemberDAOMock = mock(GroupMemberDAO.class);
        alertFactoryMock = mock(AlertFactory.class);

        controller.setUserDAO(userDAOMock);
        controller.setGroupMemberDAO(groupMemberDAOMock);
        controller.setAlertFactory(alertFactoryMock);

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
    void testSetGroupLoadsStudents() throws Exception {
        int testGroupId = 1;
        String testGroupName = "Test Group";

        User student1 = new User(1, "u1", "student", "Anna", "Kowalska", null);
        User student2 = new User(2, "u2", "student", "Jan", "Nowak", null);

        when(userDAOMock.getAllStudents()).thenReturn(List.of(student1, student2));

        Group group = mock(Group.class);
        when(group.getId()).thenReturn(testGroupId);

        when(groupMemberDAOMock.getGroupsForStudent(1)).thenReturn(List.of(group));
        when(groupMemberDAOMock.getGroupsForStudent(2)).thenReturn(List.of());

        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            controller.initialize();
            controller.setGroup(testGroupId, testGroupName);
            latch.countDown();
        });

        latch.await();

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
    void testHandleAddStudentMissingDataShowsAlert() throws Exception {
        controller.getFirstNameField().setText("");
        controller.getLastNameField().setText("Kowalska");
        controller.getPhotoPathLabel().setText("Brak zdjęcia");

        // 🔧 Mock alertFactory + Alert
        AlertFactory alertFactoryMock = mock(AlertFactory.class);
        Alert alertMock = mock(Alert.class);
        when(alertFactoryMock.createAlert(any(), anyString(), anyString())).thenReturn(alertMock);
        when(alertMock.showAndWait()).thenReturn(Optional.of(ButtonType.OK));

        controller.setAlertFactory(alertFactoryMock);

        CountDownLatch latch = new CountDownLatch(1);
        final Throwable[] thrown = {null};

        Platform.runLater(() -> {
            try {
                controller.handleAddStudent();
            } catch (Throwable t) {
                thrown[0] = t;
            } finally {
                latch.countDown();
            }
        });

        latch.await();
        if (thrown[0] != null) throw new RuntimeException(thrown[0]);

        verify(alertFactoryMock).createAlert(eq(Alert.AlertType.WARNING), anyString(), contains("Uzupełnij"));
        verify(alertMock).showAndWait();
    }



    @Test
    void testHandleDeleteStudentSuccess() {
        User user = new User(1, "user", "student", "Anna", "Kowalska", null);
        controller.getStudentsTable().getItems().add(user);
        controller.getStudentsTable().getSelectionModel().select(user);

        controller.setGroupId(3);
        when(groupMemberDAOMock.removeStudentFromGroup(user.getId(), 3)).thenReturn(true);

        controller.handleDeleteStudent();

        verify(groupMemberDAOMock).removeStudentFromGroup(user.getId(), 3);
    }

    @Test
    void testHandleDeleteStudentNoSelectionShowsAlert() throws Exception {
        controller.getStudentsTable().getSelectionModel().clearSelection();

        AlertFactory alertFactoryMock = mock(AlertFactory.class);
        Alert alertMock = mock(Alert.class);
        when(alertFactoryMock.createAlert(any(), anyString(), anyString())).thenReturn(alertMock);

        controller.setAlertFactory(alertFactoryMock);

        CountDownLatch latch = new CountDownLatch(1);
        final Throwable[] thrown = {null};

        Platform.runLater(() -> {
            try {
                controller.handleDeleteStudent();
            } catch (Throwable t) {
                thrown[0] = t;
            } finally {
                latch.countDown();
            }
        });

        latch.await();

        if (thrown[0] != null) throw new RuntimeException(thrown[0]);


        verify(alertFactoryMock).createAlert(eq(Alert.AlertType.INFORMATION), anyString(), contains("Zaznacz studenta"));
        verify(alertMock).showAndWait();
    }

}
