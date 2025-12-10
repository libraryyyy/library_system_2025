package library_system.service;

import library_system.Repository.BookRepository;
import library_system.Repository.CDRepository;
import library_system.Repository.LoanRepository;
import library_system.domain.*;

/**
 * Service handling borrowing and returning logic for books and CDs.
 */
public class BorrowService {

    /**
     * Attempts to borrow a book instance (should be a repository-held instance or equivalent).
     *
     * @param user the borrowing user
     * @param book the book to borrow
     * @return true if borrow succeeded
     */
    public boolean borrowBookInstance(User user, Book book) {
        return borrowMedia(user, book);
    }

    /**
     * Attempts to borrow a CD instance (should be a repository-held instance or equivalent).
     *
     * @param user the borrowing user
     * @param cd the CD to borrow
     * @return true if borrow succeeded
     */
    public boolean borrowCDInstance(User user, CD cd) {
        return borrowMedia(user, cd);
    }

    private boolean borrowMedia(User user, Media media) {
        if (user == null || media == null) return false;

        // Rule: if user has overdue loans -> stop immediately with exact message
        if (LoanRepository.hasOverdueLoans(user)) {
            System.out.println("You cannot borrow: you have overdue items.");
            return false;
        }

        // Locate the repository-held instance (do not create new detached objects)
        Media repoItem = media;
        if (media instanceof Book) {
            Book wanted = (Book) media;
            for (Book b : BookRepository.getBooks()) {
                if (wanted.getIsbn() != null && b.getIsbn() != null && wanted.getIsbn().equalsIgnoreCase(b.getIsbn())) {
                    repoItem = b;
                    break;
                }
                if (wanted.getTitle() != null && b.getTitle() != null && b.getTitle().equalsIgnoreCase(wanted.getTitle())
                        && wanted.getAuthor() != null && b.getAuthor() != null && b.getAuthor().equalsIgnoreCase(wanted.getAuthor())) {
                    repoItem = b;
                    break;
                }
            }
        } else if (media instanceof CD) {
            CD wanted = (CD) media;
            for (CD c : CDRepository.getAll()) {
                if (wanted.getTitle() != null && c.getTitle() != null && c.getTitle().equalsIgnoreCase(wanted.getTitle())
                        && ((wanted.getArtist() == null && c.getArtist() == null) || (wanted.getArtist() != null && c.getArtist() != null && c.getArtist().equalsIgnoreCase(wanted.getArtist())))) {
                    repoItem = c;
                    break;
                }
            }
        }

        // Rule: if user already has an active loan for this item -> stop with exact message
        if (LoanRepository.userHasActiveLoanForItem(user, repoItem)) {
            System.out.println("You already borrowed this item and have not returned it yet.");
            return false;
        }

        // Rule: if out of stock -> stop with exact message
        if (repoItem.getQuantity() <= 0) {
            System.out.println("This item is out of stock.");
            return false;
        }

        // All checks passed -> perform borrow
        repoItem.setQuantity(repoItem.getQuantity() - 1);

        if (repoItem instanceof Book) BookRepository.saveToFile();
        if (repoItem instanceof CD) CDRepository.saveToFile();

        Loan loan = new Loan(user, repoItem);
        LoanRepository.addLoan(loan);

        System.out.println("Borrow successful! You have borrowed: " + repoItem.getTitle());
        return true;
    }

    /**
     * Returns a borrowed item for the user.
     *
     * @param user the user returning
     * @param item the media being returned
     * @return true if return succeeded
     */
    public boolean returnItem(User user, Media item) {
        if (user == null || item == null) return false;

        Loan loan = LoanRepository.findActiveLoan(user, item);
        if (loan == null) {
            System.out.println("You have no active loan for this item.");
            return false;
        }

        Media repoItem = loan.getItem();
        if (repoItem == null) return false;

        // Increase quantity
        repoItem.setQuantity(repoItem.getQuantity() + 1);

        if (repoItem instanceof Book) BookRepository.saveToFile();
        if (repoItem instanceof CD) CDRepository.saveToFile();

        // Persist loan return via repository which will find and update the active loan
        int fine = loan.calculateFine();
        // Use the repository overloaded method to mark returned and record fine atomically
        if (fine > 0) {
            LoanRepository.markLoanReturned(loan, fine);
            user.addFine(fine);
            library_system.Repository.UserRepository.updateUser(user);
            System.out.println("Return completed with fine: " + fine + " NIS");
        } else {
            LoanRepository.markLoanReturned(loan);
            System.out.println("Return successful.");
        }

        return true;
    }
}
