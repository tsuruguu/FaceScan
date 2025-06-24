package java.com.faceScan.model;

import com.faceScan.model.StudentPresence;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StudentPresenceTest {

    @Test
    void testConstructorAndGetters() {
        StudentPresence sp = new StudentPresence(1, "Ada", "Lovelace");

        assertEquals(1, sp.getId());
        assertEquals("Ada", sp.getFirstName());
        assertEquals("Lovelace", sp.getLastName());
        assertFalse(sp.isPresent());
        assertNull(sp.getAttendanceId());
    }

    @Test
    void testPresentFlag() {
        StudentPresence sp = new StudentPresence(2, "Alan", "Turing");

        assertFalse(sp.isPresent());
        sp.setPresent(true);
        assertTrue(sp.isPresent());
    }

    @Test
    void testAttendanceId() {
        StudentPresence sp = new StudentPresence(3, "Grace", "Hopper");

        assertNull(sp.getAttendanceId());
        sp.setAttendanceId(999);
        assertEquals(999, sp.getAttendanceId());
    }
}
