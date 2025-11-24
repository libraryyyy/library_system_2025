package Library_System.Service;

import library_system.Repository.LoanRepository;
import library_system.Repository.UserRepository;
import library_system.domain.Book;
import library_system.domain.Loan;
import library_system.domain.User;
import library_system.notification.Observer;
import library_system.service.ReminderService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

class ReminderServiceTest {

    private ReminderService reminderService;
    private Observer mockObserver;

    @BeforeEach
    void setup() throws Exception {
        UserRepository.clear();
        LoanRepository.clear();

        reminderService = new ReminderService();

        mockObserver = Mockito.mock(Observer.class);
        reminderService.addObserver(mockObserver);

        User user = new User("sana", "123");
        UserRepository.addUser(user);

        Loan loan = new Loan(user, new Book("Java", "John", "111"));

        var dueField = Loan.class.getDeclaredField("dueDate");
        dueField.setAccessible(true);
        dueField.set(loan, LocalDate.now().minusDays(1));

        LoanRepository.addLoan(loan);
    }

    @Test
    void testSendReminder() {
        reminderService.sendOverdueReminders();

        verify(mockObserver, times(1))
                .notify(any(User.class), eq("You have 1 overdue book(s)."));
    }
}
