package library_system.service;

import library_system.domain.Admin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminServiceTest {

    private Admin admin;
    private AdminService adminService;

    @BeforeEach
    void setup() {
        admin = new Admin("admin", "1234");
        adminService = new AdminService();
    }

    @Test
    void testLoginSuccess() {
        assertTrue(adminService.login(admin, "admin", "1234"));
        assertTrue(adminService.isLoggedIn());
    }

    @Test
    void testLoginFailure() {
        assertFalse(adminService.login(admin, "admin", "wrong"));
        assertFalse(adminService.isLoggedIn());
    }

    @Test
    void testLogout() {
        adminService.login(admin, "admin", "1234");
        adminService.logout();
        assertFalse(adminService.isLoggedIn());
    }
}
