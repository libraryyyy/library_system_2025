package library_system.Repository;

import library_system.domain.Book;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class BookRepositoryTest {

    @BeforeEach
    public void setup() {
        BookRepository.clear();
    }

    @AfterEach
    public void tearDown() {
        BookRepository.clear();
    }

    @Test
    public void testAddAndSearchBook() {
        BookRepository.addBook(new Book("The Hobbit", "J.R.R. Tolkien", "12345"));
        BookRepository.addBook(new Book("Hobbit Tales", "Someone Else", "67890"));

        List<Book> byTitle = BookRepository.findByTitle("hobbit");
        Assertions.assertEquals(2, byTitle.size());

        List<Book> byAuthor = BookRepository.findByAuthor("Tolkien");
        Assertions.assertEquals(1, byAuthor.size());

        List<Book> byIsbn = BookRepository.findByIsbn("12345");
        Assertions.assertEquals(1, byIsbn.size());
    }
}
