package library_system.service;

import library_system.domain.Admin;
import library_system.Repository.LoanRepository;
import library_system.Repository.UserRepository;
import library_system.domain.User;

/**
 * Service layer handling administrator authentication and user management.
 * Supports login, logout, and unregistering users under specific constraints.
 */
public class AdminService {

    /** Currently logged-in admin instance. */
    private Admin loggedInAdmin = null;

    /**
     * Attempts to authenticate the admin with provided credentials.
     *
     * @param admin     admin object from the repository
     * @param username  input username
     * @param password  input password
     * @return true if authentication succeeds, false otherwise
     */
    public boolean login(Admin admin, String username, String password) {
        if (admin.getUsername().equalsIgnoreCase(username)
                && admin.getPassword().equals(password)) {
            loggedInAdmin = admin;
            return true;
        }
        return false;
    }

    /** Logs out the currently logged-in admin. */
    public void logout() {
        loggedInAdmin = null;
    }

    /**
     * @return true if an admin is currently logged in
     */
    public boolean isLoggedIn() {
        return loggedInAdmin != null;
    }

    /**
     * Attempts to unregister a user.
     *
     * Conditions:
     * <ul>
     *     <li>User must exist</li>
     *     <li>User must have no unpaid fines</li>
     *     <li>User must have no active or overdue loans</li>
     * </ul>
     *
     * @param username username to unregister
     * @return status message for CLI
     */
    public String unregisterUser(String username) {

        User user = UserRepository.findUser(username);

        if (user == null)
            return "User does not exist.";

        if (user.getFineBalance() > 0)
            return "Cannot unregister user: Unpaid fines exist.";

        boolean hasActiveLoans = LoanRepository.getUserLoans(username)
                .stream()
                .anyMatch(l -> !l.isReturned());

        if (hasActiveLoans)
            return "Cannot unregister user: Active loans exist.";

        boolean removed = UserRepository.removeUser(username);

        return removed
                ? "User unregistered successfully."
                : "Could not remove user (unknown error).";
    }
}
