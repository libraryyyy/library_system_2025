package library_system.service;

import library_system.Repository.LoanRepository;
import library_system.domain.*;

import java.util.ArrayList;
import java.util.List;

public class OverdueReportService {

    private FineCalculatorService fineCalculator = new FineCalculatorService();

    public OverdueReport generateReport(User user) {

        List<Loan> userLoans = LoanRepository.getUserLoans(user.getUsername());
        List<Loan> overdueItems = new ArrayList<>();

        int totalFine = 0;
        int booksCount = 0;
        int cdsCount = 0;

        for (Loan loan : userLoans) {
            if (loan.isOverdue() && !loan.isReturned()) {

                overdueItems.add(loan);

                int fine = fineCalculator.calculateFine(loan);
                totalFine += fine;

                Media item = loan.getItem();

                if (item instanceof Book) booksCount++;
                if (item instanceof CD) cdsCount++;
            }
        }

        return new OverdueReport(overdueItems, totalFine, booksCount, cdsCount);
    }
}
