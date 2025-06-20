package com.faceScan.controller;

import com.faceScan.dao.GroupDAO;
import com.faceScan.dao.GroupMemberDAO;
import com.faceScan.model.Group;
import com.faceScan.model.User;
import com.faceScan.session.SessionManager;
import com.faceScan.util.AlertFactory;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DashboardControllerTest {

    private DashboardController controller;
    private GroupDAO groupDAOMock;
    private GroupMemberDAO groupMemberDAOMock;
    private AlertFactory alertFactoryMock;

    @BeforeAll
    static void initToolkit() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);
        latch.await();
    }

    @BeforeEach
    void setUp() throws Exception {
        controller = new DashboardController();

        groupDAOMock = mock(GroupDAO.class);
        System.out.println("groupDAOMock is mock: " + Mockito.mockingDetails(groupDAOMock).isMock()); // ✅

        groupMemberDAOMock = mock(GroupMemberDAO.class);
        alertFactoryMock = mock(AlertFactory.class);

        Alert fakeAlert = mock(Alert.class);
        when(alertFactoryMock.createAlert(any(), any(), any())).thenReturn(fakeAlert);

        controller.setGroupDAO(groupDAOMock);
        controller.setGroupMemberDAO(groupMemberDAOMock);
        controller.setAlertFactory(alertFactoryMock);

        controller.setGroupListView(new ListView<>());
        controller.setGroupNameField(new TextField());
        controller.setAddGroupButton(new Button());
        controller.setDeleteGroupButton(new Button());
        controller.setRoleLabel(new Label());
    }

    @Test
    void testInitializeForStudent_disablesControlsAndLoadsGroups() {
        User student = new User(2, "stud", "student", "Jan", "Nowak", null);
        SessionManager.setCurrentUser(student);

        when(groupMemberDAOMock.getGroupsForStudent(2)).thenReturn(List.of(new Group("SG", 4)));

        controller.initialize();

        assertTrue(controller.getGroupNameField().isDisabled());
        ObservableList<Group> items = controller.getGroupListView().getItems();
        assertEquals(1, items.size());
        assertEquals("SG", items.get(0).getName());
    }

    @Test
    void testOnAddGroup_withValidName_addsGroup() {
        User prof = new User(1, "prof", "professor", "Anna", "K.", null);
        SessionManager.setCurrentUser(prof);

        controller.getGroupNameField().setText("Nowa grupa");
        when(groupDAOMock.addGroup(any())).thenReturn(true);

        controller.onAddGroup();

        verify(groupDAOMock).addGroup(any());
        assertEquals("", controller.getGroupNameField().getText());
    }

    @Test
    void testOnAddGroup_withEmptyName_doesNotAdd() throws Exception {
        SessionManager.setCurrentUser(new User(1, "prof", "professor", "Anna", "K.", null));
        controller.getGroupNameField().setText("   ");

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.onAddGroup();
            latch.countDown();
        });
        latch.await();

        verify(groupDAOMock, never()).addGroup(any());
    }


    @Test
    void testOnDeleteGroup_withSelection_deletesGroup() {
        Group g = new Group(3, "ToDelete", 1);
        controller.getGroupListView().getItems().add(g);
        controller.getGroupListView().getSelectionModel().select(g);

        SessionManager.setCurrentUser(new User(1, "prof", "professor", "Anna", "K.", null));
        when(groupDAOMock.deleteGroup(3)).thenReturn(true);

        controller.onDeleteGroup();

        verify(groupDAOMock).deleteGroup(3);
    }

    @Test
    void testOnDeleteGroup_withoutSelection_doesNotDelete() throws Exception {
        SessionManager.setCurrentUser(new User(1, "prof", "professor", "Anna", "K.", null));
        controller.getGroupListView().getSelectionModel().clearSelection();

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.onDeleteGroup();
            latch.countDown();
        });
        latch.await();

        verify(groupDAOMock, never()).deleteGroup(anyInt());
    }

}
