package library_system.service;

import library_system.repository.BookRepository;
import library_system.repository.CDRepository;
import library_system.repository.LoanRepository;
import library_system.repository.UserRepository;
import library_system.domain.Book;
import library_system.domain.CD;
import library_system.domain.User;
import library_system.service.BorrowService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class BorrowServiceTest {

    private BorrowService borrowService;

    @BeforeEach
    public void setup() {
        BookRepository.clear();
        CDRepository.clear();
        LoanRepository.clear();
        UserRepository.clear();
        borrowService = new BorrowService();
    }

    @AfterEach
    public void tearDown() {
        BookRepository.clear();
        CDRepository.clear();
        LoanRepository.clear();
        UserRepository.clear();
    }

    @Test
    public void testBorrowBookSuccessfully() {
        BookRepository.addBook(new Book("Title A", "Author A", "ISBN-A"));
        User u = new User("user1", "pass", "u1@example.com");
        UserRepository.addUser(u);

        // simulate search and selection: pick the repository-held instance
        List<Book> found = BookRepository.findByTitle("Title A");
        Book toBorrow = found.get(0);
        boolean ok = borrowService.borrowBookInstance(u, toBorrow);
        Assertions.assertTrue(ok);

        List<Book> books = BookRepository.findByTitle("Title A");
        Assertions.assertTrue(books.get(0).isBorrowed());
    }

    @Test
    public void testBorrowCDSuccessfully() {
        CDRepository.addCD(new CD("CD Title", "Artist X"));
        User u = new User("user2", "pass", "u2@example.com");
        UserRepository.addUser(u);

        List<CD> found = CDRepository.findByTitle("CD Title");
        CD toBorrow = found.get(0);
        boolean ok = borrowService.borrowCDInstance(u, toBorrow);
        Assertions.assertTrue(ok);

        List<CD> cds = CDRepository.findByTitle("CD Title");
        Assertions.assertTrue(cds.get(0).isBorrowed());
    }
}
