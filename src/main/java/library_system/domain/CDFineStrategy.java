package library_system.domain;

public class CDFineStrategy implements FineStrategy {
    @Override
    public int calculateFine(int overdueDays) {
        return overdueDays * 20;
    }
}
