package library_system.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import library_system.domain.Loan;
import library_system.domain.User;

import java.io.File;
import java.time.LocalDate;
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
     * Loads all loans from JSON file and repairs/cleans the file to contain only
     * the required fields for both user and item.
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

            boolean changed = false;
            ArrayNode cleaned = mapper.createArrayNode();

            for (JsonNode loanNode : array) {
                if (loanNode == null || !loanNode.isObject()) continue;
                ObjectNode loanObj = (ObjectNode) loanNode;

                // Build cleaned loan node
                ObjectNode cleanLoan = mapper.createObjectNode();

                // user: only username,password,email,fineBalance
                JsonNode userNode = loanObj.get("user");
                ObjectNode cleanUser = mapper.createObjectNode();
                if (userNode != null && userNode.isObject()) {
                    if (userNode.has("username")) cleanUser.put("username", userNode.get("username").asText());
                    if (userNode.has("password")) cleanUser.put("password", userNode.get("password").asText());
                    if (userNode.has("email")) cleanUser.put("email", userNode.get("email").asText());
                    if (userNode.has("fineBalance")) cleanUser.put("fineBalance", userNode.get("fineBalance").asDouble());
                }
                cleanLoan.set("user", cleanUser);

                // item: minimal fields depending on mediaType
                JsonNode itemNode = loanObj.get("item");
                ObjectNode cleanItem = mapper.createObjectNode();
                String mediaType = null;
                if (itemNode != null && itemNode.isObject()) {
                    if (itemNode.has("mediaType")) mediaType = itemNode.get("mediaType").asText();
                    // If mediaType missing, try to infer
                    if (mediaType == null || mediaType.isEmpty()) {
                        if (itemNode.has("isbn") || itemNode.has("author")) mediaType = "BOOK";
                        else if (itemNode.has("artist")) mediaType = "CD";
                    }
                    if (mediaType != null) cleanItem.put("mediaType", mediaType);
                    if (itemNode.has("title")) cleanItem.put("title", itemNode.get("title").asText());
                    if ("BOOK".equalsIgnoreCase(mediaType)) {
                        if (itemNode.has("author")) cleanItem.put("author", itemNode.get("author").asText());
                        if (itemNode.has("isbn")) cleanItem.put("isbn", itemNode.get("isbn").asText());
                    } else if ("CD".equalsIgnoreCase(mediaType)) {
                        if (itemNode.has("artist")) cleanItem.put("artist", itemNode.get("artist").asText());
                    }
                    if (itemNode.has("quantity")) cleanItem.put("quantity", itemNode.get("quantity").asInt(1));
                    else cleanItem.put("quantity", 1);
                    if (itemNode.has("borrowDuration")) cleanItem.put("borrowDuration", itemNode.get("borrowDuration").asInt());
                }
                cleanLoan.set("item", cleanItem);

                // Copy borrowedDate, dueDate, returned fields if present
                if (loanObj.has("borrowedDate")) cleanLoan.set("borrowedDate", loanObj.get("borrowedDate"));
                if (loanObj.has("dueDate")) cleanLoan.set("dueDate", loanObj.get("dueDate"));
                if (loanObj.has("returned")) cleanLoan.set("returned", loanObj.get("returned"));
                else cleanLoan.put("returned", false);

                cleaned.add(cleanLoan);
                // if original differs from cleaned, mark changed
                if (!loanObj.equals(cleanLoan)) changed = true;
            }

            if (changed) {
                mapper.writerWithDefaultPrettyPrinter().writeValue(FILE, cleaned);
            }

            // Now deserialize cleaned file into Loan objects
            List<Loan> loaded = mapper.readValue(FILE, new TypeReference<List<Loan>>() {});
            loans.clear();
            loans.addAll(loaded);

        } catch (Exception e) {
            System.err.println("Error loading loans.json: " + e.getMessage());
            loans.clear();
        }
    }

    /**
     * Saves all loans to JSON file using a cleaned representation (no ids, no borrowed field).
     */
    public static void saveToFile() {
        try {
            ArrayNode arr = mapper.createArrayNode();
            for (Loan l : loans) {
                ObjectNode loanObj = mapper.createObjectNode();
                // user minimal
                ObjectNode userNode = mapper.createObjectNode();
                User u = l.getUser();
                if (u != null) {
                    userNode.put("username", u.getUsername());
                    userNode.put("password", u.getPassword());
                    userNode.put("email", u.getEmail());
                    userNode.put("fineBalance", u.getFineBalance());
                }
                loanObj.set("user", userNode);
                // item minimal
                ObjectNode itemNode = mapper.createObjectNode();
                library_system.domain.Media m = l.getItem();
                if (m != null) {
                    String mt = m.getMediaType();
                    if (mt != null) itemNode.put("mediaType", mt);
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

                arr.add(loanObj);
            }
            mapper.writerWithDefaultPrettyPrinter().writeValue(FILE, arr);
        } catch (Exception e) {
            System.err.println("Error saving loans.json: " + e.getMessage());
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
     * Checks if a user already has an active (not returned) loan for the given media item.
     * Matching: books by ISBN if present otherwise title; CDs by title+artist.
     *
     * @param user the user
     * @param item the media item
     * @return true if user has an active loan for this item
     */
    public static boolean userHasActiveLoanForItem(User user, library_system.domain.Media item) {
        return findActiveLoan(user, item) != null;
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
