package com.faceScan.dao;

import com.faceScan.model.User;
import com.faceScan.util.DatabaseManager;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserDAOTest {

    private UserDAO userDAO;

    @BeforeAll
    void setupDatabase() {
        DatabaseManager.useTestDatabase();

        try {
            Files.deleteIfExists(Paths.get("test.db"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        DatabaseManager.init();
        userDAO = new UserDAO();
    }

    @AfterAll
    void resetDatabase() {
        DatabaseManager.useProductionDatabase();
        DatabaseManager.init();
    }

    @BeforeEach
    void clearUsersTable() throws Exception {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("PRAGMA foreign_keys = OFF");

            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT NOT NULL UNIQUE, " +
                    "password TEXT NOT NULL, " +
                    "role TEXT NOT NULL, " +
                    "first_name TEXT, " +
                    "last_name TEXT, " +
                    "photo_path TEXT)");

            stmt.executeUpdate("DELETE FROM users");

            stmt.executeUpdate("PRAGMA foreign_keys = ON");
        }
    }



    @Test
    void testRegisterUserAndLogin() {
        User user = new User("ala", "pass123", "student", "Ala", "Nowak", "/img/ala.jpg");
        boolean success = userDAO.registerUser(user);
        assertTrue(success, "User should be registered successfully");

        User logged = userDAO.loginUser("ala", "pass123");
        assertNotNull(logged, "User should be able to log in");
        assertEquals("Ala", logged.getFirstName());
        assertEquals("student", logged.getRole());
    }

    @Test
    void testGetUserByUsername() {
        userDAO.registerUser(new User("ola", "ola123", "student", "Ola", "Kowalska", null));
        User retrieved = userDAO.getUserByUsername("ola");
        assertNotNull(retrieved);
        assertEquals("Kowalska", retrieved.getLastName());
    }

    @Test
    void testGetUserById() {
        userDAO.registerUser(new User("ida", "ida321", "professor", "Ida", "Profesor", null));
        User retrieved = userDAO.getUserByUsername("ida");
        assertNotNull(retrieved);

        User byId = userDAO.getUserById(retrieved.getId());
        assertNotNull(byId);
        assertEquals("professor", byId.getRole());
    }

    @Test
    void testGetAllStudents() {
        userDAO.registerUser(new User("bob", "bob1", "student", "Bob", "A", null));
        userDAO.registerUser(new User("eva", "eva1", "student", "Eva", "B", null));
        userDAO.registerUser(new User("drx", "drx1", "professor", "Dr", "X", null));

        List<User> students = userDAO.getAllStudents();
        assertEquals(2, students.size());
        assertTrue(students.stream().anyMatch(u -> u.getUsername().equals("bob")));
        assertTrue(students.stream().noneMatch(u -> u.getUsername().equals("drx")));
    }
}
