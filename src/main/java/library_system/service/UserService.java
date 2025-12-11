package library_system.service;

import library_system.repository.UserRepository;
import library_system.repository.LoanRepository;
import library_system.domain.User;
import library_system.domain.*;

import java.time.LocalDate;

/**
 * Service handling user registration, authentication, and removal.
 * Integrates with the repositories for data persistence.
 */
public class UserService {

    /** Currently logged-in user. */
    private User loggedUser = null;

    /**
     * Registers a new user if the username and email are valid and unique.
     *
     * @param username desired username
     * @param password user password
     * @param email    user email
     * @return true if successfully registered, false otherwise
     */
    public boolean register(String username, String password, String email) {

        if (UserRepository.findUser(username) != null)
            return false;

        if (email == null || email.trim().isEmpty())
            return false;

        if (!isValidEmail(email))
            return false;

        if (UserRepository.findUserByEmail(email) != null)
            return false;

        User newUser = new User(username, password, email);
        UserRepository.addUser(newUser);

        return true;
    }

    /**
     * Validates emails using a simple structural check.
     *
     * @param email email string
     * @return true if valid
     */
    private boolean isValidEmail(String email) {
        String lower = email.toLowerCase();
        return lower.contains("@") && lower.contains(".");
    }

    /**
     * Unregisters a user if:
     * <ul>
     *     <li>No overdue loans</li>
     *     <li>No active loans</li>
     *     <li>No unpaid fines</li>
     * </ul>
     *
     * @param user user to remove
     * @return status message explaining success or reason for failure
     */
    public String unregisterUser(User user) {

        if (LoanRepository.hasOverdueLoans(user))
            return "Cannot unregister: User has overdue loans.";

        if (LoanRepository.hasActiveLoans(user))
            return "Cannot unregister: User has active loans.";

        if (user.getFineBalance() > 0)
            return "Cannot unregister: User has unpaid fines.";

        UserRepository.deleteUser(user);
        UserRepository.saveToFile();

        return "User successfully unregistered.";
    }

    public boolean returnItem(User user, Book book) {
        if (user == null || book == null) return false;

        Loan loan = LoanRepository.findActiveLoan(user, book);
        if (loan == null) return false;

        // 1. increase quantity
        book.setQuantity(book.getQuantity() + 1);

        // 2. calculate fine if overdue
        double fine = 0;
        if (loan.isOverdue()) {
            long daysLate = loan.getBorrowedDate().until(LocalDate.now()).getDays()
                    - book.getBorrowDuration();

            if (daysLate > 0) {
                fine = daysLate * 1.0; // 1$ per day (example)
                user.addFine(fine);
                loan.setFineAmount((int) fine);
            }
        }

        // 3. mark loan returned
        loan.setReturned(true);
        LoanRepository.saveToFile();

        return true;
    }

    /**
     * Attempts to authenticate a user.
     *
     * @param username input username
     * @param password input password
     * @return true if login succeeds, false otherwise
     */
    public boolean login(String username, String password) {
        User user = UserRepository.findUser(username);

        if (user != null
                && user.getUsername().equalsIgnoreCase(username)
                && user.getPassword().equals(password)) {
            loggedUser = user;
            return true;
        }
        return false;
    }

    /**
     * @return the currently logged-in user
     */
    public User getLoggedUser() {
        return loggedUser;
    }

    /** Logs out the active user. */
    public void logout() {
        loggedUser = null;
    }
}
