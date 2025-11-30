package library_system.service;

import library_system.Repository.LoanRepository;
import library_system.domain.*;

import java.util.ArrayList;
import java.util.List;

public class OverdueReportService {

    private final FineCalculatorService fineCalculator = new FineCalculatorService();
    /**

     * @param user the user whose overdue report should be generated
     * @return OverdueReport object containing fines + counts + list of overdue loans
     */
    public OverdueReport generateReport(User user) {

        List<Loan> userLoans = LoanRepository.getUserLoans(user.getUsername());
        List<Loan> overdueItems = new ArrayList<>();

        int totalFine = 0;
        int booksCount = 0;
        int cdsCount = 0;

        for (Loan loan : userLoans) {

            if (!loan.isOverdue() || loan.isReturned())
                continue;  // skip non-overdue or returned loans

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
