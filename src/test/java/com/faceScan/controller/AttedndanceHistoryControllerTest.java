package com.faceScan.controller;

import com.faceScan.dao.AttendanceDAO;
import com.faceScan.model.Attendance;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class AttendanceHistoryControllerTest {

    private AttendanceHistoryController controller;
    private AttendanceDAO attendanceDAOMock;

    @Start
    private void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/faceScan/view/AttendanceHistoryView.fxml"));
        Scene scene = new Scene(loader.load());
        controller = loader.getController();

        attendanceDAOMock = mock(AttendanceDAO.class);
        setPrivateField(controller, "attendanceDAO", attendanceDAOMock);

        stage.setScene(scene);
        stage.show();
    }

    @BeforeEach
    void prepareMocks() {
        Attendance a1 = new Attendance(1, 101, 201, "2024-01-10", true);
        Attendance a2 = new Attendance(2, 101, 201, "2024-01-11", false);

        when(attendanceDAOMock.getAttendanceForStudentInGroup(101, 201))
                .thenReturn(List.of(a1, a2));
    }

    @Test
    void testLoadHistory_PopulatesTableCorrectly() {
        controller.loadHistory(101, 201);
        ObservableList<Attendance> list = controller.attendanceTable.getItems();

        assertEquals(2, list.size());
        assertEquals("2024-01-10", list.get(0).getDate());
        assertEquals(true, list.get(0).isPresent());
        assertEquals(false, list.get(1).isPresent());

        verify(attendanceDAOMock).getAttendanceForStudentInGroup(101, 201);
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
