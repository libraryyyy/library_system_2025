package library_system.service;

import library_system.Repository.UserRepository;
import library_system.Repository.LoanRepository;
import library_system.domain.User;

public class UserService {

    private User loggedUser = null;

    /**
     * Registers a new user if the username is not already taken.
     */
    public boolean register(String username, String password,String email) {
        if (UserRepository.findUser(username) != null) {
            return false;
        }

        UserRepository.addUser(new User(username, password, email));
        return true;
    }


    /**
     * Unregisters a user ONLY if:
     * 1. User has NO active loans
     * 2. User has NO overdue loans
     * 3. User has NO unpaid fines
     */
    public String unregisterUser(User user) {

        // 1. Check active loans
        if (LoanRepository.hasActiveLoans(user)) {
            return "Cannot unregister: User has active loans.";
        }

        // 2. Check overdue loans
        if (LoanRepository.hasOverdueLoans(user)) {
            return "Cannot unregister: User has overdue loans.";
        }

        // 3. Check unpaid fines
        if (user.getFineBalance() > 0) {
            return "Cannot unregister: User has unpaid fines.";
        }




        // 4. Delete user
        UserRepository.deleteUser(user);

        return "User successfully unregistered.";
    }

    /**
     * Login method
     */
    public boolean login(String username, String password) {
        User user = UserRepository.findUser(username);
        if (user != null && user.getPassword().equals(password)) {
            loggedUser = user;
            return true;
        }
        return false;
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public void logout() {
        loggedUser = null;
    }
}
