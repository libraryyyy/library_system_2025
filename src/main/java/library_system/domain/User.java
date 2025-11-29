package library_system.domain;

/**
 * Represents a library user who can borrow items and pay fines.
 */
public class User {

    private String username;
    private String password;
    private String email;          // ✅ جديد
    private double fineBalance = 0.0;

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() { return username; }

    public String getPassword() { return password; }

    public String getEmail() { return email; } // ✅ مهم

    public double getFineBalance() { return fineBalance; }

    public void setFineBalance(double amount) { this.fineBalance = amount; }

    public void addFine(double amount) { fineBalance += amount; }

    public boolean payFine(double amount) {
        if (fineBalance <= 0 || amount <= 0 || amount > fineBalance) return false;
        fineBalance -= amount;
        return true;
    }
}
