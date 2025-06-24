package java.com.faceScan.model;

import com.faceScan.model.Attendance;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AttendanceTest {

    @Test
    void testConstructorWithId() {
        Attendance a = new Attendance(10, 5, 3, "2025-06-19", true);

        assertEquals(10, a.getId());
        assertEquals(5, a.getStudentId());
        assertEquals(3, a.getGroupId());
        assertEquals("2025-06-19", a.getDate());
        assertTrue(a.isPresent());
        assertNull(a.getStudentName()); // nie ustawione
    }

    @Test
    void testConstructorWithoutId() {
        Attendance a = new Attendance(7, 4, "2024-01-01", false);

        assertEquals(0, a.getId()); // domyślnie 0
        assertEquals(7, a.getStudentId());
        assertEquals(4, a.getGroupId());
        assertEquals("2024-01-01", a.getDate());
        assertFalse(a.isPresent());
    }

    @Test
    void testSettersAndGetters() {
        Attendance a = new Attendance(1, 1, "2022-01-01", false);

        a.setId(20);
        a.setStudentId(2);
        a.setGroupId(3);
        a.setDate("2023-03-03");
        a.setPresent(true);
        a.setStudentName("Ada Lovelace");

        assertEquals(20, a.getId());
        assertEquals(2, a.getStudentId());
        assertEquals(3, a.getGroupId());
        assertEquals("2023-03-03", a.getDate());
        assertTrue(a.isPresent());
        assertEquals("Ada Lovelace", a.getStudentName());
    }

    @Test
    void testToStringFormat() {
        Attendance a = new Attendance(12, 8, "2024-12-24", true);
        String result = a.toString();

        assertTrue(result.contains("studentId=12"));
        assertTrue(result.contains("groupId=8"));
        assertTrue(result.contains("date='2024-12-24'"));
        assertTrue(result.contains("present=true"));
    }
}
