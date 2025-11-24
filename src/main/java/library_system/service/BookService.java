package library_system.service;

import library_system.Repository.BookRepository;
import library_system.domain.Book;

import java.util.List;

/**
 * Service that encapsulates business logic related to books.
 */
public class BookService {

    /**
     * Adds a new book to the system.
     *
     * @param book book to add.
     */
    public void addBook(Book book) {
        BookRepository.addBook(book);
    }

    /**
     * Searches for books by title.
     *
     * @param title title to search for.
     * @return list of matching books.
     */
    public List<Book> searchByTitle(String title) {
        return BookRepository.findByTitle(title);
    }

    /**
     * Searches for books by author.
     *
     * @param author author name to search for.
     * @return list of matching books.
     */
    public List<Book> searchByAuthor(String author) {
        return BookRepository.findByAuthor(author);
    }

    /**
     * Searches for books by ISBN.
     *
     * @param isbn ISBN to search for.
     * @return list of matching books.
     */
    public List<Book> searchByIsbn(String isbn) {
        return BookRepository.findByIsbn(isbn);
    }
}
