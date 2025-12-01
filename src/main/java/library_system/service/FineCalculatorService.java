package library_system.service;

import library_system.domain.Loan;

public class FineCalculatorService {

    /**
     * Computes the fine for a given loan.
     *
     * @param loan overdue loan
     * @return fine amount in NIS
     */
    public int calculateFine(Loan loan) {
        if (loan == null)
            return 0;
        return loan.calculateFine();
    }
}
