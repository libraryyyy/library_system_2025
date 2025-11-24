package library_system.Repository;

import library_system.domain.Book;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory repository for storing and querying books.
 */
public class BookRepository {

    /** Internal list that stores all books in memory. */
    private static final List<Book> books = new ArrayList<>();

    /**
     * Adds a new book to the repository.
     *
     * @param book book to be added.
     */
    public static void addBook(Book book) {
        books.add(book);
    }

    /**
     * Finds books with a title matching the given value (case-insensitive).
     *
     * @param title title to search for.
     * @return list of matching books.
     */
    public static List<Book> findByTitle(String title) {
        List<Book> result = new ArrayList<>();
        for (Book b : books) {
            if (b.getTitle().equalsIgnoreCase(title)) {
                result.add(b);
            }
        }
        return result;
    }

    /**
     * Finds books with an author matching the given value (case-insensitive).
     *
     * @param author author name to search for.
     * @return list of matching books.
     */
    public static List<Book> findByAuthor(String author) {
        List<Book> result = new ArrayList<>();
        for (Book b : books) {
            if (b.getAuthor().equalsIgnoreCase(author)) {
                result.add(b);
            }
        }
        return result;
    }

    /**
     * Finds books with an ISBN matching the given value.
     *
     * @param isbn ISBN to search for.
     * @return list of matching books.
     */
    public static List<Book> findByIsbn(String isbn) {
        List<Book> result = new ArrayList<>();
        for (Book b : books) {
            if (b.getIsbn().equals(isbn)) {
                result.add(b);
            }
        }
        return result;
    }

    /**
     * Clears all books from the repository.
     * Used mainly in unit tests.
     */
    public static void clear() {
        books.clear();
    }
}
