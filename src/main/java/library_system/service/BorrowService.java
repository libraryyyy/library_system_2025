package library_system.service;

import library_system.Repository.BookRepository;
import library_system.Repository.CDRepository;
import library_system.Repository.LoanRepository;
import library_system.domain.*;

import java.util.List;

public class BorrowService {

    private final OverdueReportService overdueReportService = new OverdueReportService();

    /**
     * Checks borrow restrictions:
     * - user must not have overdue items
     * - user must not have unpaid fines
     */
    private boolean canBorrow(User user) {
        // Unpaid fines
        if (user.getFineBalance() > 0) {
            System.out.println("❌ You cannot borrow because you have unpaid fines.");
            return false;
        }

        // Overdue loans
        OverdueReport report = overdueReportService.generateReport(user);
        if (!report.getOverdueLoans().isEmpty()) {
            System.out.println("❌ You cannot borrow because you have overdue items.");
            return false;
        }

        return true;
    }

    // -----------------------------------------
    // Borrow Book
    // -----------------------------------------
    public boolean borrowBook(User user, String title) {

        if (!canBorrow(user)) return false;

        List<Book> candidates = BookRepository.findByTitle(title);

        Book selected = null;
        for (Book b : candidates) {
            if (!b.isBorrowed()) {
                selected = b;
                break;
            }
        }

        if (selected == null) return false;

        Loan loan = new Loan(user, selected);
        LoanRepository.addLoan(loan);
        selected.setBorrowed(true);

        return true;
    }

    // -----------------------------------------
    // Borrow CD
    // -----------------------------------------
    public boolean borrowCD(User user, String title) {

        if (!canBorrow(user)) return false;

        List<CD> candidates = CDRepository.findByTitle(title);

        CD selected = null;
        for (CD cd : candidates) {
            if (!cd.isBorrowed()) {
                selected = cd;
                break;
            }
        }

        if (selected == null) return false;

        Loan loan = new Loan(user, selected);
        LoanRepository.addLoan(loan);
        selected.setBorrowed(true);

        return true;
    }
}
