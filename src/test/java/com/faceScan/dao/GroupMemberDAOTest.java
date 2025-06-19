package com.faceScan.dao;

import com.faceScan.model.Group;
import com.faceScan.model.Student;
import com.faceScan.model.User;
import com.faceScan.util.DatabaseManager;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GroupMemberDAOTest {

    private GroupMemberDAO groupMemberDAO;
    private UserDAO userDAO;

    @BeforeAll
    void setup() throws Exception {
        DatabaseManager.init();
        groupMemberDAO = new GroupMemberDAO();
        userDAO = new UserDAO();

        try (Connection conn = DatabaseManager.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM group_members");
            stmt.executeUpdate("DELETE FROM groups");
            stmt.executeUpdate("DELETE FROM users");
        }
    }

    private int createUser(String username, String role) {
        User user = new User(username, "pass", role, "Test", "User");
        boolean registered = userDAO.registerUser(user);
        assertTrue(registered, "User should be registered");
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
            assertEquals(1, affected, "Group should be created");
            var rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        throw new RuntimeException("Failed to create group");
    }

    @Test
    void testAddAndRemoveStudentFromGroup() throws Exception {
        int studentId = createUser("student1", "student");
        int professorId = createUser("prof1", "professor");
        int groupId = createGroup("Group A", professorId);

        boolean added = groupMemberDAO.addStudentToGroup(studentId, groupId);
        assertTrue(added, "Student should be added to group");

        List<Group> groups = groupMemberDAO.getGroupsForStudent(studentId);
        assertEquals(1, groups.size());
        assertEquals(groupId, groups.getFirst().getId());

        List<Student> students = groupMemberDAO.getStudentsInGroup(groupId);
        assertEquals(1, students.size());
        assertEquals(studentId, students.getFirst().getId());

        boolean removed = groupMemberDAO.removeStudentFromGroup(studentId, groupId);
        assertTrue(removed, "Student should be removed from group");

        students = groupMemberDAO.getStudentsInGroup(groupId);
        assertTrue(students.isEmpty());
    }
}
