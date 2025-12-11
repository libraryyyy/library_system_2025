package library_system.service;

import library_system.repository.LoanRepository;
import library_system.repository.UserRepository;
import library_system.domain.Loan;
import library_system.domain.User;
import library_system.notification.Observer;
import library_system.repository.LoanRepository;
import java.util.*;

/**
 * Service responsible for sending overdue reminders to users via registered observers.
 * <p>
 * The {@link #sendOverdueReminders()} method returns a status code:
 * 0 = no users, 1 = users exist but no overdue, 2 = reminders sent.
 * </p>
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

            // Build a detailed message including per-item lines with media type, title, overdue days and fine
            StringBuilder sb = new StringBuilder();
            sb.append("--- Overdue Reminder ---\n");
            sb.append("You have the following overdue items:\n\n");

            int totalFine = 0;
            for (Loan loan : loans) {
                String mediaType = loan.getItem() != null ? loan.getItem().getMediaType() : "Unknown";
                String title = loan.getItem() != null ? loan.getItem().getTitle() : "<unknown>";
                int days = loan.getOverdueDays();
                // Respect per-loan payment state: if the loan's fine has already been paid,
                // do not report a fine amount in the reminder.
                if (loan.isFinePaid()) {
                    sb.append(mediaType).append(" - ").append(title)
                            .append(" | Days overdue: ").append(days)
                            .append(" | Fine: ").append("PAID").append("\n");
                } else {
                    // prefer a recorded fineAmount (e.g., charged on return) when present
                    int fineAmount = loan.getFineAmount() > 0 ? loan.getFineAmount() : loan.calculateFine();
                    totalFine += fineAmount;
                    sb.append(mediaType).append(" - ").append(title)
                            .append(" | Days overdue: ").append(days)
                            .append(" | Fine: ").append(fineAmount).append(" NIS\n");
                }
            }
            // Only show total fine if there is an outstanding unpaid amount
            if (totalFine > 0) {
                sb.append("\nTotal fine: ").append(totalFine).append(" NIS\n");
            } else {
                sb.append("\nNo outstanding unpaid fines. Please return the overdue items when possible.\n");
            }

            boolean userNotified = false;
            for (Observer observer : observers) {
                try {
                    observer.notify(user, sb.toString());
                    userNotified = true;
                } catch (Exception ex) {
                    System.err.println("Failed to notify user " + username + ": " + ex.getMessage());
                }
            }
            if (userNotified) anySent = true;
        }

        return anySent ? 2 : 1;
    }
 }
