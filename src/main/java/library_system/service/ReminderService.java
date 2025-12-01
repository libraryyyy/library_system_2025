package library_system.service;

import library_system.Repository.LoanRepository;
import library_system.Repository.UserRepository;
import library_system.domain.Book;
import library_system.domain.CD;
import library_system.domain.Loan;
import library_system.domain.User;
import library_system.notification.Observer;

import java.util.*;

/**
 * Service responsible for sending overdue reminders to users via registered observers.
 *<p>
 * The {@link #sendOverdueReminders()} method returns a status code:
 * 0 = no users, 1 = users exist but no overdue, 2 = reminders sent.
 *</p>
 */
public class ReminderService {

    /** List of observers that will receive notification events. */
    private final List<Observer> observers = new ArrayList<>();

    /**
     * Registers a new observer to receive overdue reminder events.
     *
     * @param observer notification channel (EmailNotifier, ConsoleNotifier, etc.)
     */
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    /**
     * Sends overdue reminders to all users who have outstanding overdue items.
     *
     * Return codes:
     * 0 = no users
     * 1 = users exist but no overdue items
     * 2 = reminders were sent
     *
     * @return status code (0/1/2)
     */
    public int sendOverdueReminders() {

        List<User> allUsers = UserRepository.getAllUsers();

        if (allUsers.isEmpty()) {
            System.out.println("No users in system.");
            return 0;
        }

        List<Loan> overdueLoans = LoanRepository.getOverdueLoans();
        if (overdueLoans.isEmpty()) {
            System.out.println("No overdue items found.");
            return 1;
        }

        Map<String, List<Loan>> byUser = new HashMap<>();
        for (Loan loan : overdueLoans) {
            String username = loan.getUser().getUsername();
            byUser.computeIfAbsent(username, k -> new ArrayList<>()).add(loan);
        }

        boolean anySent = false;
        for (Map.Entry<String, List<Loan>> e : byUser.entrySet()) {
            String username = e.getKey();
            User user = UserRepository.findUser(username);
            if (user == null) continue;

            List<Loan> loans = e.getValue();
            long bookCount = loans.stream().filter(l -> l.getItem() instanceof Book).count();
            long cdCount = loans.stream().filter(l -> l.getItem() instanceof CD).count();

            String message;
            if (bookCount > 0 && cdCount > 0) {
                message = "You have " + bookCount + " overdue book(s) and " + cdCount + " overdue CD(s).";
            } else if (bookCount > 0) {
                message = "You have " + bookCount + " overdue book(s).";
            } else {
                message = "You have " + cdCount + " overdue CD(s).";
            }

            for (Observer observer : observers) {
                try {
                    observer.notify(user, message);
                    anySent = true;
                } catch (Exception ex) {
                    System.err.println("Failed to notify user " + username + ": " + ex.getMessage());
                }
            }
        }

        return anySent ? 2 : 1;
    }
 }
