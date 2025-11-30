package library_system.domain;

import java.util.List;


public class OverdueReport {

    private List<Loan> overdueLoans;
    private int totalFine;
    private int overdueBooks;
    private int overdueCDs;

    public OverdueReport() {
    }

    public OverdueReport(List<Loan> overdueLoans, int totalFine, int overdueBooks, int overdueCDs) {
        this.overdueLoans = overdueLoans;
        this.totalFine = totalFine;
        this.overdueBooks = overdueBooks;
        this.overdueCDs = overdueCDs;
    }

    public List<Loan> getOverdueLoans() { return overdueLoans; }
    public void setOverdueLoans(List<Loan> overdueLoans) {
        this.overdueLoans = overdueLoans;
    }
    public int getTotalFine() { return totalFine; }
    public void setTotalFine(int totalFine) {
        this.totalFine = totalFine;
    }
    public int getOverdueBooks() { return overdueBooks; }
    public void setOverdueBooks(int overdueBooks) {
        this.overdueBooks = overdueBooks;
    }
    public int getOverdueCDs() { return overdueCDs; }
    public void setOverdueCDs(int overdueCDs) {
        this.overdueCDs = overdueCDs;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Overdue Report ---\n");
        sb.append("Books overdue: ").append(overdueBooks).append("\n");
        sb.append("CDs overdue: ").append(overdueCDs).append("\n");
        sb.append("Total fine: ").append(totalFine).append(" NIS\n\n");
        sb.append("Details:\n");
        if (overdueLoans != null) {
        for (Loan loan : overdueLoans) {
            sb.append("- ").append(loan.getItem().getTitle())
                    .append(" | Days late: ").append(loan.getOverdueDays())
                    .append(" | Fine: ").append(loan.calculateFine()).append(" NIS\n");
        }
        }

        return sb.toString();
    }
}
