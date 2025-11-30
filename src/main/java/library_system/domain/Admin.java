package library_system.domain;

/**
 * Represents a system administrator who can manage users and books.
 */
public class Admin {

    /** Unique username of the administrator. */
    private String username;

    /** Password of the administrator. */
    private String password;

    /**
     * Creates a new admin with the given credentials.
     *
     * @param username admin username.
     * @param password admin password.
     */
    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * @return the admin username.
     */
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }

    /**
     * @return the admin password.
     */
    public String getPassword() {
        return password;
    }
}
