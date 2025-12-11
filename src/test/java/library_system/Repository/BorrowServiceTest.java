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
        LoanRepository.getAllLoans().clear();
        UserRepository.getUsers().clear();

        UserRepository.getUsers().add(user);
    }

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
        loan.setBorrowedDate(LocalDate.now().minusDays(30));
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

        // second borrow -> should fail because active loan exists
        assertFalse(service.borrowBookInstance(user, book));
    }




    @Test
    public void testReturnFailsNoActiveLoan() {
        Book book = new Book("456", "Cloud", "Mark");
        book.setQuantity(2);
        BookRepository.getBooks().add(book);

        boolean result = service.returnItem(user, book);

        assertFalse(result);
    }


}
