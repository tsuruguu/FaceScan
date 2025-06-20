package com.faceScan.dao;

import com.faceScan.model.Group;
import com.faceScan.model.User;
import com.faceScan.util.DatabaseManager;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GroupDAOTest {

    private GroupDAO groupDAO;
    private UserDAO userDAO;

    @BeforeAll
    void setup() throws Exception {
        DatabaseManager.useTestDatabase();
        DatabaseManager.init();

        groupDAO = new GroupDAO();
        userDAO = new UserDAO();

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("PRAGMA foreign_keys = OFF");
            stmt.executeUpdate("DELETE FROM group_members");
            stmt.executeUpdate("DELETE FROM groups");
            stmt.executeUpdate("DELETE FROM users");
            stmt.executeUpdate("PRAGMA foreign_keys = ON");

        }
    }

    @AfterAll
    void teardown() {
        DatabaseManager.useProductionDatabase();
        DatabaseManager.init();
    }

    private int createUser(String username, String role) {
        User user = new User(username, "pass", role, "Test", "User");
        assertTrue(userDAO.registerUser(user), "User should be registered");
        User fromDb = userDAO.getUserByUsername(username);
        assertNotNull(fromDb, "User should be retrievable after registration");
        return fromDb.getId();
    }

    private int createGroup(String name, int professorId) throws Exception {
        Group group = new Group(0, name, professorId);
        assertTrue(groupDAO.addGroup(group), "Group should be added successfully");

        List<Group> groups = GroupDAO.getGroupsByUserId(professorId);
        return groups.stream()
                .filter(g -> g.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Group not found"))
                .getId();
    }

    @Test
    void testAddGroupAndGetGroupsByUserId() throws Exception {
        int profId = createUser("prof1", "professor");
        int groupId = createGroup("Test Group", profId);

        List<Group> groups = GroupDAO.getGroupsByUserId(profId);
        assertFalse(groups.isEmpty(), "Group list should not be empty for professor");
        assertTrue(groups.stream().anyMatch(g -> g.getId() == groupId), "Created group should be present");
    }

    @Test
    void testGetStudentsInGroup() throws Exception {
        int studentId = createUser("student1", "student");
        int profId = createUser("prof2", "professor");
        int groupId = createGroup("Group for Students", profId);

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO group_members (group_id, student_id) VALUES (?, ?)")) {
            pstmt.setInt(1, groupId);
            pstmt.setInt(2, studentId);
            pstmt.executeUpdate();
        }

        List<User> students = groupDAO.getStudentsInGroup(groupId);
        assertEquals(1, students.size(), "There should be one student in the group");
        assertEquals(studentId, students.get(0).getId(), "Student ID should match");
    }

    @Test
    void testDeleteGroup() throws Exception {
        int profId = createUser("prof3", "professor");
        int groupId = createGroup("Group To Delete", profId);

        boolean deleted = groupDAO.deleteGroup(groupId);
        assertTrue(deleted, "Group should be deleted");

        List<Group> groups = GroupDAO.getGroupsByUserId(profId);
        assertTrue(groups.stream().noneMatch(g -> g.getId() == groupId), "Group should no longer exist");
    }
}
