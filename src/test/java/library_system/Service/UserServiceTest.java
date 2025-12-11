package library_system.service;

import library_system.repository.*;
import library_system.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;
    private User user;
    private Book book;

    @BeforeEach
    void setup() {
        UserRepository.clear();
        LoanRepository.clear();
        userService = new UserService();

        // Added test email address
        user = new User("testUser", "pass", "testUser@example.com");
        UserRepository.addUser(user);

        book = new Book("Java Basics", "James", "1234");
    }

    @Test
    void testUnregisterUserWithActiveLoan() {
        LoanRepository.addLoan(new Loan(user, book));
        String result = userService.unregisterUser(user);
        assertEquals("Cannot unregister: User has active loans.", result);
    }

    @Test
    void testUnregisterUserWithOverdueLoan() {
        Loan loan = new Loan(user, book);
        loan.setDueDate(LocalDate.now().minusDays(1));
        LoanRepository.addLoan(loan);

        String result = userService.unregisterUser(user);
        assertEquals("Cannot unregister: User has overdue loans.", result);
    }

    @Test
    void testUnregisterUserWithUnpaidFines() {
        user.setFineBalance(50);
        String result = userService.unregisterUser(user);
        assertEquals("Cannot unregister: User has unpaid fines.", result);
    }

    @Test
    void testUnregisterUserSuccess() {
        user.setFineBalance(0);
        String result = userService.unregisterUser(user);
        assertEquals("User successfully unregistered.", result);
        assertNull(UserRepository.findUser(user.getUsername()));
    }

    @Test
    void testLoginLogout() {
        assertTrue(userService.login("testUser", "pass"));
        assertEquals(user, userService.getLoggedUser());
        userService.logout();
        assertNull(userService.getLoggedUser());
    }

    @Test
    void testUserRegistrationSuccess() {
        boolean success = userService.register("sana", "1234", "sana@example.com");
        assertTrue(success);

        User stored = UserRepository.findUser("sana");
        assertNotNull(stored);
        assertEquals("sana", stored.getUsername());
        assertEquals("1234", stored.getPassword());
        assertEquals("sana@example.com", stored.getEmail());
    }

    @Test
    void testUserRegistrationFailsIfUsernameExists() {
        userService.register("sana", "1234", "sana@example.com");

        boolean success = userService.register("sana", "anotherPass", "sana2@example.com");
        assertFalse(success);
    }

    @Test
    void testLoginSuccess() {
        userService.register("sana", "1234", "sana@example.com");
        boolean login = userService.login("sana", "1234");

        assertTrue(login);
        assertNotNull(userService.getLoggedUser());
    }

    @Test
    void testLoginFailsWrongPassword() {
        userService.register("sana", "1234", "sana@example.com");
        boolean login = userService.login("sana", "wrong");

        assertFalse(login);
        assertNull(userService.getLoggedUser());
    }

    @Test
    void testLoginFailsNonExistingUser() {
        boolean login = userService.login("unknown", "1234");
        assertFalse(login);
        assertNull(userService.getLoggedUser());
    }

    @Test
    void testLogout() {
        userService.register("sana", "1234", "sana@example.com");
        userService.login("sana", "1234");
        userService.logout();
        assertNull(userService.getLoggedUser());
    }
}
