package Library_System.Service;

import library_system.Repository.BookRepository;
import library_system.Repository.LoanRepository;
import library_system.Repository.UserRepository;
import library_system.domain.Book;
import library_system.domain.Loan;
import library_system.domain.User;
import library_system.service.BookService;
import library_system.service.BorrowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BorrowServiceTest {

    private BorrowService borrowService;
    private BookService bookService;
    private User user;

    @BeforeEach
    void setup() {
        BookRepository.clear();
        LoanRepository.clear();
        UserRepository.clear();

        borrowService = new BorrowService();
        bookService = new BookService();

        user = new User("sana", "1234");
        UserRepository.addUser(user);

        bookService.addBook(new Book("Java", "John", "111"));
        bookService.addBook(new Book("Java", "Jane", "222")); // second copy
    }

    @Test
    void testBorrowSuccess() {
        boolean result = borrowService.borrow(user, "Java");
        assertTrue(result);

        List<Loan> loans = LoanRepository.getUserLoans("sana");
        assertEquals(1, loans.size());
        assertEquals("Java", loans.get(0).getBook().getTitle());
        assertTrue(loans.get(0).getBook().isBorrowed());
    }

    @Test
    void testBorrowFailure_NoAvailableCopy() {
        borrowService.borrow(user, "Java");
        borrowService.borrow(user, "Java"); // borrow second copy

        boolean thirdAttempt = borrowService.borrow(user, "Java");
        assertFalse(thirdAttempt);  // no more copies
    }
}
