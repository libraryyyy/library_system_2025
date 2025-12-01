package library_system.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import library_system.domain.Book;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing Book objects persisted to JSON.
 * <p>
 * Provides load/save operations that ensure polymorphic type information
 * (the "mediaType" discriminator) is present. Also contains search helpers.
 * </p>
 */
public class BookRepository {

    private static final List<Book> books = new ArrayList<>();
    private static final ObjectMapper mapper = MapperProvider.MAPPER;
    private static final String FILE_NAME = "books.json";

    private static final File FILE = FileUtil.getDataFile(FILE_NAME);

    /**
     * Loads books from JSON file. If the file or entries lack the required
     * polymorphic "mediaType" property, this method will attempt to repair
     * the data and rewrite the corrected JSON back to disk.
     */
    public static void loadFromFile() {
        try {
            if (!FILE.exists() || FILE.length() == 0) {
                // ensure an empty array file exists
                saveToFile();
                return;
            }

            JsonNode root = mapper.readTree(FILE);
            ArrayNode array;
            if (root == null || root.isNull()) {
                array = mapper.createArrayNode();
            } else if (root.isArray()) {
                array = (ArrayNode) root;
            } else {
                // single object -> wrap into array
                array = mapper.createArrayNode();
                array.add(root);
            }

            boolean fixed = false;
            for (int i = 0; i < array.size(); i++) {
                JsonNode node = array.get(i);
                if (node != null && node.isObject()) {
                    ObjectNode obj = (ObjectNode) node;
                    if (!obj.has("mediaType") || obj.get("mediaType").isNull() || obj.get("mediaType").asText().isEmpty()) {
                        // Best-effort detection: books have 'isbn' or 'author' fields
                        if (obj.has("isbn") || obj.has("author")) {
                            obj.put("mediaType", "BOOK");
                            fixed = true;
                        }
                    }
                }
            }

            if (fixed) {
                // rewrite corrected JSON back to file
                mapper.writerWithDefaultPrettyPrinter().writeValue(FILE, array);
            }

            // convert JSON array to list of Book instances
            List<Book> loaded = mapper.convertValue(array, new TypeReference<>() {});
            books.clear();
            books.addAll(loaded);

        } catch (Exception e) {
            System.err.println("Error loading books.json: " + e.getMessage());
            // attempt to initialize an empty file to avoid repeated errors
            try {
                saveToFile();
            } catch (Exception ignore) {}
        }
    }

    /**
     * Saves the in-memory book list to disk and ensures each object contains
     * the "mediaType" discriminator required by Jackson polymorphic deserialization.
     */
    public static void saveToFile() {
        try {
            // Ensure every saved object includes the mediaType property
            ArrayNode array = mapper.createArrayNode();
            for (Book b : books) {
                ObjectNode obj = mapper.convertValue(b, ObjectNode.class);
                // set mediaType explicitly to be safe
                obj.put("mediaType", "BOOK");
                array.add(obj);
            }
            mapper.writerWithDefaultPrettyPrinter().writeValue(FILE, array);
        } catch (Exception e) {
            System.err.println("Error saving books.json: " + e.getMessage());
        }
    }

    /**
     * Adds a book and persists the repository.
     *
     * @param book the book to add
     */
    public static void addBook(Book book) {
        books.add(book);
        saveToFile();
    }

    // -------- SEARCH METHODS --------

    /**
     * Finds books whose title contains the given substring (case-insensitive).
     *
     * @param title substring to search for
     * @return list of matching Book instances (empty if none)
     */
    public static List<Book> findByTitle(String title) {
        if (title == null) return new ArrayList<>();
        title = title.toLowerCase();
        List<Book> result = new ArrayList<>();

        for (Book b : books) {
            if (b.getTitle().toLowerCase().contains(title)) {
                result.add(b);
            }
        }
        return result;
    }

    /**
     * Finds books whose author contains the given substring (case-insensitive).
     *
     * @param author substring to search for
     * @return list of matching Book instances (empty if none)
     */
    public static List<Book> findByAuthor(String author) {
        if (author == null) return new ArrayList<>();
        author = author.toLowerCase();
        List<Book> result = new ArrayList<>();

        for (Book b : books) {
            if (b.getAuthor().toLowerCase().contains(author)) {
                result.add(b);
            }
        }
        return result;
    }

    /**
     * Finds books whose ISBN contains the given substring (case-insensitive).
     *
     * @param isbn substring to search for
     * @return list of matching Book instances (empty if none)
     */
    public static List<Book> findByIsbn(String isbn) {
        if (isbn == null) return new ArrayList<>();
        String q = isbn.toLowerCase().trim();
        List<Book> result = new ArrayList<>();

        for (Book b : books) {
            String bi = b.getIsbn() == null ? "" : b.getIsbn().toLowerCase();
            if (bi.contains(q)) {
                result.add(b);
            }
        }
        return result;
    }

    /**
     * Returns all books in memory (defensive copy of the internal list).
     *
     * @return list of all Book instances currently held in the repository
     */
    public static List<Book> getAll() {
        return new ArrayList<>(books);
    }

    /**
     * Clears all books from the repository and persists the empty state (for testing).
     */
    public static void clear() {
        books.clear();
        saveToFile();
    }
}
