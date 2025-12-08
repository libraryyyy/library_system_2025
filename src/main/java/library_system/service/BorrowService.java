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

        if (user.getFineBalance() > 0) {
            System.out.println("You cannot borrow: outstanding fines: " + user.getFineBalance());
            return false;
        }
        if (LoanRepository.hasOverdueLoans(user)) {
            System.out.println("You cannot borrow: you have overdue items.");
            return false;
        }

        // Find repository-held instance to update quantity
        Media repoItem = media;
        if (media instanceof Book) {
            Book wanted = (Book) media;
            for (Book b : BookRepository.getBooks()) {
                if (wanted.getIsbn() != null && b.getIsbn() != null && wanted.getIsbn().equalsIgnoreCase(b.getIsbn())) {
                    repoItem = b;
                    break;
                }
                if (wanted.getTitle() != null && b.getTitle() != null && wanted.getTitle().equalsIgnoreCase(b.getTitle())
                        && wanted.getAuthor() != null && b.getAuthor() != null && wanted.getAuthor().equalsIgnoreCase(b.getAuthor())) {
                    repoItem = b;
                    break;
                }
            }
        } else if (media instanceof CD) {
            CD wanted = (CD) media;
            for (CD c : CDRepository.getAll()) {
                if (wanted.getTitle() != null && c.getTitle() != null && wanted.getTitle().equalsIgnoreCase(c.getTitle())
                        && ((wanted.getArtist() == null && c.getArtist() == null) || (wanted.getArtist() != null && c.getArtist() != null && wanted.getArtist().equalsIgnoreCase(c.getArtist())))) {
                    repoItem = c;
                    break;
                }
            }
        }

        // Use quantity as source of truth
        if (repoItem.getQuantity() <= 0) {
            System.out.println("Item unavailable. Quantity is zero.");
            return false;
        }

        // Prevent duplicate active loan
        if (LoanRepository.userHasActiveLoanForItem(user, repoItem)) {
            System.out.println("You already borrowed this item. Please return it first.");
            return false;
        }

        // Decrease quantity and persist
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

        // Update loan and repo item
        loan.markReturned();
        Media repoItem = loan.getItem();
        if (repoItem == null) return false;

        // Increase quantity
        repoItem.setQuantity(repoItem.getQuantity() + 1);

        if (repoItem instanceof Book) BookRepository.saveToFile();
        if (repoItem instanceof CD) CDRepository.saveToFile();

        LoanRepository.markLoanReturned(loan);
        int fine = loan.calculateFine();
        if (fine > 0) {
            user.addFine(fine);
            library_system.Repository.UserRepository.updateUser(user);
            System.out.println("Return completed with fine: " + fine + " NIS");
        } else {
            System.out.println("Return successful.");
        }

        return true;
    }
}
