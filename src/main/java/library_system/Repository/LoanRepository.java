package library_system.repository;

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
    private static final ObjectMapper mapper = MapperProvider.MAPPER;
    private static final String FILE_NAME = "loans.json";
    private static final File FILE = FileUtil.getDataFile(FILE_NAME);

    /**
     * Loads all loans from JSON file, fixes missing fields, and cleans the JSON.
     */
    public static void loadFromFile() {
        try {
            if (!FILE.exists() || FILE.length() == 0) {
                loans.clear();
                saveToFile();
                return;
            }

            List<Loan> loaded;
            JsonNode root = mapper.readTree(FILE);

            if (root.isArray()) {
                loaded = mapper.readValue(FILE, new TypeReference<List<Loan>>() {});
            } else if (root.isObject()) {
                Loan loan = mapper.readValue(FILE, Loan.class);
                loaded = new ArrayList<>();
                loaded.add(loan);
            } else {
                loaded = new ArrayList<>();
            }

            loans.clear();
            boolean changed = false;
            for (Loan l : loaded) {
                // sanitize email
                if (l.getUser() != null && l.getUser().getEmail() != null) {
                    l.getUser().setEmail(sanitizeEmail(l.getUser().getEmail()));
                }

                // fix dueDate if missing or invalid
                if (l.getBorrowedDate() != null) {
                    if (l.getDueDate() == null || l.getDueDate().isBefore(l.getBorrowedDate())) {
                        if (l.getItem() != null) {
                            int duration = l.getItem().getBorrowDuration();
                            l.setDueDate(l.getBorrowedDate().plusDays(duration));
                            changed = true;
                        }
                    }
                }

                // ensure fine fields exist
                if (l.getFineAmount() < 0) { l.setFineAmount(0); changed = true; }
                if (!l.isFinePaid()) { l.setFinePaid(false); }

                loans.add(l);
            }

            // save back to file if anything changed (to keep JSON clean)
            if (changed) saveToFile();

        } catch (Exception e) {
            System.err.println("Error loading loans.json: " + e.getMessage());
            loans.clear();
        }
    }

    /**
     * Returns all loans belonging to a specific user.
     */
    public static List<Loan> getUserLoans(String username) {
        List<Loan> result = new ArrayList<>();
        for (Loan l : loans) {
            if (l.getUser() != null && l.getUser().getUsername().equalsIgnoreCase(username)) {
                result.add(l);
            }
        }
        return result;
    }

    /**
     * Checks if the specified user has any overdue loans (not returned and past due date).
     *
     * @param user the user to check
     * @return true if the user has at least one overdue loan
     */
    public static boolean hasOverdueLoans(User user) {
        if (user == null) return false;
        for (Loan l : loans) {
            if (l.getUser() != null
                    && l.getUser().getUsername().equalsIgnoreCase(user.getUsername())
                    && !l.isReturned()
                    && l.isOverdue()) {
                return true;
            }
        }
        return false;
    }
    /**
     * Checks if the user already has an active (not returned) loan for the given media item.
     *
     * @param user the user
     * @param item the media item
     * @return true if user has an active loan for this item
     */
    public static boolean userHasActiveLoanForItem(User user, library_system.domain.Media item) {
        return findActiveLoan(user, item) != null;
    }

    /**
     * Finds the active (not returned) loan for the specified user and media item.
     * Matching: books by ISBN if present, otherwise by title+author; CDs by title+artist.
     *
     * @param user the user who borrowed
     * @param item the media item
     * @return the matching Loan or null if not found
     */
    public static Loan findActiveLoan(User user, library_system.domain.Media item) {
        if (user == null || item == null) return null;
        for (Loan l : loans) {
            if (l.isReturned()) continue;
            if (l.getUser() == null || !l.getUser().getUsername().equalsIgnoreCase(user.getUsername())) continue;
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

    public static void addLoan(Loan loan) {
        if (loan == null) return;

        // prevent duplicates
        Loan existing = findActiveLoan(loan.getUser(), loan.getItem());
        if (existing != null) return;

        // إصلاح dueDate إذا مفقود
        if (loan.getBorrowedDate() != null && (loan.getDueDate() == null) && loan.getItem() != null) {
            loan.setDueDate(loan.getBorrowedDate().plusDays(loan.getItem().getBorrowDuration()));
        }

        loans.add(loan);
        saveToFile();
    }

    /**
     * Marks the specified loan as returned and persists change.
     *
     * @param loan loan to mark returned
     */
    public static void markLoanReturned(Loan loan) {
        if (loan == null) return;

        for (Loan l : loans) {
            if (!l.isReturned() && l == loan) {
                l.setReturned(true);
                break;
            }
        }
        saveToFile();
    }

    /**
     * Marks the specified loan as returned and records an associated fine (if any).
     *
     * @param loan loan to mark returned
     * @param fine fine amount to record
     */
    public static void markLoanReturned(Loan loan, int fine) {
        if (loan == null) return;

        for (Loan l : loans) {
            if (!l.isReturned() && l == loan) {
                l.setReturned(true);
                if (fine > 0) l.setFineAmount(fine);
                break;
            }
        }
        saveToFile();
    }
    /**
     * Checks if the user has any active (not returned) loans.
     *
     * @param user the user
     * @return true if user has active loans
     */
    public static boolean hasActiveLoans(User user) {
        if (user == null) return false;
        for (Loan l : loans) {
            if (!l.isReturned() && l.getUser() != null
                    && l.getUser().getUsername().equalsIgnoreCase(user.getUsername())) {
                return true;
            }
        }
        return false;
    }
    /**
     * Clears all loans (useful for testing).
     */
    public static void clear() {
        loans.clear();
        saveToFile();
    }

    /**
     * Returns a copy of all loans.
     *
     * @return list of all loans
     */
    public static List<Loan> getAllLoans() {
        return new ArrayList<>(loans);
    }


    /**
     * Returns all overdue loans (not yet returned and past due date).
     */
    public static List<Loan> getOverdueLoans() {
        List<Loan> overdue = new ArrayList<>();
        for (Loan l : loans) {
            if (!l.isReturned() && l.isOverdue()) {
                overdue.add(l);
            }
        }
        return overdue;
    }

    public static void saveToFile() {
        try {
            ArrayNode arr = mapper.createArrayNode();
            for (Loan l : loans) {
                ObjectNode loanObj = mapper.createObjectNode();

                // user
                ObjectNode userNode = mapper.createObjectNode();
                User u = l.getUser();
                if (u != null) {
                    userNode.put("username", u.getUsername());
                    userNode.put("password", u.getPassword());
                    userNode.put("email", u.getEmail() != null ? sanitizeEmail(u.getEmail()) : null);
                    userNode.put("fineBalance", u.getFineBalance());
                }
                loanObj.set("user", userNode);

                // item
                ObjectNode itemNode = mapper.createObjectNode();
                library_system.domain.Media m = l.getItem();
                if (m != null) {
                    if (m.getMediaType() != null) itemNode.put("mediaType", m.getMediaType());
                    itemNode.put("title", m.getTitle());
                    if (m instanceof library_system.domain.Book) {
                        library_system.domain.Book bk = (library_system.domain.Book) m;
                        itemNode.put("author", bk.getAuthor());
                        if (bk.getIsbn() != null) itemNode.put("isbn", bk.getIsbn());
                    } else if (m instanceof library_system.domain.CD) {
                        library_system.domain.CD cd = (library_system.domain.CD) m;
                        itemNode.put("artist", cd.getArtist());
                    }
                    itemNode.put("quantity", m.getQuantity());
                    itemNode.put("borrowDuration", m.getBorrowDuration());
                }
                loanObj.set("item", itemNode);

                if (l.getBorrowedDate() != null) loanObj.put("borrowedDate", l.getBorrowedDate().toString());
                if (l.getDueDate() != null) loanObj.put("dueDate", l.getDueDate().toString());
                loanObj.put("returned", l.isReturned());
                loanObj.put("finePaid", l.isFinePaid());
                loanObj.put("fineAmount", l.getFineAmount());

                arr.add(loanObj);
            }
            mapper.writerWithDefaultPrettyPrinter().writeValue(FILE, arr);
        } catch (Exception e) {
            System.err.println("Error saving loans.json: " + e.getMessage());
        }
    }

    private static String sanitizeEmail(String email) {
        if (email == null) return null;
        return email.trim().toLowerCase();
    }

    // باقي دوال Repository تبقى كما هي...
}
