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
        UserRepository.clear();
        userService = new UserService();
    }

    @Test
    void testRegisterSuccess() {
        assertTrue(userService.register("sana", "1234"));
        assertNotNull(UserRepository.findUser("sana"));
    }

    @Test
    void testRegisterFailure_DuplicateUser() {
        userService.register("sana", "1234");
        assertFalse(userService.register("sana", "9999"));  // duplicate
    }

    @Test
    void testLoginSuccess() {
        userService.register("sana", "1234");
        assertTrue(userService.login("sana", "1234"));
        assertEquals("sana", userService.getLoggedUser().getUsername());
    }

    @Test
    void testLoginFailure() {
        userService.register("sana", "1234");
        assertFalse(userService.login("sana", "wrongpass"));
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
