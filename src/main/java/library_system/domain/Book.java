package library_system.domain;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Book extends Media {
    private String author;
    private String isbn;

    public Book() {
        super();
    }

    /**
     * Creates a new Book instance.
     *
     * @param title  book title.
     * @param author book author.
     * @param isbn   book ISBN.
     */
    public Book(String title, String author, String isbn) {
        super(title);
        this.author = author;
        this.isbn = isbn;
    }

    /**
     * @return author name.
     */
    public String getAuthor() { return author; }
    /**
     * Sets author name (for JSON).
     */
    public void setAuthor(String author) {
        this.author = author;
    }
    /**
     * @return ISBN.
     */
    public String getIsbn() { return isbn; }
    /**
     * Sets ISBN (for JSON).
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    @Override
    public int getBorrowDuration() {
        return 28;
    }
    /**
     * Uses {@link BookFineStrategy} for fine calculation.
     */
    @JsonIgnore
    @Override
    public FineStrategy getFineStrategy() {
        return new BookFineStrategy();
    }
    @Override
    public String toString() {
        return "Book: " + title + " (" + author + ")";
    }
}
