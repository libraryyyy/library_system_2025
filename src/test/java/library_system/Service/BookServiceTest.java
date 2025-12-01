package library_system.Service;

import library_system.Repository.BookRepository;
import library_system.domain.Book;
import library_system.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookServiceTest {

    private BookService bookService;
    private Book book1;
    private Book book2;

    @BeforeEach
    void setup() {
        BookRepository.clear();  // ✔ مهم جدًا
        bookService = new BookService();
        book1 = new Book("Java Basics", "John Doe", "111");
        book2 = new Book("Advanced Java", "Jane Doe", "222");
        bookService.addBook(book1);
        bookService.addBook(book2);
    }

    @Test
    void testAddBook() {
        Book book3 = new Book("Spring Boot", "Mike Smith", "333");
        bookService.addBook(book3);
        List<Book> found = bookService.searchByTitle("Spring Boot");
        assertEquals(1, found.size());
        assertEquals("Mike Smith", found.get(0).getAuthor());
    }

    @Test
    void testSearchByTitle() {
        List<Book> result = bookService.searchByTitle("Java Basics");
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getAuthor());
    }

    @Test
    void testSearchByAuthor() {
        List<Book> result = bookService.searchByAuthor("Jane Doe");
        assertEquals(1, result.size());
        assertEquals("Advanced Java", result.get(0).getTitle());
    }

    @Test
    void testSearchByIsbn() {
        List<Book> result = bookService.searchByIsbn("111");
        assertEquals(1, result.size());
        assertEquals("Java Basics", result.get(0).getTitle());
    }
}
