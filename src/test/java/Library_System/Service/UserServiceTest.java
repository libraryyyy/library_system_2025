package Library_System.Service;

import library_system.Repository.UserRepository;
import library_system.domain.User;
import library_system.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setup() {
        userService = new UserService();
        UserRepository.clear();

    }

    @Test
    void testUserRegistrationSuccess() {
        boolean success = userService.register("sana", "1234");
        assertTrue(success, "User registration should succeed for a new username.");

        User stored = UserRepository.findUser("sana");
        assertNotNull(stored, "User must exist in repository after registration.");
        assertEquals("sana", stored.getUsername());
        assertEquals("1234", stored.getPassword());
    }

    @Test
    void testUserRegistrationFailsIfUsernameExists() {
        userService.register("sana", "1234");

        boolean success = userService.register("sana", "anotherPass");
        assertFalse(success, "Registration should fail if username already exists.");
    }

    // ======================================
    // Login Tests
    // ======================================

    @Test
    void testLoginSuccess() {
        userService.register("sana", "1234");
        boolean login = userService.login("sana", "1234");

        assertTrue(login, "Login should succeed with correct credentials.");
        assertNotNull(userService.getLoggedUser(), "getLoggedUser() must not return null after login.");
    }

    @Test
    void testLoginFailsWrongPassword() {
        userService.register("sana", "1234");
        boolean login = userService.login("sana", "wrong");

        assertFalse(login, "Login should fail with incorrect password.");
        assertNull(userService.getLoggedUser(), "No user should be logged in after failed login.");
    }

    @Test
    void testLoginFailsNonExistingUser() {
        boolean login = userService.login("unknown", "1234");
        assertFalse(login, "Login should fail for non-existing user.");
        assertNull(userService.getLoggedUser());
    }

    @Test
    void testLogout() {
        userService.register("sana", "1234");
        userService.login("sana", "1234");
        userService.logout();
        assertNull(userService.getLoggedUser());
    }
}
