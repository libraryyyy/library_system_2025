package library_system.domain;

import java.util.List;

/**
 * Represents a summary of overdue items and total fines for a user.
 */
public class OverdueReport {

    private List<Loan> overdueLoans;
    private int totalFine;
    private int overdueBooks;
    private int overdueCDs;

    public OverdueReport(List<Loan> overdueLoans, int totalFine, int overdueBooks, int overdueCDs) {
        this.overdueLoans = overdueLoans;
        this.totalFine = totalFine;
        this.overdueBooks = overdueBooks;
        this.overdueCDs = overdueCDs;
    }

    public List<Loan> getOverdueLoans() { return overdueLoans; }

    public int getTotalFine() { return totalFine; }

    public int getOverdueBooks() { return overdueBooks; }

    public int getOverdueCDs() { return overdueCDs; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Overdue Report ---\n");
        sb.append("Books overdue: ").append(overdueBooks).append("\n");
        sb.append("CDs overdue: ").append(overdueCDs).append("\n");
        sb.append("Total fine: ").append(totalFine).append(" NIS\n\n");
        sb.append("Details:\n");

        for (Loan loan : overdueLoans) {
            sb.append("- ").append(loan.getItem().getTitle())
                    .append(" | Days late: ").append(loan.getOverdueDays())
                    .append(" | Fine: ").append(loan.calculateFine()).append(" NIS\n");
        }

        return sb.toString();
    }
}
