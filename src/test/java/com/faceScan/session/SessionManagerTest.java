package com.faceScan.session;

import com.faceScan.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {

    @AfterEach
    void clearSessionAfterTest() {
        SessionManager.logout();
    }

    @Test
    void testLoginSetsCurrentUser() {
        User user = new User("albert", "tajnehaslo", "student", "Albert", "Kosmos");

        SessionManager.login(user);

        assertTrue(SessionManager.isLoggedIn());
        assertEquals(user, SessionManager.getCurrentUser());
    }

    @Test
    void testLogoutClearsSession() {
        User user = new User("dobi", "sekret", "admin", "Dobi", "Rumszewicz");

        SessionManager.login(user);
        assertTrue(SessionManager.isLoggedIn());

        SessionManager.logout();

        assertFalse(SessionManager.isLoggedIn());
        assertNull(SessionManager.getCurrentUser());
    }

    @Test
    void testGetCurrentUserReturnsNullInitially() {
        assertFalse(SessionManager.isLoggedIn());
        assertNull(SessionManager.getCurrentUser());
    }
}
