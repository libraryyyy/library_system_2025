package library_system.domain;

import com.fasterxml.jackson.annotation.*;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName("BOOK")
public class Book extends Media {

    private String author;
    private String isbn;

    public Book() { super(); }

    public Book(String title, String author, String isbn) {
        super(title);
        this.author = author;
        this.isbn = isbn;
    }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    @Override
    public int getBorrowDuration() {
        return 14;
    }

    @Override
    @JsonIgnore  // مهم عشان ما يتحفظش في الـ JSON
    public FineStrategy getFineStrategy() {
        return new BookFineStrategy();
    }
}