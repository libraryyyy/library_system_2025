package library_system.domain;

public class Book {
    private String title;
    private String author;
    private String isbn;
    private boolean borrowed = false;

    public Book(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
    }

    // Getters & Setters
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public boolean isBorrowed() { return borrowed; }
    public void setBorrowed(boolean borrowed) { this.borrowed = borrowed; }
}
