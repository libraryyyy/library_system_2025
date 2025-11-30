package library_system.Repository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import library_system.domain.Book;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class BookRepository {

    /** Internal list that stores all books in memory. */
    private static final List<Book> books = new ArrayList<>();
    private static final String FILE_PATH = "src/main/resources/books.json";
    private static final ObjectMapper mapper = new ObjectMapper();


    // Load data at startup
    static {
        loadFromFile();
    }


    /**
     * Returns all books.
     */
    public static List<Book> getAll() {
        return new ArrayList<>(books);
    }
    public static void addBook(Book book) {
        books.add(book);
        saveToFile();
    }
    /**
     * Finds books by title (case-insensitive).
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
     * Finds books by author (case-insensitive).
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
     * Finds books by ISBN (exact match).
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
     * Clears all books (mainly for unit tests).
     */
    public static void clear() {
        books.clear();
        saveToFile();
    }

    /**
     * Saves the book list to books.json
     */
    private static void saveToFile() {
        try {
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(FILE_PATH), books);
        } catch (Exception e) {
            System.err.println("ERROR saving books.json → " + e.getMessage());
        }
    }

    /**
     * Loads book data from JSON file.
     */
    private static void loadFromFile() {
        try {
            File file = new File(FILE_PATH);

            if (!file.exists()) {
                mapper.writeValue(file, books); // create empty file
                return;
            }

            List<Book> loaded =
                    mapper.readValue(file, new TypeReference<List<Book>>() {});
            books.clear();
            books.addAll(loaded);

        } catch (Exception e) {
            System.err.println("ERROR loading books.json → " + e.getMessage());
        }
    }


}
