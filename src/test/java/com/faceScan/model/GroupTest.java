package com.faceScan.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GroupTest {

    @Test
    void testConstructorWithId() {
        Group g = new Group(101, "Grupa A", 42);

        assertEquals(101, g.getId());
        assertEquals("Grupa A", g.getName());
        assertEquals(42, g.getProfessorId());
    }

    @Test
    void testConstructorWithoutId() {
        Group g = new Group("Grupa B", 7);

        assertEquals(0, g.getId()); // domyślnie 0
        assertEquals("Grupa B", g.getName());
        assertEquals(7, g.getProfessorId());
    }

    @Test
    void testToStringReturnsName() {
        Group g = new Group("Rocket Team", 999);
        assertEquals("Rocket Team", g.toString());
    }
}
