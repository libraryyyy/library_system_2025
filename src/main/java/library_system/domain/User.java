package library_system.domain;

/**
 * Represents a library user who can borrow items and pay fines.
 */
public class User {

    /** Username of the user. */
    private String username;

    /** Password of the user. */
    private String password;

    /** Current outstanding fine balance for the user. */
    private double fineBalance = 0.0;

    /**
     * Creates a new user with the given credentials.
     *
     * @param username user username.
     * @param password user password.
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * @return the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return the password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return current fine balance.
     */
    public double getFineBalance() {
        return fineBalance;
    }

    /**
     * Increases the user's fine balance by the given amount.
     *
     * @param amount amount to add to the fine balance.
     */
    public void addFine(double amount) {
        fineBalance += amount;
    }

    /**
     * Decreases the user's fine balance by the given amount.
     *
     * @param amount amount to subtract from the fine balance.
     */
    public boolean payFine(double amount) {

        // لا يوجد غرامة للدفع
        if (fineBalance <= 0) {
            return false;
        }

        // مبلغ غير صالح
        if (amount <= 0) {
            return false;
        }

        // لا يمكن دفع أكثر من الغرامة
        if (amount > fineBalance) {
            return false;
        }

        fineBalance -= amount;
        return true;
    }
    public void addFineAmount(int amount) {
        this.fineBalance += amount;
    }

}
