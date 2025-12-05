package library_system.domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Loan {

    /** The user who borrowed the item. */
    private User user;

    /**
     * The borrowed media item (Book or CD).
     * Jackson type info ensures correct polymorphic JSON loading.
     */
    private Media item;

    /** The date the item was borrowed. */
    private LocalDate borrowedDate;

    /** The due date calculated based on media borrow duration. */
    private LocalDate dueDate;

    /** Whether the media has been returned by the user. */
    private boolean returned;

    /** Default constructor for JSON deserialization. */
    public Loan() {}

    /**
     * Creates a new loan beginning today.
     *
     * @param user the user borrowing the item
     * @param item the media item (Book or CD)
     */
    public Loan(User user, Media item) {
        this.user = user;
        this.item = item;
        this.borrowedDate = LocalDate.now();
        this.dueDate = borrowedDate.plusDays(item.getBorrowDuration());
        this.returned = false;
    }

    /** @return the borrowing user */
    public User getUser() {
        return user;
    }

    /** @return the borrowed media item */
    public Media getItem() {
        return item;
    }

    /** @return the date the item was borrowed */
    public LocalDate getBorrowedDate() {
        return borrowedDate;
    }

    /** @return the date the item is due */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /** @return true if the media was returned */
    public boolean isReturned() {
        return returned;
    }

    /** @param user sets the borrowing user */
    public void setUser(User user) {
        this.user = user;
    }

    /** @param item sets the borrowed media item */
    public void setItem(Media item) {
        this.item = item;
    }

    /**
     * Sets the borrowed date and automatically recalculates due date.
     *
     * @param date new borrowed date
     */
    public void setBorrowedDate(LocalDate date) {
        this.borrowedDate = date;
        if (item != null) {
            this.dueDate = borrowedDate.plusDays(item.getBorrowDuration());
        } else {
            this.dueDate = null;
        }
    }

    /** @param date new due date (mainly for testing/JSON) */
    public void setDueDate(LocalDate date) {
        this.dueDate = date;
    }

    /** @param returned sets return status */
    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    /** Marks the loan as returned. */
    public void markReturned() {
        this.returned = true;
    }

    /**
     * Determines whether the loan is overdue based on current date.
     *
     * @return true if overdue and not returned
     */
    public boolean isOverdue() {

            return !returned && dueDate != null && LocalDate.now().isAfter(dueDate);

    }

    /**
     * Calculates the number of overdue days.
     *
     * @return number of overdue days, or 0 if not overdue
     */
    public int getOverdueDays() {
        if (!isOverdue() || dueDate == null) return 0;
        return (int) ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }

    /**
     * Calculates the fine based on the media type and overdue days.
     *
     * @return fine amount in NIS
     */
    public int calculateFine() {
        if (item == null) return 0;
        FineStrategy strategy = item.getFineStrategy();
        if (strategy == null) return 0;
        return strategy.calculateFine(getOverdueDays());
    }
}
