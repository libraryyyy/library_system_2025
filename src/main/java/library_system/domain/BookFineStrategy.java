package library_system.domain;
public class BookFineStrategy implements FineStrategy {

    /**
     * @param overdueDays days past due date
     * @return overdueDays * 10, or 0 if not overdue
     */
    @Override
    public int calculateFine(int overdueDays) {
        return overdueDays <= 0 ? 0 : overdueDays * 10;
    }
}
