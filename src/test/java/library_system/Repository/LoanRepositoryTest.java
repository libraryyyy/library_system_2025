package library_system.repository;

import library_system.domain.Book;
import library_system.domain.CD;
import library_system.domain.Loan;
import library_system.domain.User;
import org.junit.jupiter.api.*;

import java.io.File;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LoanRepositoryTest {

    private static final File loanFile = FileUtil.getDataFile("loans.json");

    @BeforeEach
    void setup() {
        LoanRepository.clear(); // تنظيف قبل كل تيست
    }

    @AfterEach
    void cleanup() {
        LoanRepository.clear(); // تنظيف بعد كل تيست
    }

    @Test
    void testLoad_noFile_createsEmptyFile() {
        if (loanFile.exists()) loanFile.delete();
        LoanRepository.loadFromFile();
        assertTrue(loanFile.exists(), "File should be created if not present");
        assertEquals(0, LoanRepository.getAllLoans().size(), "Loans list should be empty");
    }

    @Test
    void testLoad_rootNotArray() throws Exception {
        User user = new User("a", "p", "x@gmail.com");
        Book book = new Book("T", "A", "I", 1);
        Loan loan = new Loan(user, book);
        loan.setBorrowedDate(LocalDate.now());
        loan.setReturned(false);

        MapperProvider.MAPPER.writerWithDefaultPrettyPrinter().writeValue(loanFile, loan); // single object

        LoanRepository.loadFromFile();

        assertEquals(1, LoanRepository.getAllLoans().size(), "Should load single loan from object JSON");
    }

    @Test
    void testLoad_validLoan() throws Exception {
        User user = new User("user1", "pass", "user1@example.com");
        Book book = new Book("Title", "Author", "123", 7);
        Loan loan = new Loan(user, book);
        loan.setBorrowedDate(LocalDate.now());
        LoanRepository.addLoan(loan);

        LoanRepository.loadFromFile();

        assertEquals(1, LoanRepository.getAllLoans().size());
        Loan l = LoanRepository.getAllLoans().get(0);
        assertEquals("user1", l.getUser().getUsername());
        assertEquals("Title", l.getItem().getTitle());
    }

    @Test
    void testLoad_emailSanitization() throws Exception {
        User user = new User("user2", "pass", "User2@Example.Com ");
        Book book = new Book("Title", "Author", "234", 7);
        Loan loan = new Loan(user, book);
        LoanRepository.addLoan(loan);

        LoanRepository.loadFromFile();

        Loan l = LoanRepository.getAllLoans().get(0);
        assertEquals("user2@example.com", l.getUser().getEmail(), "Email should be sanitized");
    }

    @Test
    void testLoad_fixMissingDueDate() throws Exception {
        User user = new User("user3", "pass", "user3@example.com");
        Book book = new Book("Book3", "Author3", "345", 10); // سيتم استبداله من JSON بعد التحميل
        Loan loan = new Loan(user, book);
        loan.setBorrowedDate(LocalDate.now());
        loan.setDueDate(null);
        LoanRepository.addLoan(loan);

        LoanRepository.loadFromFile();

        Loan l = LoanRepository.getAllLoans().get(0);

        // استخرج المدة الحقيقية من الـ item بعد التحميل
        int actualBorrowDuration = l.getItem().getBorrowDuration();

        assertNotNull(l.getDueDate(), "Due date should be fixed automatically");
        assertEquals(
                l.getBorrowedDate().plusDays(actualBorrowDuration),
                l.getDueDate()
        );
    }
    @Test
    void testMarkLoanReturned_simple() {
        User user = new User("aaa", "pass", "a@gmail.com");
        Book book = new Book("T", "A", "I1", 5);

        Loan loan = new Loan(user, book);
        LoanRepository.addLoan(loan);

        assertFalse(loan.isReturned(), "Loan should start as NOT returned");

        LoanRepository.markLoanReturned(loan);

        Loan l = LoanRepository.getAllLoans().get(0);
        assertTrue(l.isReturned(), "Loan should be marked returned");
    }


    @Test
    void testMarkLoanReturned_withFine() {
        User user = new User("bbb", "pass", "b@gmail.com");
        Book book = new Book("T2", "A2", "I2", 7);

        Loan loan = new Loan(user, book);
        LoanRepository.addLoan(loan);

        LoanRepository.markLoanReturned(loan, 25);

        Loan l = LoanRepository.getAllLoans().get(0);

        assertTrue(l.isReturned(), "Loan must be marked returned");
        assertEquals(25, l.getFineAmount(), "Fine must be saved correctly");
    }




    @Test
    void testLoad_fixInvalidDueDate() throws Exception {
        User user = new User("user4", "pass", "user4@example.com");
        Book book = new Book("Book4", "Author4", "456", 5);
        Loan loan = new Loan(user, book);
        loan.setBorrowedDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().minusDays(1)); // invalid
        LoanRepository.addLoan(loan);

        LoanRepository.loadFromFile();

        Loan l = LoanRepository.getAllLoans().get(0);
        assertTrue(l.getDueDate().isAfter(l.getBorrowedDate()), "Due date should be corrected if invalid");
    }

    @Test
    void testLoad_defaultsFineFields() throws Exception {
        User user = new User("user5", "pass", "user5@example.com");
        Book book = new Book("Book5", "Author5", "567", 7);
        Loan loan = new Loan(user, book);
        loan.setBorrowedDate(LocalDate.now());
        // finePaid and fineAmount not set
        LoanRepository.addLoan(loan);

        LoanRepository.loadFromFile();

        Loan l = LoanRepository.getAllLoans().get(0);
        assertFalse(l.isFinePaid(), "Default finePaid should be false");
        assertEquals(0, l.getFineAmount(), "Default fineAmount should be 0");
    }

    @Test
    void testLoad_inferMediaType_book() throws Exception {
        User user = new User("user6", "pass", "user6@example.com");
        Book book = new Book("Book6", "Author6", "678", 7);
        Loan loan = new Loan(user, book);
        LoanRepository.addLoan(loan);

        LoanRepository.loadFromFile();

        Loan l = LoanRepository.getAllLoans().get(0);
        assertEquals("BOOK", l.getItem().getMediaType(), "Media type should be inferred as BOOK");
    }

}
