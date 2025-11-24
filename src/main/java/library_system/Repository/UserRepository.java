package library_system.Repository;

import library_system.domain.User;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory repository for storing and retrieving users.
 */
public class UserRepository {

    /** Internal list that stores all registered users. */
    private static final List<User> users = new ArrayList<>();

    /**
     * Adds a new user to the repository.
     *
     * @param user user to add.
     */
    public static void addUser(User user) {
        users.add(user);
    }

    /**
     * Finds a user by username.
     *
     * @param username username to search for.
     * @return matching user or {@code null} if not found.
     */
    public static User findUser(String username) {
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Clears all users from the repository.
     * Used mainly in unit tests.
     */
    public static void clear() {
        users.clear();
    }



}

