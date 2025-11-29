package library_system.Repository;

import library_system.domain.Loan;
import library_system.domain.User;

import java.util.ArrayList;
import java.util.List;

public class LoanRepository {

    private static final List<Loan> loans = new ArrayList<>();

    // Sprint 4
    public static boolean hasOverdueLoans(User user) {
        return loans.stream()
                .anyMatch(l -> l.getUser().equals(user) && l.isOverdue());
    }

    public static boolean hasActiveLoans(User user) {
        return loans.stream()
                .anyMatch(l -> l.getUser().equals(user) && !l.isReturned());
    }

    // Core Functions
    public static void addLoan(Loan loan) {
        loans.add(loan);
    }

    public static List<Loan> getUserLoans(String username) {
        List<Loan> result = new ArrayList<>();
        for (Loan l : loans) {
            if (l.getUser().getUsername().equals(username)) {
                result.add(l);
            }
        }
        return result;
    }

    public static List<Loan> getAllLoans() {
        return new ArrayList<>(loans);
    }

    // 🔥 NEW for Sprint 5
    public static List<Loan> getOverdueLoans() {
        List<Loan> result = new ArrayList<>();
        for (Loan loan : loans) {
            if (!loan.isReturned() && loan.isOverdue()) {
                result.add(loan);
            }
        }
        return result;
    }

    public static void clear() {
        loans.clear();
    }
}
