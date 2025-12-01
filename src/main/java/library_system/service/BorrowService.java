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

  //  private final OverdueReportService overdueReportService = new OverdueReportService();
    /**
     * @param user the user attempting to borrow
     * @return true if allowed, false otherwise
     */
    public boolean canBorrow(User user) {


        if (user.getFineBalance() > 0) {
            return false;
        }
        if (LoanRepository.hasActiveLoans(user)) {
            return false;
        }
        if (LoanRepository.hasOverdueLoans(user)) {
            return false;
        }

        return true;
    }

    /**
     * @param user the user borrowing the item
     * @param item the media item (Book/CD)
     * @return true if borrow succeeds, false otherwise
     */
    private boolean borrow(User user, Media item) {

        if (!canBorrow(user)) return false;
        if ( item == null || item.isBorrowed() ) return false;

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

        if (borrow(user, book)) {
            System.out.println("Book borrowed");
            return true;
        }

        System.out.println("Book borrow failed");
        return false;
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

        if (borrow(user, cd)) {
            System.out.println("CD borrowed");
            return true;
        }

        System.out.println("CD borrow failed");
        return false;
    }

}
