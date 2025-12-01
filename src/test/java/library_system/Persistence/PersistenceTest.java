package library_system.Persistence;

import library_system.Repository.BookRepository;
import library_system.Repository.CDRepository;
import library_system.Repository.LoanRepository;
import library_system.Repository.UserRepository;
import library_system.domain.Book;
import library_system.domain.CD;
import library_system.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class PersistenceTest {

    @BeforeEach
    public void setup() {
        BookRepository.clear();
        CDRepository.clear();
        LoanRepository.clear();
        UserRepository.clear();
    }

    @AfterEach
    public void tearDown() {
        BookRepository.clear();
        CDRepository.clear();
        LoanRepository.clear();
        UserRepository.clear();
    }

    @Test
    public void testSaveAndLoadBooks() {
        BookRepository.addBook(new Book("PersistBook", "X", "ISBN-P"));
        BookRepository.loadFromFile();
        List<Book> books = BookRepository.findByTitle("PersistBook");
        Assertions.assertEquals(1, books.size());
    }

    @Test
    public void testSaveAndLoadCDs() {
        CDRepository.addCD(new CD("PersistCD", "ArtistP"));
        CDRepository.loadFromFile();
        List<CD> cds = CDRepository.search("PersistCD");
        Assertions.assertEquals(1, cds.size());
    }

    @Test
    public void testSaveAndLoadUsers() {
        UserRepository.addUser(new User("u1", "p", "u1@example.com"));
        UserRepository.loadFromFile();
        Assertions.assertNotNull(UserRepository.findUser("u1"));
    }
}

