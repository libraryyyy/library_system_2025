package library_system.repository;

import library_system.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {

    @BeforeEach
    void setup() {
        // In-memory only
        UserRepository.clear();
    }

    @Test
    void testAddAndFindUser() {
        User u = new User("sara", "1234", "sara@mail.com");
        UserRepository.addUser(u);

        User found = UserRepository.findUser("sara");

        assertNotNull(found);
        assertEquals("sara", found.getUsername());
        assertEquals("sara@mail.com", found.getEmail());
    }

    @Test
    void testFindUserByEmail() {
        UserRepository.addUser(new User("sara", "1234", "test@mail.com"));

        User found = UserRepository.findUserByEmail("test@mail.com");

        assertNotNull(found);
        assertEquals("sara", found.getUsername());
    }

    @Test
    void testRemoveUser() {
        UserRepository.addUser(new User("ali", "1234", "a@mail.com"));

        boolean removed = UserRepository.removeUser("ali");

        assertTrue(removed);
        assertNull(UserRepository.findUser("ali"));
    }

    @Test
    void testUpdateUser() {
        User u = new User("sara", "1234", "sara@mail.com");
        UserRepository.addUser(u);

        u.setFineBalance(50);
        UserRepository.updateUser(u);

        assertEquals(50, UserRepository.findUser("sara").getFineBalance());
    }
}
