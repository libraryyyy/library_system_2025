package library_system.domain;

public class User {

    private String username;
    private String password;
    private String email;          // ✅ جديد
    private double fineBalance = 0.0;

    public User() {
    }
    /**
     * Creates a new user with credentials and email.
     *
     * @param username unique username.
     * @param password user password.
     * @param email    user email address.
     */
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    /** @return username. */
    public String getUsername() { return username; }
    /** Sets username (for JSON). */
    public void setUsername(String username) {
        this.username = username;
    }
    /** @return password. */
    public String getPassword() { return password; }
    /** Sets password (for JSON). */
    public void setPassword(String password) {
        this.password = password;
    }
    /** @return email. */
    public String getEmail() { return email; } // ✅ مهم
    /** Sets email (for JSON). */
    public void setEmail(String email) {
        this.email = email;
    }
    /** @return current fine balance. */
    public double getFineBalance() { return fineBalance; }

    public void setFineBalance(double amount) { this.fineBalance = amount; }

    public void addFine(double amount) { fineBalance += amount; }
    /**
     * Pays part or all of the fine.
     *
     * @param amount amount to pay.
     * @return true if payment accepted, false otherwise.
     */
    public boolean payFine(double amount) {
        if (fineBalance <= 0 || amount <= 0 || amount > fineBalance) return false;
        fineBalance -= amount;
        return true;
    }
}
