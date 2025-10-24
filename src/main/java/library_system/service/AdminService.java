package library_system.service;

import library_system.domain.Admin;

public class AdminService {
    private Admin loggedInAdmin = null;

    public boolean login(Admin admin, String username, String password) {
        if (admin.getUsername().equals(username) && admin.getPassword().equals(password)) {
            loggedInAdmin = admin;
            return true;
        }
        return false;
    }

    public void logout() {
        loggedInAdmin = null;
    }

    public boolean isLoggedIn() {
        return loggedInAdmin != null;
    }
}
