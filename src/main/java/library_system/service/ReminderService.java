package library_system.service;

import library_system.Repository.LoanRepository;
import library_system.domain.*;
import library_system.notification.Observer;

import java.util.*;

public class ReminderService {

    private final List<Observer> observers = new ArrayList<>();
    /**
     * Adds a new observer (EmailNotifier, SMSNotifier, etc...)
     *
     * @param observer the observer to add
     */
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    /**
     * Send reminders to users with overdue items (Books + CDs)
     */
    public void sendOverdueReminders() {
        List<Loan> allLoans = LoanRepository.getAllLoans();

        Map<User, Integer> overdueBooks = new HashMap<>();
        Map<User, Integer> overdueCDs = new HashMap<>();

        for (Loan loan : allLoans) {
            if (!loan.isOverdue()|| loan.isReturned()) continue;

            User user = loan.getUser();

            if (loan.getItem() instanceof Book) {
                overdueBooks.put(user, overdueBooks.getOrDefault(user, 0) + 1);
            } else if (loan.getItem() instanceof CD) {
                overdueCDs.put(user, overdueCDs.getOrDefault(user, 0) + 1);
            }
        }

        Set<User> affectedUsers = new HashSet<>();
        affectedUsers.addAll(overdueBooks.keySet());
        affectedUsers.addAll(overdueCDs.keySet());

        for (User user : affectedUsers) {
            int books = overdueBooks.getOrDefault(user, 0);
            int cds = overdueCDs.getOrDefault(user, 0);

            String msg;
            if (cds > 0) {
                msg = "You have " + books + " overdue book(s) and " + cds + " overdue CD(s).";
            } else {
                msg = "You have " + books + " overdue book(s).";
            }

            for (Observer observer : observers) {
                observer.notify(user, msg);
            }
        }
    }
}
