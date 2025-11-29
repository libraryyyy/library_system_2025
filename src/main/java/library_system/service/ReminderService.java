package library_system.service;

import library_system.Repository.LoanRepository;
import library_system.domain.*;
import library_system.notification.Observer;

import java.util.*;

public class ReminderService {

    private final List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    /**
     * Send reminders to users with overdue items (Books + CDs)
     */
    public void sendOverdueReminders() {
        List<Loan> allLoans = LoanRepository.getAllLoans();

        Map<User, Integer> overdueBooksMap = new HashMap<>();
        Map<User, Integer> overdueCDsMap = new HashMap<>();

        for (Loan loan : allLoans) {
            if (!loan.isOverdue()) continue;

            User user = loan.getUser();
            if (loan.getItem() instanceof Book) {
                overdueBooksMap.put(user, overdueBooksMap.getOrDefault(user, 0) + 1);
            } else if (loan.getItem() instanceof CD) {
                overdueCDsMap.put(user, overdueCDsMap.getOrDefault(user, 0) + 1);
            }
        }

        Set<User> users = new HashSet<>();
        users.addAll(overdueBooksMap.keySet());
        users.addAll(overdueCDsMap.keySet());

        for (User user : users) {
            int books = overdueBooksMap.getOrDefault(user, 0);
            int cds = overdueCDsMap.getOrDefault(user, 0);

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
