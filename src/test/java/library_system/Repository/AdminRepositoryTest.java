package library_system.repository;

import library_system.domain.Admin;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminRepositoryTest {

    @Test
    void testGetAdminReturnsNonNull() {
        Admin admin = AdminRepository.getAdmin();
        assertNotNull(admin, "Admin should not be null");
    }

    @Test
    void testGetAdminHasCorrectUsername() {
        Admin admin = AdminRepository.getAdmin();
        assertEquals("admin", admin.getUsername(), "Admin username should be 'admin'");
    }

    @Test
    void testGetAdminHasCorrectPassword() {
        Admin admin = AdminRepository.getAdmin();
        assertEquals("1234", admin.getPassword(), "Admin password should be '1234'");
    }

    @Test
    void testGetAdminAlwaysReturnsSameInstance() {
        Admin admin1 = AdminRepository.getAdmin();
        Admin admin2 = AdminRepository.getAdmin();

        assertSame(admin1, admin2, "AdminRepository should always return the same Admin instance");
    }
}
