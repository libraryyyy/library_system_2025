package library_system.service;

import library_system.Repository.BookRepository;
import library_system.Repository.LoanRepository;
import library_system.domain.Book;
import library_system.domain.Loan;
import library_system.domain.User;

import java.util.List;

/**
 * Service that handles borrowing operations.
 */
public class BorrowService {

    /**
     * Attempts to borrow the first available (not borrowed) book with the given title
     * for the specified user.
     *
     * @param user  user who wants to borrow the book.
     * @param title title of the book to borrow.
     * @return true if the book is found and borrowed; false otherwise.
     */
    public boolean borrow(User user, String title) {
        Book book = null;
        List<Book> candidates = BookRepository.findByTitle(title);

        for (Book b : candidates) {
            if (!b.isBorrowed()) {
                book = b;
                break;
            }
        }

        if (book == null) {
            return false;
        }

        Loan loan = new Loan(user, book);
        LoanRepository.addLoan(loan);
        book.setBorrowed(true);
        return true;
    }
}
