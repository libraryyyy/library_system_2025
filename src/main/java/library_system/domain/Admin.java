package library_system.domain;

/**
 * Represents the system administrator with full access to manage
 * users, books, CDs, and other library operations.
 * <p>
 * This class is kept intentionally simple because the system has
 * only one admin account in Phase 1 & 2.
 * </p>
 *
 * @author sana
 * @version 1.0
 */
public class Admin {

    /** Unique username of the administrator. */
    private String username;

    /** Password of the administrator (plaintext for simplicity in Phase 1). */
    private String password;

    /**
     * Constructs an Admin object with username and password.
     *
     * @param username the admin username
     * @param password the admin password
     */
    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * @return the admin's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Updates the admin's username.
     *
     * @param username new username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the admin's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Updates the admin's password.
     *
     * @param password new password
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
