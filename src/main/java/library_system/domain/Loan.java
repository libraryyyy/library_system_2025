package library_system.domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Loan {

    private User user;
    private Media item;
    private LocalDate borrowedDate;
    private LocalDate dueDate;
    private boolean returned;


    public Loan() {
    }
    /**
     * Creates a new loan starting today.
     *
     * @param user borrowing user.
     * @param item media item (Book or CD).
     */
    public Loan(User user, Media item) {
        this.user = user;
        this.item = item;
        this.borrowedDate = LocalDate.now();
        this.dueDate = borrowedDate.plusDays(item.getBorrowDuration());
        this.returned = false;
    }

    public void setBorrowedDate(LocalDate date) {
        this.borrowedDate = date;
        this.dueDate = borrowedDate.plusDays(item.getBorrowDuration()); // 🔥 إصلاح ضروري
    }

    public void setDueDate(LocalDate date) {
        this.dueDate = date;
    }

    public void markReturned() {
        this.returned = true;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public Media getItem() {
        return item;
    }
    public void setItem(Media item) {
        this.item = item;
    }

    public LocalDate getBorrowedDate() {
        return borrowedDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }
    /**
     * @return true if the current date is after the due date.
     */
    public boolean isOverdue() {
        return LocalDate.now().isAfter(dueDate);
    }
    /**
     * @return number of overdue days (0 if not overdue).
     */
    public int getOverdueDays() {
        if (!isOverdue()) return 0;
        return (int) ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }
    /**
     * Calculates fine for this loan based on media type and overdue days.
     *
     * @return fine in NIS.
     */
    public int calculateFine() {
        return item.getFineStrategy().calculateFine(getOverdueDays());
    }
}
