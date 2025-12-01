package library_system.service;

import library_system.Repository.UserRepository;
import library_system.Repository.LoanRepository;
import library_system.domain.User;

public class UserService {

    private User loggedUser = null;

    /**
     * Registers a new user IF the username is not taken.
     *
     * After adding the new user → saves to JSON file.
     *
     * @return true if user added, false otherwise.
     */
    public boolean register(String username, String password, String email) {

        // 1. Username must be unique
        if (UserRepository.findUser(username) != null) {
            return false;
        }

        // 2. Email should not be empty
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        // 3. Create and store user
        User newUser = new User(username, password, email);
        UserRepository.addUser(newUser);

        // 4. Save to file
        UserRepository.saveToFile();

        return true;
    }


    /**
     * Unregisters a user ONLY if:
     * 1. User has NO active loans
     * 2. User has NO overdue loans
     * 3. User has NO unpaid fines
     *  * After removing → saves to JSON file
     *  * @return message describing success/failure reason.
     */
    public String unregisterUser(User user) {

        // 1. Check overdue loans
        if (LoanRepository.hasOverdueLoans(user)) {
            return "Cannot unregister: User has overdue loans.";
        }
        // 2. Check active loans
        if (LoanRepository.hasActiveLoans(user)) {
            return "Cannot unregister: User has active loans.";
        }



        // 3. Check unpaid fines
        if (user.getFineBalance() > 0) {
            return "Cannot unregister: User has unpaid fines.";
        }




        // 4. Delete user
        UserRepository.deleteUser(user);
        // 5. Save the updated user list in JSON file
        UserRepository.saveToFile();
        return "User successfully unregistered.";
    }

    /**
     * Attempts to log in a user.
     *
     * @return true if login successful, false otherwise.
     */
    public boolean login(String username, String password) {
        User user = UserRepository.findUser(username);
        if (user != null && user.getPassword().equals(password)) {
            loggedUser = user;
            return true;
        }
        return false;
    }
    /** @return the currently logged-in user */
    public User getLoggedUser() {
        return loggedUser;
    }

    public void logout() {
        loggedUser = null;
    }
}
