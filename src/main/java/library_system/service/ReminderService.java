package library_system.service;

import library_system.Repository.LoanRepository;
import library_system.domain.Loan;
import library_system.domain.User;
import library_system.notification.Observer;

import java.util.*;

public class ReminderService {

    private final List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void sendOverdueReminders() {
        List<Loan> allLoans = LoanRepository.getAllLoans();

        Map<User, Integer> overdueMap = new HashMap<>();

        for (Loan loan : allLoans) {
            if (loan.isOverdue()) {
                overdueMap.put(
                        loan.getUser(),
                        overdueMap.getOrDefault(loan.getUser(), 0) + 1
                );
            }
        }

        for (Map.Entry<User, Integer> entry : overdueMap.entrySet()) {
            User user = entry.getKey();
            int count = entry.getValue();

            String msg = "You have " + count + " overdue book(s).";

            for (Observer observer : observers) {
                observer.notify(user, msg);
            }
        }
    }
}
