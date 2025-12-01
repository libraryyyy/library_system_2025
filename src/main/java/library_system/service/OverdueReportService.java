package library_system.service;

import library_system.Repository.LoanRepository;
import library_system.domain.*;

import java.util.ArrayList;
import java.util.List;


public class OverdueReportService {

    /** Fine calculator used to compute fines for individual loans. */
    private final FineCalculatorService fineCalculator = new FineCalculatorService();

    /**
     * Builds an overdue report for the given user.
     *
     * @param user the user for whom the report is generated
     * @return an {@link OverdueReport} containing:
     */
    public OverdueReport generateReport(User user) {

        List<Loan> userLoans = LoanRepository.getUserLoans(user.getUsername());
        List<Loan> overdueItems = new ArrayList<>();

        int totalFine = 0;
        int booksCount = 0;
        int cdsCount = 0;

        for (Loan loan : userLoans) {

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
