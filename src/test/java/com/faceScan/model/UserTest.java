package com.faceScan.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testConstructorWithId() {
        User user = new User(1, "dobi", "student", "Dobrawa", "Rumszewicz", "/face_data/Dobrawa/dobrawa_1.jpg");

        assertEquals(1, user.getId());
        assertEquals("dobi", user.getUsername());
        assertEquals("student", user.getRole());
        assertEquals("Dobrawa", user.getFirstName());
        assertEquals("Rumszewicz", user.getLastName());
        assertEquals("/face_data/Dobrawa/dobrawa_1.jpg", user.getPhotoPath());
        assertNull(user.getPassword());
    }

    @Test
    void testRegistrationConstructor() {
        User user = new User("albert", "tajnehaslo", "student", "Albert", "Kosmos");

        assertEquals("albert", user.getUsername());
        assertEquals("tajnehaslo", user.getPassword());
        assertEquals("student", user.getRole());
        assertEquals("Albert", user.getFirstName());
        assertEquals("Kosmos", user.getLastName());
        assertEquals(0, user.getId());
        assertNull(user.getPhotoPath());
    }

    @Test
    void testSetters() {
        User user = new User("albert", "tajne", "student", "Albert", "Kosmos");
        user.setId(42);
        user.setPassword("nowehaslo");
        user.setPhotoPath("/avatars/albert.png");

        assertEquals(42, user.getId());
        assertEquals("nowehaslo", user.getPassword());
        assertEquals("/avatars/albert.png", user.getPhotoPath());
    }

    @Test
    void testGetFullName() {
        User user = new User("a", "b", "c", "Ada", "Lovelace");
        assertEquals("Ada Lovelace", user.getFullName());
    }

    @Test
    void testIsProfessorTrue() {
        assertTrue(new User("x", "x", "professor", "X", "Y").isProfessor());
        assertTrue(new User("x", "x", "Professor", "X", "Y").isProfessor());
        assertTrue(new User("x", "x", "PROFESSOR", "X", "Y").isProfessor());
    }

    @Test
    void testIsProfessorFalse() {
        assertFalse(new User("x", "x", "student", "X", "Y").isProfessor());
        assertFalse(new User("x", "x", "admin", "X", "Y").isProfessor());
        User user = new User("x", "x", null, "X", "Y");
        assertFalse(user.isProfessor());
    }

}
