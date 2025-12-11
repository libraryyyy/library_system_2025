package library_system.service;

import library_system.domain.*;
import library_system.repository.BookRepository;
import library_system.repository.CDRepository;
import library_system.repository.LoanRepository;
import library_system.repository.UserRepository;
import org.junit.jupiter.api.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class BorrowServiceTest {

    private BorrowService service;
    private User user;

    @BeforeEach
    public void setup() {
        service = new BorrowService();
        user = new User("sara", "123", "sara@test.com");

        // reset repositories
        BookRepository.getBooks().clear();
        CDRepository.getAll().clear();
        LoanRepository.clear();
        UserRepository.getUsers().clear();

        UserRepository.getUsers().add(user);
    }

    // ------------------------------------------------------------
    // Borrow Tests
    // ------------------------------------------------------------

    @Test
    public void testBorrowBookSuccess() {
        Book book = new Book("123", "Java", "Sara");
        book.setQuantity(2);
        BookRepository.getBooks().add(book);

        boolean result = service.borrowBookInstance(user, book);

        assertTrue(result);
        assertEquals(1, book.getQuantity());
        assertEquals(1, LoanRepository.getAllLoans().size());
    }

    @Test
    public void testBorrowFailsOutOfStock() {
        Book book = new Book("111", "OS", "Tanenbaum");
        book.setQuantity(0);
        BookRepository.getBooks().add(book);

        boolean result = service.borrowBookInstance(user, book);

        assertFalse(result);
        assertEquals(0, book.getQuantity());
    }

    @Test
    public void testBorrowFailsUserHasOverdueLoans() {
        Book book = new Book("55", "Networks", "Kurose");
        book.setQuantity(1);
        BookRepository.getBooks().add(book);

        // create overdue loan
        Loan loan = new Loan(user, book);
        loan.setBorrowedDate(LocalDate.now().minusDays(30)); // overdue
        LoanRepository.addLoan(loan);

        assertTrue(loan.isOverdue());

        boolean result = service.borrowBookInstance(user, book);
        assertFalse(result);
    }

    @Test
    public void testFailBorrowSameBookTwice() {
        Book book = new Book("77", "AI", "Russell");
        book.setQuantity(2);
        BookRepository.getBooks().add(book);

        // first borrow
        assertTrue(service.borrowBookInstance(user, book));

        // second should fail
        assertFalse(service.borrowBookInstance(user, book));
    }

    // ------------------------------------------------------------
    // Return Tests
    // ------------------------------------------------------------

    @Test
    public void testReturnFailsNoActiveLoan() {
        Book book = new Book("456", "Cloud", "Mark");
        book.setQuantity(2);
        BookRepository.getBooks().add(book);

        boolean result = service.returnItem(user, book);

        assertFalse(result);
    }

    @Test
    public void testReturnSuccess_NoFine() {
        Book book = new Book("999", "Algorithms", "CLRS");
        book.setQuantity(1);
        BookRepository.getBooks().add(book);

        Loan loan = new Loan(user, book);
        loan.setBorrowedDate(LocalDate.now()); // no overdue
        LoanRepository.addLoan(loan);

        boolean result = service.returnItem(user, book);

        assertTrue(result);
        assertEquals(2, book.getQuantity());
        assertTrue(loan.isReturned(), "Loan must be marked returned");
    }

    @Test
    public void testReturnSuccess_WithFine() {

        LoanRepository.clear();
        BookRepository.clear();

        Book book = new Book("222", "Clean Code", "Martin");
        book.setQuantity(1);
        BookRepository.getBooks().add(book);

        Loan loan = new Loan(user, book);

        // Important: make it overdue!
        loan.setBorrowedDate(LocalDate.now().minusDays(30)); // overdue: 30 - 28 = 2 days
        LoanRepository.addLoan(loan);

        double previousFine = user.getFineBalance();

        boolean result = service.returnItem(user, book);

        assertTrue(result);
        assertEquals(2, book.getQuantity());
        assertTrue(loan.isReturned());
        assertTrue(user.getFineBalance() > previousFine, "User must receive additional fine");
    }


    @Test
    public void testReturnFails_ItemInsideLoanIsNull() {
        Book book = new Book("333", "DB Systems", "Raghu");
        book.setQuantity(1);
        BookRepository.getBooks().add(book);

        Loan loan = new Loan(user, book);
        loan.setItem(null); // corrupt loan
        LoanRepository.addLoan(loan);

        boolean result = service.returnItem(user, book);

        assertFalse(result, "Return must fail when internal item is null");
    }
}
