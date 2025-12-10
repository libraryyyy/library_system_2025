package library_system.service;

import library_system.Repository.LoanRepository;
import library_system.domain.*;
import library_system.Repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Service that builds an overdue report for a specific user.
 *
 * The report contains the list of overdue loans for the user, the total fine
 * amount for those overdue loans, and counts of overdue books and CDs.
 */
public class OverdueReportService {

    /** Fine calculator used to compute fines for individual loans. */
    private final FineCalculatorService fineCalculator = new FineCalculatorService();

    /**
     * Builds an overdue report for the given user.
     *
     * @param user the user for whom the report is generated
     * @return an {@link OverdueReport} containing overdue loans, total fine and counts
     */
    public OverdueReport generateReport(User user) {

        List<Loan> userLoans = LoanRepository.getUserLoans(user.getUsername());
        List<Loan> overdueItems = new ArrayList<>();

        int totalFine = 0;
        int booksCount = 0;
        int cdsCount = 0;

        for (Loan loan : userLoans) {

            // Consider loans that have unpaid fines:
            // - currently overdue and fine not paid
            // - OR already returned but a fine amount was recorded and not paid
            if (loan.isFinePaid()) continue;
            boolean hasOutstanding = loan.isOverdue() || (loan.isReturned() && loan.getFineAmount() > 0);
            if (!hasOutstanding) continue;

            overdueItems.add(loan);

            int fine = fineCalculator.calculateFine(loan);
            // If the loan already has a recorded fineAmount (e.g., on return), prefer that value
            if (loan.getFineAmount() > 0) fine = loan.getFineAmount();
             totalFine += fine;

            Media item = loan.getItem();
            if (item instanceof Book) booksCount++;
            if (item instanceof CD) cdsCount++;
        }

        // The overdue report should not overwrite the user's persistent fineBalance
        // which reflects payments. It only displays unpaid outstanding fines.

        return new OverdueReport(overdueItems, totalFine, booksCount, cdsCount);
    }
}
