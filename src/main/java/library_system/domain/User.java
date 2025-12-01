package library_system.domain;

/**
 * Represents an application user (library patron).
 * <p>
 * Stores authentication credentials, contact email and any outstanding fines.
 * This class is used for JSON serialization/deserialization by the repositories
 * and therefore provides a default constructor and setters/getters for all fields.
 * </p>
 */
public class User {

    /** Unique username for login. */
    private String username;

    /** Password for authentication. */
    private String password;

    /** Email address (needed for overdue reminder notifications). */
    private String email;

    /** Outstanding fines owed by the user. */
    private double fineBalance = 0.0;

    /**
     * Default constructor for JSON serialization/deserialization.
     */
    public User() {
        // Empty constructor required by Jackson.
    }

    /**
     * Constructs a new user with credentials and email.
     *
     * @param username unique username
     * @param password password
     * @param email    validated user email
     */
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    /**
     * Returns the username used for login.
     *
     * @return username string
     */
    public String getUsername() {
        return username;
    }

    /**
     * Updates the username (used by JSON deserialization).
     *
     * @param username new username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the user's password (stored in plain text in this simple example).
     *
     * @return password string
     */
    public String getPassword() {
        return password;
    }

    /**
     * Updates the user's password (used by JSON deserialization).
     *
     * @param password new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the user's email address.
     *
     * @return email address or null
     */
    public String getEmail() {
        return email;
    }

    /**
     * Updates the user's email address (used by JSON deserialization).
     *
     * @param email new email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the user's outstanding fine balance.
     *
     * @return fine balance (NIS)
     */
    public double getFineBalance() {
        return fineBalance;
    }

    /**
     * Sets the user's fine balance (used when loading from JSON).
     *
     * @param amount total fine amount
     */
    public void setFineBalance(double amount) {
        this.fineBalance = amount;
    }

    /**
     * Increases the user's fine balance by the given amount.
     *
     * @param amount fine amount to add (must be > 0)
     */
    public void addFine(double amount) {
        if (amount <= 0) return;
        fineBalance += amount;
    }

    /**
     * Attempts to pay a portion (or all) of the user's fine.
     *
     * @param amount amount to pay (must be > 0 and <= current balance)
     * @return true if payment successful, false otherwise
     */
    public boolean payFine(double amount) {
        if (amount <= 0) return false;
        if (fineBalance <= 0) return false;
        if (amount > fineBalance) return false;
        fineBalance -= amount;
        return true;
    }

    /**
     * Validates the email format using a simple regex.
     * This is intentionally permissive; production code should use a robust validator.
     *
     * @return true if email looks valid, false otherwise
     */
    public boolean isValidEmail() {
        if (email == null) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}
