package library_system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Represents a loan (a user borrowing a media item).
 * <p>
 * This class stores the borrowing user, the borrowed media item, borrowed/due dates,
 * and the returned flag. It provides helpers to compute overdue days and fines.
 * </p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Loan {

    /** The user who borrowed the item. */
    private User user;

    /**
     * The borrowed media item (Book or CD). Jackson type info ensures correct polymorphic JSON loading.
     */
    private Media item;


    /** The date the item was borrowed. */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate borrowedDate;

    /** The due date calculated based on media borrow duration. */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate dueDate;

    /** Whether the media has been returned by the user. */
    private boolean returned;

    /** Whether the fine for this loan has been paid. Defaults to false. */
    private boolean finePaid = false;

    /** Fine amount recorded for this loan (e.g., set on return when a fine is charged). In NIS. */
    private int fineAmount = 0;

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
        // Due date is borrowedDate + borrowDuration days. The item becomes overdue
        // when current date is after the due date (i.e., on day borrowDuration+1).
        this.dueDate = borrowedDate.plusDays(item != null ? item.getBorrowDuration() : 0);
      // this.dueDate = borrowedDate.minusDays(1);

        this.returned = false;
    }

    /**
     * Returns the borrowing user.
     *
     * @return the user who borrowed the item
     */
    public User getUser() {
        return user;
    }

    /**
     * Returns the borrowed media item.
     *
     * @return the media item (Book or CD)
     */
    public Media getItem() {
        return item;
    }

    /**
     * Returns the date the item was borrowed.
     *
     * @return borrowed date (LocalDate)
     */
    public LocalDate getBorrowedDate() {
        return borrowedDate;
    }

    /**
     * Returns the date the item is due.
     *
     * @return due date (LocalDate)
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Returns whether the media was returned.
     *
     * @return true if returned
     */
    public boolean isReturned() {
        return returned;
    }

    /**
     * Sets the borrowing user (used by deserialization).
     *
     * @param user the user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Sets the borrowed media item (used by deserialization).
     *
     * @param item the media item
     */
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

    /**
     * Sets the due date (mainly for testing/JSON).
     *
     * @param date new due date
     */
    public void setDueDate(LocalDate date) {
        this.dueDate = date;
    }

    /**
     * Sets the return status of this loan.
     *
     * @param returned true when returned
     */
    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    /**
     * Marks the loan as returned.
     */
    public void markReturned() {
        this.returned = true;
    }

    /**
     * Whether the fine for this loan was paid.
     * Stored in JSON as `finePaid`.
     */
    public boolean isFinePaid() {
        return finePaid;
    }

    public void setFinePaid(boolean finePaid) {
        this.finePaid = finePaid;
    }

    public int getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(int fineAmount) {
        this.fineAmount = Math.max(0, fineAmount);
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
