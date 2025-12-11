package library_system.repository;

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
 * Repository for managing Book persistence and in-memory list.
 */
public class BookRepository {

    private static final List<Book> books = new ArrayList<>();
    private static final String FILE_PATH = "src/main/resources/books.json";

    private static final ObjectMapper mapper = MapperProvider.MAPPER;

    /**
     * Loads books from file, auto-creating and repairing JSON entries when necessary.
     */
    public static void loadFromFile() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists() || file.length() == 0) {
                // ensure parent and empty file present
                FileUtil.ensureDataDirExists();
                mapper.writerWithDefaultPrettyPrinter().writeValue(file, List.of());
                books.clear();
                return;
            }

            JsonNode root = mapper.readTree(file);
            ArrayNode array;
            if (root == null || root.isNull()) {
                array = mapper.createArrayNode();
            } else if (root.isArray()) {
                array = (ArrayNode) root;
            } else {
                array = mapper.createArrayNode();
                array.add(root);
            }

            boolean fixed = false;
            ArrayNode cleaned = mapper.createArrayNode();
            for (int i = 0; i < array.size(); i++) {
                JsonNode node = array.get(i);
                if (node != null && node.isObject()) {
                    ObjectNode obj = (ObjectNode) node;
                    ObjectNode clean = mapper.createObjectNode();
                    // Ensure mediaType exists and is BOOK
                    if (!obj.has("mediaType") || obj.get("mediaType").isNull() || obj.get("mediaType").asText().isEmpty()) {
                        clean.put("mediaType", "BOOK");
                        fixed = true;
                    } else {
                        clean.put("mediaType", obj.get("mediaType").asText());
                    }
                    // title/author/isbn
                    if (obj.has("title")) clean.put("title", obj.get("title").asText());
                    if (obj.has("author")) clean.put("author", obj.get("author").asText());
                    if (obj.has("isbn")) clean.put("isbn", obj.get("isbn").asText());
                    // quantity - default to 1 if missing
                    if (obj.has("quantity") && obj.get("quantity").canConvertToInt()) clean.put("quantity", obj.get("quantity").asInt());
                    else { clean.put("quantity", 1); fixed = true; }
                    // borrowDuration
                    if (obj.has("borrowDuration")) clean.put("borrowDuration", obj.get("borrowDuration").asInt());

                    cleaned.add(clean);
                }
            }

            if (fixed) {
                // rewrite repaired and cleaned JSON
                mapper.writerWithDefaultPrettyPrinter().writeValue(file, cleaned);
            }

            List<Book> loaded = mapper.readValue(file, new TypeReference<List<Book>>() {});
            books.clear();
            books.addAll(loaded);

            // Ensure quantity >=0
            for (Book b : books) {
                if (b == null) continue;
                b.setQuantity(Math.max(0, b.getQuantity()));
            }

        } catch (Exception e) {
            System.err.println("Error loading books: " + e.getMessage());
            books.clear();
        }
    }

    /**
     * Saves books to disk (pretty printed) and ensures mediaType & quantity are persisted.
     * Writes a cleaned representation (no id, no borrowed flag).
     */
    public static void saveToFile() {
        try {
            // write cleaned array
            ArrayNode arr = mapper.createArrayNode();
            for (Book b : books) {
                ObjectNode obj = mapper.createObjectNode();
                obj.put("mediaType", "BOOK");
                if (b.getTitle() != null) obj.put("title", b.getTitle());
                if (b.getAuthor() != null) obj.put("author", b.getAuthor());
                if (b.getIsbn() != null) obj.put("isbn", b.getIsbn());
                obj.put("quantity", b.getQuantity());
                obj.put("borrowDuration", b.getBorrowDuration());
                arr.add(obj);
            }
            FileUtil.writeAtomic(new File(FILE_PATH), arr, mapper);
        } catch (Exception e) {
            System.err.println("Error saving books: " + e.getMessage());
        }
    }

    /**
     * Returns the live in-memory list of books. Modifying returned list will affect repo.
     *
     * @return live list of books
     */
    public static List<Book> getBooks() {
        return books;
    }

    /**
     * Alias for getBooks to preserve existing callers.
     *
     * @return live list
     */
    public static List<Book> getAll() { return books; }

    /**
     * Adds a new book and persists.
     *
     * @param b book to add
     */
    public static void addBook(Book b) {
        if (b == null) return;
        if (b.getQuantity() <= 0) b.setQuantity(1);
        books.add(b);
        saveToFile();
    }

    /**
     * Clears repository and saves.
     */
    public static void clear() {
        books.clear();
        saveToFile();
    }

    /**
     * Case-insensitive partial title search.
     *
     * @param part substring of title
     * @return matching books (live instances)
     */
    public static List<Book> findByTitleContaining(String part) {
        if (part == null || part.isBlank()) return new ArrayList<>(books);
        String lower = part.toLowerCase();
        List<Book> res = new ArrayList<>();
        for (Book b : books) {
            if (b.getTitle() != null && b.getTitle().toLowerCase().contains(lower)) res.add(b);
        }
        return res;
    }

    /**
     * Partial title match (case-insensitive).
     *
     * @param title substring to search for
     * @return matching books
     */
    public static List<Book> findByTitle(String title) {
        return findByTitleContaining(title);
    }

    /**
     * Case-insensitive partial author search.
     *
     * @param author substring of author
     * @return matching books
     */
    public static List<Book> findByAuthor(String author) {
        if (author == null) return new ArrayList<>();
        String lower = author.toLowerCase();
        List<Book> res = new ArrayList<>();
        for (Book b : books) if (b.getAuthor() != null && b.getAuthor().toLowerCase().contains(lower)) res.add(b);
        return res;
    }

    /**
     * Find by ISBN (partial, case-insensitive).
     *
     * @param isbn isbn or substring
     * @return matching books
     */
    public static List<Book> findByIsbn(String isbn) {
        if (isbn == null) return new ArrayList<>();
        String lower = isbn.toLowerCase();
        List<Book> res = new ArrayList<>();
        for (Book b : books) if (b.getIsbn() != null && b.getIsbn().toLowerCase().contains(lower)) res.add(b);
        return res;
    }

    /**
     * Check existence by exact title (case-insensitive, trimmed).
     * Used to enforce uniqueness when adding books.
     *
     * @param title title to check
     * @return true if a book with the same title exists
     */
    public static boolean existsByTitle(String title) {
        if (title == null) return false;
        String t = title.trim().toLowerCase();
        for (Book b : books) {
            if (b.getTitle() != null && b.getTitle().trim().toLowerCase().equals(t)) return true;
        }
        return false;
    }

    /**
     * Check existence by exact ISBN (case-insensitive, trimmed).
     * Used to enforce uniqueness when adding books.
     *
     * @param isbn isbn to check
     * @return true if a book with the same isbn exists
     */
    public static boolean existsByIsbn(String isbn) {
        if (isbn == null) return false;
        String s = isbn.trim().toLowerCase();
        for (Book b : books) {
            if (b.getIsbn() != null && b.getIsbn().trim().toLowerCase().equals(s)) return true;
        }
        return false;
    }
}
