package library_system.service;

import library_system.Repository.BookRepository;
import library_system.Repository.CDRepository;
import library_system.Repository.LoanRepository;
import library_system.domain.*;

import java.util.List;

/**
 * Service handling borrowing logic for books and CDs.
 */
public class BorrowService {

    // No fixed maximum enforced here; business rules are enforced via LoanRepository checks.

    /**
     * Checks whether the user is allowed to borrow a new item.
     *
     * Conditions:
     * - No unpaid fines
     * - No overdue loans
     * - No active loans
     *
     * @param user the user attempting to borrow
     * @return true if allowed
     */
    public boolean canBorrow(User user) {
        if (user == null) return false;
        if (user.getFineBalance() > 0) return false;
        // disallow borrow if user has any active loans or any overdue loans
        if (LoanRepository.hasActiveLoans(user)) return false;
        if (LoanRepository.hasOverdueLoans(user)) return false;
        return true;
    }

    /**
     * Internal borrowing method used by both book and CD.
     * Operates on repository-held instances to preserve identity.
     *
     * @param user the user borrowing
     * @param item the media item (may be an instance returned by search)
     * @return true if borrowing succeeded
     */
    private boolean borrow(User user, Media item) {

        if (!canBorrow(user)) return false;
        if (item == null) return false;

        // Ensure we operate on the repository-held instance so reference equality is preserved
        Media repoItem = item;
        if (item instanceof Book) {
            Book wanted = (Book) item;
            for (Book b : BookRepository.getAll()) {
                if (b.getIsbn() != null && wanted.getIsbn() != null && b.getIsbn().equalsIgnoreCase(wanted.getIsbn())) {
                    repoItem = b;
                    break;
                }
                if (b.getTitle() != null && wanted.getTitle() != null && b.getTitle().equalsIgnoreCase(wanted.getTitle())
                        && b.getAuthor() != null && wanted.getAuthor() != null && b.getAuthor().equalsIgnoreCase(wanted.getAuthor())) {
                    repoItem = b;
                    break;
                }
            }
        } else if (item instanceof CD) {
            CD wanted = (CD) item;
            for (CD c : CDRepository.getAll()) {
                if (c.getTitle() != null && wanted.getTitle() != null && c.getTitle().equalsIgnoreCase(wanted.getTitle())
                        && c.getArtist() != null && wanted.getArtist() != null && c.getArtist().equalsIgnoreCase(wanted.getArtist())) {
                    repoItem = c;
                    break;
                }
            }
        }

        if (repoItem.isBorrowed()) return false;

        // mark repository item as borrowed and persist changes
        repoItem.setBorrowed(true);
        if (repoItem instanceof Book) {
            BookRepository.saveToFile();
        } else if (repoItem instanceof CD) {
            CDRepository.saveToFile();
        }

        Loan loan = new Loan(user, repoItem);
        LoanRepository.addLoan(loan);

        return true;
    }

    /**
     * Attempts to borrow a book by searching with title.
     *
     * @param user user borrowing
     * @param title book title search string
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

    /**
     * Attempts to borrow a CD by title search.
     *
     * @param user user borrowing
     * @param title CD title search string
     * @return true if successful
     */
    public boolean borrowCD(User user, String title) {

        List<CD> list = CDRepository.findByTitle(title);
        CD cd = list.stream()
                .filter(c -> !c.isBorrowed())
                .findFirst()
                .orElse(null);

        return borrow(user, cd);
    }

    /**
     * Attempts to borrow the specified Book instance (repository-held or search result).
     *
     * @param user user borrowing the book
     * @param book book instance selected by the user
     * @return true if borrowing succeeded
     */
    public boolean borrowBookInstance(User user, Book book) {
        return borrow(user, book);
    }

    /**
     * Attempts to borrow the specified CD instance (repository-held or search result).
     *
     * @param user user borrowing the CD
     * @param cd CD instance selected by the user
     * @return true if borrowing succeeded
     */
    public boolean borrowCDInstance(User user, CD cd) {
        return borrow(user, cd);
    }

    /**
     * Returns a borrowed item for the user. Marks the associated loan returned
     * and sets the media 'borrowed' flag to false, persisting both changes.
     *
     * @param user the user returning the item
     * @param item the media item to return (Book or CD)
     * @return true if return succeeded (an active loan was found and updated)
     */
    public boolean returnItem(User user, Media item) {
        if (user == null || item == null) return false;

        Loan loan = LoanRepository.findActiveLoan(user, item);
        if (loan == null) return false;

        // Update repository-held media instance
        Media repoItem = loan.getItem();
        if (repoItem == null) return false;
        repoItem.setBorrowed(false);
        if (repoItem instanceof Book) BookRepository.saveToFile();
        if (repoItem instanceof CD) CDRepository.saveToFile();

        // Mark loan returned
        LoanRepository.markLoanReturned(loan);
        return true;
    }
}
