package com.faceScan.controller;

import com.faceScan.dao.AttendanceDAO;
import com.faceScan.dao.GroupMemberDAO;
import com.faceScan.model.Attendance;
import com.faceScan.model.Student;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class AttendanceControllerTest {

    private AttendanceController controller;
    private AttendanceDAO attendanceDAOMock;
    private GroupMemberDAO groupMemberDAOMock;

    @Start
    private void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/faceScan/view/AttendanceView.fxml"));
        Scene scene = new Scene(loader.load());
        controller = loader.getController();

         attendanceDAOMock = mock(AttendanceDAO.class);
        groupMemberDAOMock = mock(GroupMemberDAO.class);
        setPrivateField(controller, "attendanceDAO", attendanceDAOMock);
        setPrivateField(controller, "groupMemberDAO", groupMemberDAOMock);

        stage.setScene(scene);
        stage.show();
    }

    @BeforeEach
    void setupTestData() {
        Student testStudent = new Student(1, "Alicja", "Borek", null);

        when(groupMemberDAOMock.getStudentsInGroup(123)).thenReturn(List.of(testStudent));
        when(attendanceDAOMock.getAttendanceForStudentInGroup(1, 123)).thenReturn(Collections.emptyList());

        controller.setGroup(123, "TestGroup");
    }

    @Test
    void testLoadAttendance_AddsOneEntry() {
        ObservableList<Attendance> list = controller.getAttendanceTable().getItems();
        assertEquals(1, list.size());
        assertEquals("Alicja Borek", list.get(0).getStudentName());
    }

    @Test
    void testHandleSaveAttendance_CallsAddAttendance() {
        ObservableList<Attendance> list = controller.getAttendanceTable().getItems();
        Attendance newAttendance = list.get(0);

        when(attendanceDAOMock.addAttendance(newAttendance)).thenReturn(true);

        controller.handleSaveAttendance();

        verify(attendanceDAOMock).addAttendance(newAttendance);
    }

    private void setPrivateField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
