package library_system.domain;

import java.time.LocalDate;

/**
 * Represents a loan of any media item (Book, CD, etc.).
 */
public class Loan {

    private User user;
    private Media item;
    private LocalDate borrowedDate;
    private LocalDate dueDate;

    public Loan(User user, Media item) {
        this.user = user;
        this.item = item;
        this.borrowedDate = LocalDate.now();
        this.dueDate = borrowedDate.plusDays(item.getBorrowDuration());
    }

    public User getUser() { return user; }

    public Media getItem() { return item; }

    public LocalDate getBorrowedDate() { return borrowedDate; }

    public LocalDate getDueDate() { return dueDate; }

    public boolean isOverdue() {
        return LocalDate.now().isAfter(dueDate);
    }

    public int getOverdueDays() {
        if (!isOverdue()) return 0;
        return (int) java.time.temporal.ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }

    public int calculateFine() {
        return item.getFineStrategy().calculateFine(getOverdueDays());
    }
}
