package library_system.service;

import library_system.domain.Admin;
import library_system.Repository.LoanRepository;
import library_system.Repository.UserRepository;
import library_system.domain.User;

public class AdminService {

    private Admin loggedInAdmin = null;

    /**
     * Attempts to log in an admin using credentials.
     *
     * @param admin hardcoded admin object from AdminRepository
     * @param username input username
     * @param password input password
     * @return true if credentials match, false otherwise
     */
    public boolean login(Admin admin, String username, String password) {
        if (admin.getUsername().equals(username)
                && admin.getPassword().equals(password)) {

            loggedInAdmin = admin;
            return true;
        }
        return false;
    }

    /** Logs out the current admin. */
    public void logout() {
        loggedInAdmin = null;
    }

    /** @return true if an admin is currently logged in */
    public boolean isLoggedIn() {
        return loggedInAdmin != null;
    }

    /**
     * Attempts to unregister a user from the system.
     *
     * Conditions:
     * 1. User must exist
     * 2. User must have no unpaid fines
     * 3. User must have no active or overdue loans
     *
     * @param username username to remove
     * @return detailed status message (used directly by the CLI).
     */
    public String unregisterUser(String username) {

        User u = UserRepository.findUser(username);

        if (u == null)
            return "User does not exist.";

        if (u.getFineBalance() > 0)
            return "Cannot unregister user: Unpaid fines exist.";

        if (!LoanRepository.getUserLoans(username).isEmpty())
            return "Cannot unregister user: Active loans exist.";

        boolean removed = UserRepository.removeUser(username);
        if (removed)
            return "User unregistered successfully.";

        return "Could not remove user (unknown error).";
    }

}
