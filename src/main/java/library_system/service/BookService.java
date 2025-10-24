package library_system.service;

import library_system.domain.Book;
import java.util.ArrayList;
import java.util.List;

public class BookService {
    private List<Book> books = new ArrayList<>();

    public void addBook(Book book) {
        books.add(book);
    }

    public List<Book> searchByTitle(String title) {
        List<Book> result = new ArrayList<>();
        for (Book b : books) {
            if (b.getTitle().equalsIgnoreCase(title)) result.add(b);
        }
        return result;
    }

    public List<Book> searchByAuthor(String author) {
        List<Book> result = new ArrayList<>();
        for (Book b : books) {
            if (b.getAuthor().equalsIgnoreCase(author)) result.add(b);
        }
        return result;
    }

    public List<Book> searchByIsbn(String isbn) {
        List<Book> result = new ArrayList<>();
        for (Book b : books) {
            if (b.getIsbn().equals(isbn)) result.add(b);
        }
        return result;
    }
}
