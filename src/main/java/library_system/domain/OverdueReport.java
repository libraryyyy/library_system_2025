package library_system.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user's overdue summary including:
 * <ul>
 *     <li>List of overdue loans</li>
 *     <li>Total accumulated fine</li>
 *     <li>Number of overdue books</li>
 *     <li>Number of overdue CDs</li>
 * </ul>
 */
public class OverdueReport {

    /** List of overdue loans for the user. */
    private List<Loan> overdueLoans;

    /** Total fine for all overdue items. */
    private int totalFine;

    /** Number of overdue books. */
    private int overdueBooks;

    /** Number of overdue CDs. */
    private int overdueCDs;

    /** Default constructor for JSON use. */
   // public OverdueReport() {}

    /**
     * Creates a new report instance with the given details.
     *
     * @param overdueLoans list of overdue loans
     * @param totalFine    total fine amount
     * @param overdueBooks number of overdue books
     * @param overdueCDs   number of overdue CDs
     */
    public OverdueReport(List<Loan> overdueLoans, int totalFine, int overdueBooks, int overdueCDs) {
        this.overdueLoans = overdueLoans;
        this.totalFine = totalFine;
        this.overdueBooks = overdueBooks;
        this.overdueCDs = overdueCDs;
    }

    public List<Loan> getOverdueLoans() {
        return overdueLoans;
    }

    public void setOverdueLoans(List<Loan> overdueLoans) {
      //  this.overdueLoans = overdueLoans;
    }

    public int getTotalFine() {
        return totalFine;
    }

    public void setTotalFine(int totalFine) {
  //      this.totalFine = totalFine;
    }

    public int getOverdueBooks() {
        return overdueBooks;
    }

    public void setOverdueBooks(int overdueBooks) {
   //     this.overdueBooks = overdueBooks;
    }

    public int getOverdueCDs() {
        return overdueCDs;
    }

  /*  public void setOverdueCDs(int overdueCDs) {
        this.overdueCDs = overdueCDs;
    }
    */


    /**
     * Creates a formatted string representation of this report.
     *
     * @return formatted overdue report
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("--- Overdue Report ---\n");
        sb.append("Books overdue: ").append(overdueBooks).append("\n");
        sb.append("CDs overdue: ").append(overdueCDs).append("\n");
        sb.append("Total fine: ").append(totalFine).append(" NIS\n\n");
        sb.append("Details:\n");

        List<Loan> list = (overdueLoans == null) ? new ArrayList<>() : overdueLoans;

        for (Loan loan : list) {
            sb.append("- ").append(loan.getItem().getTitle())
                    .append(" | Days late: ").append(loan.getOverdueDays())
                    .append(" | Fine: ").append(loan.calculateFine()).append(" NIS\n");
        }

        return sb.toString();
    }


}
