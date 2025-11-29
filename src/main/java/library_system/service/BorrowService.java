package library_system.service;

import library_system.Repository.BookRepository;
import library_system.Repository.CDRepository;
import library_system.Repository.LoanRepository;
import library_system.domain.*;

import java.util.List;

public class BorrowService {

    private final OverdueReportService overdueReportService = new OverdueReportService();

    public boolean canBorrow(User user) {

        if (user.getFineBalance() > 0) {
            return false;
        }

        OverdueReport report = overdueReportService.generateReport(user);
        return report.getOverdueLoans().isEmpty();
    }

    // ----------------------------------------------------
    // 🔥 Polymorphic Borrow Function
    // ----------------------------------------------------
    private boolean borrow(User user, Media item) {

        if (!canBorrow(user)) return false;
        if (item == null || item.isBorrowed()) return false;

        Loan loan = new Loan(user, item);
        LoanRepository.addLoan(loan);

        item.setBorrowed(true);
        return true;
    }

    // ----------------------------------------------------
    // Borrow Book
    // ----------------------------------------------------
    public boolean borrowBook(User user, String title) {

        List<Book> list = BookRepository.findByTitle(title);
        Book book = list.stream().filter(b -> !b.isBorrowed()).findFirst().orElse(null);

        if (borrow(user, book)) {
            System.out.println("✔ Book borrowed");
            return true;
        }

        System.out.println("❌ Book borrow failed");
        return false;
    }

    // ----------------------------------------------------
    // Borrow CD
    // ----------------------------------------------------
    public boolean borrowCD(User user, String title) {

        List<CD> list = CDRepository.findByTitle(title);
        CD cd = list.stream().filter(c -> !c.isBorrowed()).findFirst().orElse(null);

        if (borrow(user, cd)) {
            System.out.println("✔ CD borrowed");
            return true;
        }

        System.out.println("❌ CD borrow failed");
        return false;
    }

}
