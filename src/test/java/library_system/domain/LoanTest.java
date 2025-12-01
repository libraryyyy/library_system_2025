package library_system.domain;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class LoanTest {

    @Test
    void testLoanReturnedFlag() {
        User user = new User("testUser", "pass", "testUser@example.com");
        Book book = new Book("Java Basics", "Some Author", "1234567890");
        Loan loan = new Loan(user, book);

        assertFalse(loan.isReturned());
        loan.markReturned();
        assertTrue(loan.isReturned());
    }

    @Test
    void testLoanOverdueCalculation() {
        User user = new User("testUser", "pass", "testUser@example.com");
        Book book = new Book("Java Basics", "Some Author", "1234567890");
        Loan loan = new Loan(user, book);
        loan.setDueDate(LocalDate.now().minusDays(1));

        assertTrue(loan.isOverdue());
        assertEquals(1, loan.getOverdueDays());
    }

    @Test
    void testCalculateFine() {
        User user = new User("testUser", "pass", "testUser@example.com");
        Book book = new Book("Java Basics", "Some Author", "1234567890");
        Loan loan = new Loan(user, book);
        loan.setDueDate(LocalDate.now().minusDays(3));

        int expectedFine = book.getFineStrategy().calculateFine(3);
        assertEquals(expectedFine, loan.calculateFine());
    }
}
