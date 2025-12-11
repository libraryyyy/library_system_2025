package library_system.service;

import library_system.repository.BookRepository;
import library_system.domain.Book;

import java.util.List;

public class BookService {

    public void addBook(Book book) {
        if (book == null) return;

        // Validate required fields
        if (book.getTitle() == null || book.getTitle().isBlank() || book.getAuthor() == null || book.getAuthor().isBlank() || book.getIsbn() == null || book.getIsbn().isBlank()) {
            System.out.println("Error: title, author and ISBN are required and must not be blank.");
            return;
        }

        // Uniqueness checks
        if (BookRepository.existsByTitle(book.getTitle()) || BookRepository.existsByIsbn(book.getIsbn())) {
            System.out.println("A book with this title or ISBN already exists.");
            return;
        }

        BookRepository.addBook(book);
    }

    public List<Book> searchByTitle(String title) {
        return BookRepository.findByTitle(title);
    }

    public List<Book> searchByAuthor(String author) {
        return BookRepository.findByAuthor(author);
    }

    public List<Book> searchByIsbn(String isbn) {
        return BookRepository.findByIsbn(isbn);
    }
}
