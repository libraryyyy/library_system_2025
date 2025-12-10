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

    @JsonProperty("quantity")
    public int getQuantity() {
        return super.getQuantity();
    }

    @JsonProperty("quantity")
    public void setQuantity(int q) {
        super.setQuantity(q);
    }

    @Override
    public int getBorrowDuration() {
        // Books are borrowed for 28 days (become overdue starting day 29)
        return 28;
    }

    @Override
    @JsonIgnore  // Important: do not serialize fine strategy into JSON
    public FineStrategy getFineStrategy() {
        return new BookFineStrategy();
    }
}