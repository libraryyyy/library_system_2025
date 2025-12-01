package library_system.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import library_system.domain.Loan;
import library_system.domain.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for storing and managing loan records.
 */
public class LoanRepository {

    private static final List<Loan> loans = new ArrayList<>();
    // persisted file name (in data/ directory)
    private static final ObjectMapper mapper = MapperProvider.MAPPER;
    private static final String FILE_NAME = "loans.json";
    private static final File FILE = FileUtil.getDataFile(FILE_NAME);

    /**
     * Loads all loans from JSON file.
     */
    public static void loadFromFile() {
        try {
            if (!FILE.exists() || FILE.length() == 0) {
                saveToFile();
                return;
            }

            JsonNode root = mapper.readTree(FILE);
            ArrayNode array;
            if (root == null || root.isNull()) {
                array = mapper.createArrayNode();
            } else if (root.isArray()) {
                array = (ArrayNode) root;
            } else {
                array = mapper.createArrayNode();
                array.add(root);
            }

            boolean fixed = false;
            for (int i = 0; i < array.size(); i++) {
                JsonNode loanNode = array.get(i);
                if (loanNode != null && loanNode.isObject()) {
                    ObjectNode loanObj = (ObjectNode) loanNode;
                    JsonNode itemNode = loanObj.get("item");
                    if (itemNode != null && itemNode.isObject()) {
                        ObjectNode itemObj = (ObjectNode) itemNode;
                        if (!itemObj.has("mediaType") || itemObj.get("mediaType").isNull() || itemObj.get("mediaType").asText().isEmpty()) {
                            if (itemObj.has("isbn") || itemObj.has("author")) {
                                itemObj.put("mediaType", "BOOK");
                                fixed = true;
                            } else if (itemObj.has("artist")) {
                                itemObj.put("mediaType", "CD");
                                fixed = true;
                            }
                        }
                    }
                }
            }

            if (fixed) {
                mapper.writerWithDefaultPrettyPrinter().writeValue(FILE, array);
            }

            List<Loan> loaded = mapper.convertValue(array, new TypeReference<>() {});
            loans.clear();
            loans.addAll(loaded);

        } catch (Exception e) {
            System.err.println("Error loading loans.json: " + e.getMessage());
        }
    }

    /**
     * Saves all loans to JSON file.
     */
    public static void saveToFile() {
        try {
            FileUtil.writeAtomic(FILE, loans, mapper);

        } catch (Exception e) {
            System.err.println("Error saving loans.json");
        }
    }

    /**
     * Adds a new loan and persists change.
     *
     * @param loan the loan to add
     */
    public static void addLoan(Loan loan) {
        loans.add(loan);
        saveToFile();
    }

    /**
     * Counts the user's active (not returned) loans.
     */
    public static int countActiveLoans(User user) {
        return (int) loans.stream()
                .filter(l -> l.getUser().getUsername().equalsIgnoreCase(user.getUsername()) && !l.isReturned())
                .count();
    }

    /**
     * Returns all loans belonging to a user.
     *
     * @param username username to search for
     * @return user's loans
     */
    public static List<Loan> getUserLoans(String username) {
        List<Loan> result = new ArrayList<>();
        for (Loan l : loans) {
            if (l.getUser().getUsername().equalsIgnoreCase(username)) {
                result.add(l);
            }
        }
        return result;
    }

    /**
     * Returns all loans.
     *
     * @return list of all loans
     */
    public static List<Loan> getAllLoans() {
        return new ArrayList<>(loans);
    }

    /**
     * Returns all overdue (and not returned) loans.
     *
     * @return list of overdue loans
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
     * Determines if the user has any overdue loans.
     *
     * @param user the user
     * @return true if overdue exists
     */
    public static boolean hasOverdueLoans(User user) {
        return loans.stream()
                .anyMatch(l -> l.getUser().getUsername()
                        .equalsIgnoreCase(user.getUsername())
                        && l.isOverdue());
    }

    /**
     * Determines if the user has any active (not returned) loans.
     *
     * @param user the user
     * @return true if user has active loans
     */
    public static boolean hasActiveLoans(User user) {
        return loans.stream()
                .anyMatch(l -> l.getUser().getUsername()
                        .equalsIgnoreCase(user.getUsername())
                        && !l.isReturned());
    }

    /**
     * Clears all loans (for testing).
     */
    public static void clear() {
        loans.clear();
        saveToFile();
    }

    /**
     * Finds the active (not returned) loan for the specified user and media item.
     * Matching is done by comparing ISBN for books or title+artist for CDs.
     *
     * @param user the user who borrowed
     * @param item the media item
     * @return the matching Loan or null if not found
     */
    public static Loan findActiveLoan(User user, library_system.domain.Media item) {
        if (user == null || item == null) return null;
        for (Loan l : loans) {
            if (l.isReturned()) continue;
            if (!l.getUser().getUsername().equalsIgnoreCase(user.getUsername())) continue;
            library_system.domain.Media li = l.getItem();
            if (li == null) continue;
            if (li instanceof library_system.domain.Book && item instanceof library_system.domain.Book) {
                String a = ((library_system.domain.Book) li).getIsbn();
                String b = ((library_system.domain.Book) item).getIsbn();
                if (a != null && b != null && a.equalsIgnoreCase(b)) return l;
            } else if (li instanceof library_system.domain.CD && item instanceof library_system.domain.CD) {
                String t1 = li.getTitle();
                String t2 = item.getTitle();
                String ar1 = ((library_system.domain.CD) li).getArtist();
                String ar2 = ((library_system.domain.CD) item).getArtist();
                if (t1 != null && t2 != null && ar1 != null && ar2 != null
                        && t1.equalsIgnoreCase(t2) && ar1.equalsIgnoreCase(ar2)) {
                    return l;
                }
            }
        }
        return null;
    }

    /**
     * Marks the specified loan as returned and persists change.
     *
     * @param loan loan to mark returned
     */
    public static void markLoanReturned(Loan loan) {
        if (loan == null) return;
        for (Loan l : loans) {
            if (l == loan) {
                l.setReturned(true);
                saveToFile();
                return;
            }
        }
        // If not the same instance, try to find matching loan and mark
        for (Loan l : loans) {
            if (!l.isReturned() && l.getUser().getUsername().equalsIgnoreCase(loan.getUser().getUsername())
                    && l.getItem() != null && loan.getItem() != null
                    && l.getItem().getTitle().equalsIgnoreCase(loan.getItem().getTitle())) {
                l.setReturned(true);
                saveToFile();
                return;
            }
        }
    }
}
