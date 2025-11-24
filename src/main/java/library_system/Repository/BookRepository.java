package library_system.repository;

import library_system.domain.Book;
import java.util.ArrayList;
import java.util.List;

public class BookRepository {

    private static final List<Book> books = new ArrayList<>();

    public static void addBook(Book book) {
        books.add(book);
    }

    public static List<Book> findByTitle(String title) {
        List<Book> result = new ArrayList<>();
        for (Book b : books) {
            if (b.getTitle().equalsIgnoreCase(title)) result.add(b);
        }
        return result;
    }

    public static List<Book> findByAuthor(String author) {
        List<Book> result = new ArrayList<>();
        for (Book b : books) {
            if (b.getAuthor().equalsIgnoreCase(author)) result.add(b);
        }
        return result;
    }

    public static List<Book> findByIsbn(String isbn) {
        List<Book> result = new ArrayList<>();
        for (Book b : books) {
            if (b.getIsbn().equals(isbn)) result.add(b);
        }
        return result;
    }

    public static void clear() {
        books.clear();
    }
}
