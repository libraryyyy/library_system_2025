package library_system.domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Loan {

    private User user;
    private Media item;
    private LocalDate borrowedDate;
    private LocalDate dueDate;
    private boolean returned;

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

    public Media getItem() {
        return item;
    }

    public LocalDate getBorrowedDate() {
        return borrowedDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public boolean isOverdue() {
        return LocalDate.now().isAfter(dueDate);
    }

    public int getOverdueDays() {
        if (!isOverdue()) return 0;
        return (int) ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }

    public int calculateFine() {
        return item.getFineStrategy().calculateFine(getOverdueDays());
    }
}
