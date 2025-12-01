package library_system.domain;

/**
 * Fine calculation strategy for CDs.
 * CDs incur a fine of 20 NIS per overdue day.
 */
public class CDFineStrategy implements FineStrategy {

    /**
     * @param overdueDays days past due date
     * @return overdueDays * 20, or 0 if not overdue
     */
    @Override
    public int calculateFine(int overdueDays) {
        return overdueDays <= 0 ? 0 : overdueDays * 20;
    }
}
