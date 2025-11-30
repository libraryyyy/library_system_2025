package library_system.service;

import library_system.Repository.BookRepository;
import library_system.Repository.CDRepository;
import library_system.Repository.LoanRepository;
import library_system.domain.*;

import java.util.List;
/**

 *     Sprint 2: Borrow items + due dates</li>
 *    Sprint 4: Borrow restrictions (fines + overdue items)</li>
 *    Sprint 5: Media polymorphism (Book/CD via Media)</li>
 */
public class BorrowService {

    private final OverdueReportService overdueReportService = new OverdueReportService();
    /**
     * @param user the user attempting to borrow
     * @return true if allowed, false otherwise
     */
    public boolean canBorrow(User user) {


        if (user == null) return false;
        if (user.getFineBalance() > 0) {
            return false;
        }

        OverdueReport report = overdueReportService.generateReport(user);
        return report.getOverdueLoans().isEmpty();
    }

    /**
     * @param user the user borrowing the item
     * @param item the media item (Book/CD)
     * @return true if borrow succeeds, false otherwise
     */
    private boolean borrow(User user, Media item) {

        if (user == null || item == null) return false;
        if (!canBorrow(user)) return false;
        if ( item.isBorrowed()) return false;

        Loan loan = new Loan(user, item);
        LoanRepository.addLoan(loan);

        item.setBorrowed(true);
        return true;
    }
    /**
     * Attempts to borrow a book by title.
     *
     * @param user  the user borrowing
     * @param title book title
     * @return true if successful
     */
    public boolean borrowBook(User user, String title) {

        List<Book> list = BookRepository.findByTitle(title);

        Book book = list.stream()
                .filter(b -> !b.isBorrowed())
                .findFirst()
                .orElse(null);

        return borrow(user, book);
    }

    // ----------------------------------------------------
    // Borrow CD
    // ----------------------------------------------------

    /**
     * Attempts to borrow a CD by title.
     *
     * @param user  user borrowing
     * @param title CD title
     * @return true if successful
     */

    public boolean borrowCD(User user, String title) {

        List<CD> list = CDRepository.findByTitle(title);
        CD cd = list.stream().filter(c -> !c.isBorrowed()).findFirst().orElse(null);

        return borrow(user, cd);
    }

}
