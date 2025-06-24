package java.com.faceScan.model;

import com.faceScan.model.Group;
import com.faceScan.model.Student;
import com.faceScan.model.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StudentTest {

    @Test
    void testConstructorAndGetters() {
        Student s = new Student(101, "Ada", "Lovelace", "/avatars/ada.png");

        assertEquals(101, s.getId());
        assertEquals("Ada", s.getFirstName());
        assertEquals("Lovelace", s.getLastName());
        assertEquals("/avatars/ada.png", s.getPhotoPath());
    }

    @Test
    void testToStringReturnsFullName() {
        Student s = new Student(42, "Albert", "Kosmos", null);
        assertEquals("Albert Kosmos", s.toString());
    }

    @Test
    void testFromUser() {
        User user = new User(5, "albert", "student", "Albert", "Kosmos", "/photos/a.png");
        Student s = Student.fromUser(user);

        assertEquals(5, s.getId());
        assertEquals("Albert", s.getFirstName());
        assertEquals("Kosmos", s.getLastName());
        assertEquals("/photos/a.png", s.getPhotoPath());
    }

    @Test
    void testAddGroup() {
        Student s = new Student(11, "Dobi", "Frączek", null);
        Group g1 = new Group("Grupa A", 1);
        Group g2 = new Group("Grupa B", 1);

        s.addGroup(g1);
        s.addGroup(g2);

        assertEquals(2, s.getGroups().size());
        assertTrue(s.getGroups().contains(g1));
        assertTrue(s.getGroups().contains(g2));
    }
}
