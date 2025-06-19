package com.faceScan.dao;

import com.faceScan.model.Attendance;
import com.faceScan.model.User;
import com.faceScan.util.DatabaseManager;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AttendanceDAOTest {

    private AttendanceDAO attendanceDAO;
    private UserDAO userDAO;

    @BeforeAll
    void setup() throws Exception {
        DatabaseManager.init();
        attendanceDAO = new AttendanceDAO();
        userDAO = new UserDAO();

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM attendance");
            stmt.executeUpdate("DELETE FROM users");
            stmt.executeUpdate("DELETE FROM groups");
            stmt.executeUpdate("DELETE FROM group_members");
        }
    }

    private int createUser(String username, String role) {
        User user = new User(username, "pass", role, "Test", "User");
        assertTrue(userDAO.registerUser(user));
        User fromDb = userDAO.getUserByUsername(username);
        assertNotNull(fromDb);
        return fromDb.getId();
    }

    private int createGroup(String name, int professorId) throws Exception {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO groups(name, professor_id) VALUES (?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, professorId);
            int affected = pstmt.executeUpdate();
            assertEquals(1, affected);
            var rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        throw new RuntimeException("Failed to create group");
    }

    @Test
    void testAddAndRetrieveAttendance() throws Exception {
        int studentId = createUser("student_att", "student");
        int professorId = createUser("prof_att", "professor");
        int groupId = createGroup("Attendance Group", professorId);

        Attendance attendance = new Attendance(0, studentId, groupId, "2025-06-19", true);
        boolean added = attendanceDAO.addAttendance(attendance);
        assertTrue(added, "Attendance should be added");

        List<Attendance> studentAttendances = attendanceDAO.getAttendanceByStudent(studentId);
        assertFalse(studentAttendances.isEmpty());

        List<Attendance> groupAttendances = attendanceDAO.getAttendanceForStudentInGroup(studentId, groupId);
        assertFalse(groupAttendances.isEmpty());

        Attendance firstRecord = groupAttendances.getFirst();
        assertEquals("2025-06-19", firstRecord.getDate());
        assertTrue(firstRecord.isPresent());
    }

    @Test
    void testUpdateAttendance() throws Exception {
        int studentId = createUser("student_upd", "student");
        int professorId = createUser("prof_upd", "professor");
        int groupId = createGroup("Update Group", professorId);

        Attendance attendance = new Attendance(0, studentId, groupId, "2025-06-19", true);
        assertTrue(attendanceDAO.addAttendance(attendance));

        List<Attendance> attendances = attendanceDAO.getAttendanceForStudentInGroup(studentId, groupId);
        assertFalse(attendances.isEmpty());

        Attendance toUpdate = attendances.getFirst();
        toUpdate.setPresent(false);

        boolean updated = attendanceDAO.updateAttendance(toUpdate);
        assertTrue(updated);

        List<Attendance> updatedAttendances = attendanceDAO.getAttendanceForStudentInGroup(studentId, groupId);
        assertFalse(updatedAttendances.isEmpty());
        assertFalse(updatedAttendances.getFirst().isPresent());
    }
}
