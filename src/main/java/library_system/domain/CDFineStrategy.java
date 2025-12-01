package library_system.domain;

public class CDFineStrategy implements FineStrategy {
    @Override
    public int calculateFine(int overdueDays) {
        if (overdueDays <= 0) return 0;
        return overdueDays * 20;
    }
}
