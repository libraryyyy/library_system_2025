package library_system.domain;

public class Book extends Media {
    private String author;
    private String isbn;

    public Book(String title, String author, String isbn) {
        super(title);
        this.author = author;
        this.isbn = isbn;
    }

    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }

    @Override
    public int getBorrowDuration() {
        return 28;
    }

    @Override
    public FineStrategy getFineStrategy() {
        return new BookFineStrategy();
    }
}
