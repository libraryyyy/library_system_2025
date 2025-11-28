package Library_System.Service;

import library_system.Repository.BookRepository;
import library_system.Repository.LoanRepository;
import library_system.Repository.CDRepository;
import library_system.domain.*;
import library_system.service.BorrowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BorrowServiceTest {

    private BorrowService borrowService;
    private User user;

    @BeforeEach
    void setup() {
        borrowService = new BorrowService();
        user = new User("sana", "123");

        BookRepository.clear();
        CDRepository.clear();
        LoanRepository.clear();
    }

    @Test
    void testBorrowSuccess() {
        Book book = new Book("Java", "John", "111");
        BookRepository.addBook(book);

        boolean ok = borrowService.borrowBook(user, "Java");
        assertTrue(ok, "Borrowing a book should succeed.");

        assertTrue(book.isBorrowed());
        assertEquals(1, LoanRepository.getUserLoans(user.getUsername()).size());
    }

    @Test
    void testBorrowCDSuccess() {
        CD cd = new CD("Hits", "Artist");
        CDRepository.addCD(cd);

        boolean ok = borrowService.borrowCD(user, "Hits");
        assertTrue(ok, "Borrowing a CD should succeed.");

        assertTrue(cd.isBorrowed());
        assertEquals(1, LoanRepository.getUserLoans(user.getUsername()).size());
    }
    @Test
    void testBorrowBlockedDueToUnpaidFines() {
        user.addFine(50);

        BookRepository.addBook(new Book("Java", "John", "111"));

        boolean ok = borrowService.borrowBook(user, "Java");
        assertFalse(ok, "User with unpaid fines must not borrow.");
    }
    @Test
    void testBorrowBlockedDueToOverdueLoan() throws Exception {
        Book book = new Book("Java", "John", "111");
        BookRepository.addBook(book);

        // Borrow once to create a loan
        borrowService.borrowBook(user, "Java");

        Loan loan = LoanRepository.getUserLoans(user.getUsername()).get(0);

        // نحط التاريخ Due في الماضي لتصير Overdue
        Field due = Loan.class.getDeclaredField("dueDate");
        due.setAccessible(true);
        due.set(loan, LocalDate.now().minusDays(5));

        // الآن نضيف كتاب جديد ونحاول نستعير
        BookRepository.addBook(new Book("C++", "Alice", "222"));

        boolean ok = borrowService.borrowBook(user, "C++");
        assertFalse(ok, "User with overdue items cannot borrow.");
    }
    @Test
    void testBorrowFailsNoAvailableCopy() {
        Book book = new Book("Java", "John", "111");
        book.setBorrowed(true); // already borrowed

        BookRepository.addBook(book);

        boolean ok = borrowService.borrowBook(user, "Java");
        assertFalse(ok, "Borrow should fail if no available copy exists.");
    }
}
