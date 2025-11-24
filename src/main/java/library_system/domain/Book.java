package library_system.domain;

/**
 * Represents a book that can be stored and borrowed in the library.
 */
public class Book {

    /** Title of the book. */
    private String title;

    /** Author of the book. */
    private String author;

    /** Unique ISBN identifier of the book. */
    private String isbn;

    /** Indicates whether the book is currently borrowed. */
    private boolean borrowed = false;

    /**
     * Creates a new book instance.
     *
     * @param title  title of the book.
     * @param author author of the book.
     * @param isbn   unique ISBN of the book.
     */
    public Book(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
    }

    /**
     * @return book title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return book author.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @return book ISBN.
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * @return true if the book is currently borrowed; false otherwise.
     */
    public boolean isBorrowed() {
        return borrowed;
    }

    /**
     * Updates the borrowed status of the book.
     *
     * @param borrowed true if the book is now borrowed; false otherwise.
     */
    public void setBorrowed(boolean borrowed) {
        this.borrowed = borrowed;
    }
}
