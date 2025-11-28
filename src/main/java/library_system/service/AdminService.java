package library_system.service;

import library_system.domain.Admin;
import library_system.Repository.LoanRepository;
import library_system.Repository.UserRepository;
import library_system.domain.User;

/**
 * Service that handles admin authentication and session state.
 */
public class AdminService {

    /** Currently logged-in admin, or null if no admin is logged in. */
    private Admin loggedInAdmin = null;

    /**
     * Attempts to log in an admin with the given credentials.
     *
     * @param admin    admin instance to validate against.
     * @param username entered username.
     * @param password entered password.
     * @return true if credentials are valid; false otherwise.
     */
    public boolean login(Admin admin, String username, String password) {
        if (admin.getUsername().equals(username) && admin.getPassword().equals(password)) {
            loggedInAdmin = admin;
            return true;
        }
        return false;
    }

    /**
     * Logs out the current admin.
     */
    public void logout() {
        loggedInAdmin = null;
    }

    /**
     * Checks whether an admin is currently logged in.
     *
     * @return true if an admin is logged in; false otherwise.
     */
    public boolean isLoggedIn() {
        return loggedInAdmin != null;
    }

    public String unregisterUser(String username) {

        User u = UserRepository.findUser(username);

        if (u == null)
            return "❌ User does not exist.";

        if (u.getFineBalance() > 0)
            return "❌ Cannot unregister user: Unpaid fines exist.";

        if (!LoanRepository.getUserLoans(username).isEmpty())
            return "❌ Cannot unregister user: Active loans exist.";

        boolean removed = UserRepository.removeUser(username);
        if (removed) return "✔ User unregistered successfully.";

        return "❌ Could not remove user (unknown error).";
    }
}
