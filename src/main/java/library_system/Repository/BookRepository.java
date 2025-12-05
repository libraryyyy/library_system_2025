package library_system.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import library_system.domain.Book;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BookRepository {

    private static final List<Book> books = new ArrayList<>();
    private static final String FILE_PATH = "src/main/resources/books.json";

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    // دالة التحميل من الملف
    public static void loadFromFile() {
        try {
            File file = new File(FILE_PATH);
            if (file.exists() && file.length() > 0) {
                books.clear(); // مهم عشان ما يتكررش الكتب
                books.addAll(mapper.readValue(file, new TypeReference<List<Book>>() {}));
            }
            System.out.println("Loaded books: " + books.size());
        } catch (Exception e) {
            System.out.println("Error loading books: " + e.getMessage());
            books.clear();
        }
    }

    public static void saveToFile() {
        try {
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(FILE_PATH), books);
        } catch (Exception e) {
            System.out.println("Error saving books: " + e.getMessage());
        }
    }

    public static List<Book> getBooks() {
        return books;
    }

    public static List<Book> getAll() {
        return books;
    }

    public static void addBook(Book b) {
        books.add(b);
        saveToFile();
    }

    public static void clear() {
        books.clear();
        saveToFile();
    }

    // البحث بالعنوان (جزئي)
    public static List<Book> findByTitleContaining(String part) {
        return books.stream()
                .filter(b -> b.getTitle().toLowerCase().contains(part.toLowerCase()))
                .toList();
    }

    // البحث بالعنوان (تطابق تام)
    public static List<Book> findByTitle(String title) {
        return books.stream()
                .filter(book -> book.getTitle().equalsIgnoreCase(title))
                .toList();
    }

    public static List<Book> findByAuthor(String author) {
        return books.stream()
                .filter(b -> b.getAuthor().equalsIgnoreCase(author))
                .toList();
    }

    public static List<Book> findByIsbn(String isbn) {
        return books.stream()
                .filter(b -> b.getIsbn().equalsIgnoreCase(isbn))
                .toList();
    }
}