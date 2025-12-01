package library_system.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LoanOverdueTest {

    @Test
    void testLoanNotOverdueOnBorrowDay() {
        User user1 = new User("sana", "1234", "sana@example.com");
        User user2 = new User("ali", "abcd", "ali@example.com");

        Loan loan1 = new Loan(user1, new Book("Java", "John", "111"));
        Loan loan2 = new Loan(user2, new CD("Hits", "Artist"));

        assertFalse(loan1.isOverdue());
        assertFalse(loan2.isOverdue());
    }

    @Test
    void testCDLoanOverdueMultipleUsers() throws Exception {
        User user1 = new User("sana", "1234", "sana@example.com");
        User user2 = new User("ali", "abcd", "ali@example.com");

        CD cd1 = new CD("Hits", "Artist");
        CD cd2 = new CD("Best Hits", "Band");

        Loan loanCD1 = new Loan(user1, cd1);
        Loan loanCD2 = new Loan(user2, cd2);

        var dueField1 = Loan.class.getDeclaredField("dueDate");
        dueField1.setAccessible(true);
        dueField1.set(loanCD1, LocalDate.now().minusDays(2));

        var dueField2 = Loan.class.getDeclaredField("dueDate");
        dueField2.setAccessible(true);
        dueField2.set(loanCD2, LocalDate.now().minusDays(3));

        assertTrue(loanCD1.isOverdue());
        assertEquals(40, loanCD1.calculateFine(), "User1 CD overdue fine should be 20 NIS/day x 2");

        assertTrue(loanCD2.isOverdue());
        assertEquals(60, loanCD2.calculateFine(), "User2 CD overdue fine should be 20 NIS/day x 3");
    }

    @Test
    void testBookLoanOverdueMultipleUsers() throws Exception {
        User user1 = new User("sana", "1234", "sana@example.com");
        User user2 = new User("ali", "abcd", "ali@example.com");

        Book book1 = new Book("Java", "John", "111");
        Book book2 = new Book("Python", "Alice", "222");

        Loan loanBook1 = new Loan(user1, book1);
        Loan loanBook2 = new Loan(user2, book2);

        var dueField1 = Loan.class.getDeclaredField("dueDate");
        dueField1.setAccessible(true);
        dueField1.set(loanBook1, LocalDate.now().minusDays(1));

        var dueField2 = Loan.class.getDeclaredField("dueDate");
        dueField2.setAccessible(true);
        dueField2.set(loanBook2, LocalDate.now().minusDays(4));

        assertTrue(loanBook1.isOverdue());
        assertTrue(loanBook2.isOverdue());
    }
}
