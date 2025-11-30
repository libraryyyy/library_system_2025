package library_system.service;

import library_system.domain.Loan;

/**
 * Calculates fine cost for any media type using its internal strategy.
 */
public class FineCalculatorService {

    /**
     * Computes the fine for a single loan.
     *
     * @param loan the overdue loan
     * @return fine amount in NIS
     */
    public int calculateFine(Loan loan) {

        if (loan == null) {
            return 0;
        }
        return loan.calculateFine();
    }
}
