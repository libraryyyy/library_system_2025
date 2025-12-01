package library_system.domain;

/**
 * Strategy interface for calculating overdue fines for different media types.
 * Implemented by {@link BookFineStrategy} and {@link CDFineStrategy}.
 */
public interface FineStrategy {

    /**
     * Calculates the fine based on number of overdue days.
     *
     * @param overdueDays number of overdue days
     * @return fine amount in NIS
     */
    int calculateFine(int overdueDays);
}
