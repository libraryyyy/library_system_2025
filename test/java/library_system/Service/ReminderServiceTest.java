package library_system.Service;

import library_system.Repository.LoanRepository;
import library_system.Repository.UserRepository;
import library_system.domain.CD;
import library_system.domain.Book;
import library_system.domain.Loan;
import library_system.domain.User;
import library_system.notification.Observer;
import library_system.service.ReminderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReminderServiceTest {

    private ReminderService reminderService;

    @BeforeEach
    public void setup() {
        UserRepository.clear();
        LoanRepository.clear();
        reminderService = new ReminderService();
    }

    @AfterEach
    public void tearDown() {
        UserRepository.clear();
        LoanRepository.clear();
    }

    @Test
    public void testNoUsers() {
        int status = reminderService.sendOverdueReminders();
        Assertions.assertEquals(0, status);
    }

    @Test
    public void testNoOverdue() {
        User u = new User("u1", "p", "u1@example.com");
        UserRepository.addUser(u);
        int status = reminderService.sendOverdueReminders();
        Assertions.assertEquals(1, status);
    }

    @Test
    public void testRemindersSent() {
        User u = new User("u2", "p", "u2@example.com");
        UserRepository.addUser(u);

        Book b = new Book("Old Book", "A", "ISBN-OLD");
        Loan loan = new Loan(u, b);
        loan.setBorrowedDate(LocalDate.now().minusDays(30));
        loan.setDueDate(LocalDate.now().minusDays(15));
        List<Loan> list = new ArrayList<>();
        list.add(loan);
        // directly inject into repository for test
        LoanRepository.clear();
        LoanRepository.addLoan(loan);

        // add a no-op observer that records notifications
        final boolean[] called = {false};
        reminderService.addObserver(new Observer() {
            @Override
            public void notify(User user, String message) {
                called[0] = true;
            }
        });

        int status = reminderService.sendOverdueReminders();
        Assertions.assertEquals(2, status);
        Assertions.assertTrue(called[0]);
    }
}

