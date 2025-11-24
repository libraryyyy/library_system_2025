package library_system.domain;

import java.time.LocalDate;

/**
 * Represents a loan of a single book by a user.
 * Stores borrowed and due dates and can determine if it is overdue.
 */
public class Loan {

    /** User who borrowed the book. */
    private User user;

    /** Borrowed book. */
    private Book book;

    /** Date when the book was borrowed. */
    private LocalDate borrowedDate;

    /** Due date for returning the book (borrowedDate + 28 days). */
    private LocalDate dueDate;

    /**
     * Creates a new loan for the given user and book.
     * The due date is automatically set to 28 days after the borrowed date.
     *
     * @param user user who borrows the book.
     * @param book book being borrowed.
     */
    public Loan(User user, Book book) {
        this.user = user;
        this.book = book;
        this.borrowedDate = LocalDate.now();
        this.dueDate = borrowedDate.plusDays(0);
    }

    /**
     * @return the user who borrowed the book.
     */
    public User getUser() {
        return user;
    }

    /**
     * @return the borrowed book.
     */
    public Book getBook() {
        return book;
    }

    /**
     * @return date when the book was borrowed.
     */
    public LocalDate getBorrowedDate() {
        return borrowedDate;
    }

    /**
     * @return due date for the loan.
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Checks whether this loan is overdue based on the current system date.
     *
     * @return true if the current date is after the due date; false otherwise.
     */
    public boolean isOverdue() {
        return LocalDate.now().isAfter(dueDate);
    }
}
