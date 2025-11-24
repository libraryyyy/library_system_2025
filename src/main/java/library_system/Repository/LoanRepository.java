package library_system.Repository;

import library_system.domain.Loan;

import java.util.ArrayList;
import java.util.List;

import library_system.domain.Loan;

/**
 * In-memory repository for storing loan records.
 */
public class LoanRepository {

    /** Internal list that stores all loans. */
    private static final List<Loan> loans = new ArrayList<>();

    /**
     * Adds a new loan to the repository.
     *
     * @param loan loan to add.
     */
    public static void addLoan(Loan loan) {
        loans.add(loan);
    }

    /**
     * Returns all loans associated with a given username.
     *
     * @param username username to search loans for.
     * @return list of loans for the given user.
     */
    public static List<Loan> getUserLoans(String username) {
        List<Loan> result = new ArrayList<>();
        for (Loan l : loans) {
            if (l.getUser().getUsername().equals(username)) {
                result.add(l);
            }
        }
        return result;
    }

    /**
     * Clears all loans from the repository.
     * Useful for unit tests.
     */
    public static void clear() {
        loans.clear();
    }

    public static List<Loan> getAllLoans() {
        return new ArrayList<>(loans);
    }

}
