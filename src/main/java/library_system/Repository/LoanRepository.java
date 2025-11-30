package library_system.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import library_system.domain.Loan;
import library_system.domain.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for storing all loan records (Book/CD borrowing).
 * Uses JSON file persistence to keep loans across program runs.
 */
public class LoanRepository {

    /** In-memory loan list */
    private static final List<Loan> loans = new ArrayList<>();

    private static final String FILE_PATH = "src/main/resources/loans.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    // Load loans on startup
    static {
        loadFromFile();
    }

    /**
     * Adds a new loan to repository and saves to file.
     */
    public static void addLoan(Loan loan) {
        loans.add(loan);
        saveToFile();
    }

    /**
     * Returns a list of all loans for a specific username.
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
     * Returns all loans in system.
     */
    public static List<Loan> getAllLoans() {
        return new ArrayList<>(loans);
    }

    /**
     * Returns all overdue (but not returned) loans.
     */
    public static List<Loan> getOverdueLoans() {
        List<Loan> result = new ArrayList<>();
        for (Loan loan : loans) {
            if (!loan.isReturned() && loan.isOverdue()) {
                result.add(loan);
            }
        }
        return result;
    }

    /**
     * Checks whether a user has overdue loans.
     */
    public static boolean hasOverdueLoans(User user) {
        return loans.stream()
                .anyMatch(l -> l.getUser().equals(user) && l.isOverdue());
    }

    /**
     * Checks whether a user has active (non-returned) loans.
     */
    public static boolean hasActiveLoans(User user) {
        return loans.stream()
                .anyMatch(l -> l.getUser().equals(user) && !l.isReturned());
    }

    /**
     * Saves loan list to JSON file.
     */
    private static void saveToFile() {
        try {
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(FILE_PATH), loans);
        } catch (Exception e) {
            System.err.println("ERROR saving loans.json → " + e.getMessage());
        }
    }

    /**
     * Loads loan list from JSON file during startup.
     */
    private static void loadFromFile() {
        try {
            File file = new File(FILE_PATH);

            if (!file.exists()) {
                mapper.writeValue(file, loans); // create empty file
                return;
            }

            List<Loan> loaded =
                    mapper.readValue(file, new TypeReference<List<Loan>>() {});
            loans.clear();
            loans.addAll(loaded);

        } catch (Exception e) {
            System.err.println("ERROR loading loans.json → " + e.getMessage());
        }
    }

    /**
     * Clears all loans (used only in unit testing)
     */
    public static void clear() {
        loans.clear();
        saveToFile();
    }
}
