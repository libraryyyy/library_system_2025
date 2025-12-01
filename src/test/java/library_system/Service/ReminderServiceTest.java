package library_system.Service;

import library_system.Repository.LoanRepository;
import library_system.Repository.UserRepository;
import library_system.domain.Book;
import library_system.domain.CD;
import library_system.domain.Loan;
import library_system.domain.User;
import library_system.notification.Observer;
import library_system.service.ReminderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

class ReminderServiceTest {

    private ReminderService reminderService;
    private Observer mockObserver;
    private User user;

    @BeforeEach
    void setup() throws Exception {
        UserRepository.clear();
        LoanRepository.clear();

        reminderService = new ReminderService();

        // نعمل mock للـ Observer (بدل EmailNotifier الفعلي)
        mockObserver = mock(Observer.class);
        reminderService.addObserver(mockObserver);

        // إنشاء مستخدم
        user = new User("sana", "123", "sana@example.com");
        UserRepository.addUser(user);
    }

    @Test
    void testSendReminderForBook() {
        // Arrange: Loan متأخر لكتاب
        Book book = new Book("Java", "John", "111");
        Loan loan = new Loan(user, book);
        loan.setDueDate(LocalDate.now().minusDays(1));
        LoanRepository.addLoan(loan);

        // Act
        reminderService.sendOverdueReminders();

        // Assert: تحقق أن notify تم استدعاؤها مرة واحدة بالرسالة الصحيحة
        verify(mockObserver, times(1))
                .notify(user, "You have 1 overdue book(s).");
    }

    @Test
    void testSendReminderForBookAndCD() {
        // Arrange: Loan متأخر لكتاب وCD
        Book book = new Book("Java", "John", "111");
        CD cd = new CD("Best Hits", "Artist");

        Loan loanBook = new Loan(user, book);
        Loan loanCD = new Loan(user, cd);

        loanBook.setDueDate(LocalDate.now().minusDays(1));
        loanCD.setDueDate(LocalDate.now().minusDays(2));

        LoanRepository.addLoan(loanBook);
        LoanRepository.addLoan(loanCD);

        // Act
        reminderService.sendOverdueReminders();

        // Assert: تحقق من استدعاء notify برسالة متوافقة
        verify(mockObserver, times(1))
                .notify(user, "You have 1 overdue book(s) and 1 overdue CD(s).");
    }
}
