package library_system.service;

import library_system.domain.Book;
import library_system.repository.BookRepository;

import java.util.List;

public class BookService {

    public void addBook(Book book) {
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
