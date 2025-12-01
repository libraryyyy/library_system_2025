package library_system.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Represents a book item in the library system.
 * Books are a type of {@link Media} and inherit its common attributes
 * such as ID, title, and availability status.
 *
 * <p>
 * Business rules (from project requirements):
 * <ul>
 *   <li>Borrow duration: 28 days</li>
 *   <li>Fine: 10 NIS per overdue day (via {@link BookFineStrategy})</li>
 * </ul>
 * </p>
 *
 * @author sana
 * @version 1.0
 */
@JsonTypeName("BOOK")
public class Book extends Media {

    /** The author of the book. */
    private String author;

    /** The ISBN identifier of the book. */
    private String isbn;

    /**
     * Default constructor required for JSON serialization/deserialization.
     */
    public Book() {
        super();
    }

    /**
     * Constructs a new Book with the given metadata.
     *
     * @param title  the title of the book
     * @param author the name of the book's author
     * @param isbn   the ISBN identifier
     */
    public Book(String title, String author, String isbn) {
        super(title);
        this.author = author;
        this.isbn = isbn;
    }

    /**
     * @return the author of the book
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Updates the author (required for JSON deserialization).
     *
     * @param author new author name
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return the ISBN code
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Updates the ISBN (required for JSON deserialization).
     *
     * @param isbn new ISBN value
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * {@inheritDoc}
     * Books are borrowed for 28 days.
     *
     * @return the number of days the book can be borrowed
     */
    @Override
    public int getBorrowDuration() {
        return 28;
    }

    /**
     * Returns the fine calculation strategy for books.
     * Books use {@link BookFineStrategy} which sets the fine rate to 10 NIS/day.
     *
     * @return BookFineStrategy instance
     */
    @Override
    @JsonIgnore
    public FineStrategy getFineStrategy() {
        return new BookFineStrategy();
    }

    /**
     * @return a readable string representation of the book
     */
    @Override
    public String toString() {
        return "Book: " + getTitle() + " (" + author + ")";
    }
}
