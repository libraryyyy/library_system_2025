package Library_System.Domain;

import library_system.domain.Book;
import library_system.domain.Loan;
import library_system.domain.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LoanOverdueTest {

    @Test
    void testLoanNotOverdueOnBorrowDay() {
        Loan loan = new Loan(new User("sana", "1234"), new Book("Java", "John", "111"));
        assertFalse(loan.isOverdue());
    }

    @Test
    void testLoanIsOverdueAfterDueDate() throws Exception {
        Loan loan = new Loan(new User("sana", "1234"),
                new Book("Java", "John", "111"));

        // simulate overdue by changing dueDate
        LocalDate fakeDueDate = LocalDate.now().minusDays(1);

        var dueField = Loan.class.getDeclaredField("dueDate");
        dueField.setAccessible(true);
        dueField.set(loan, fakeDueDate);

        assertTrue(loan.isOverdue());
    }

}
