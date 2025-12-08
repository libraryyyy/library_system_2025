package library_system.service;

import library_system.Repository.LoanRepository;
import library_system.domain.*;

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

            // skip returned or non-overdue loans
            if (loan.isReturned() || !loan.isOverdue())
                continue;

            overdueItems.add(loan);

            int fine = fineCalculator.calculateFine(loan);
            totalFine += fine;

            Media item = loan.getItem();
            if (item instanceof Book) booksCount++;
            if (item instanceof CD) cdsCount++;
        }

        return new OverdueReport(overdueItems, totalFine, booksCount, cdsCount);
    }
}
