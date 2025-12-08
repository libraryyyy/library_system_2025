package library_system.Repository;

import library_system.domain.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository responsible for persisting User objects to JSON.
 * <p>
 * Uses the shared {@link MapperProvider#MAPPER} and {@link FileUtil} helpers
 * to read and write {@code src/main/resources/users.json}. Methods return
 * defensive copies where appropriate.
 * </p>
 */
public class UserRepository {

    private static final ObjectMapper mapper = MapperProvider.MAPPER;
    private static final String FILE_NAME = "users.json";
    private static final File FILE = FileUtil.getDataFile(FILE_NAME);

    private static List<User> users = new ArrayList<>();

    /**
     * Loads users from disk. Creates an empty file if missing.
     */
    public static void loadFromFile() {
        try {
            List<User> loaded = FileUtil.readList(FILE, new TypeReference<List<User>>() {}, mapper);
            users.clear();
            if (loaded != null) {
                for (User u : loaded) {
                    if (u.getEmail() != null) {
                        u.setEmail(sanitizeEmail(u.getEmail()));
                    }
                    users.add(u);
                }
            }
        } catch (Exception e) {
            users = new ArrayList<>();
            System.err.println("Error loading users.json: " + e.getMessage());
        }
    }

    /**
     * Saves users to disk atomically.
     */
    public static void saveToFile() {
        try {
            // sanitize emails before saving
            for (User u : users) {
                if (u.getEmail() != null) {
                    u.setEmail(sanitizeEmail(u.getEmail()));
                }
            }
            FileUtil.writeAtomic(FILE, users, mapper);
        } catch (Exception e) {
            System.err.println("Failed to save users.json: " + e.getMessage());
        }
    }

    /**
     * Adds a new user and persists the repository.
     *
     * @param user user to add
     */
    public static void addUser(User user) {
        if (user.getEmail() != null) {
            user.setEmail(sanitizeEmail(user.getEmail()));
        }
        users.add(user);
        saveToFile();
    }

    /**
     * Finds a user by username (case-insensitive).
     *
     * @param username username to search for
     * @return matching User or null if not found
     */
    public static User findUser(String username) {
        if (username == null) return null;
        return users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds a user by email (case-insensitive).
     *
     * @param email email to search
     * @return matching User or null
     */
    public static User findUserByEmail(String email) {
        if (email == null) return null;
        String s = sanitizeEmail(email);
        return users.stream()
                .filter(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(s))
                .findFirst()
                .orElse(null);
    }

    /**
     * Removes a user by username and persists the change.
     *
     * @param username username to remove
     * @return true if removed
     */
    public static boolean removeUser(String username) {
        User u = findUser(username);
        if (u != null) {
            users.remove(u);
            saveToFile();
            return true;
        }
        return false;
    }

    /**
     * Deletes the given user instance and persists.
     *
     * @param user user to delete
     */
    public static void deleteUser(User user) {
        users.remove(user);
        saveToFile();
    }

    /**
     * Clears all users (for testing) and persists the empty state.
     */
    public static void clear() {
        users.clear();
        saveToFile();
    }

    /**
     * @return all users in a defensive copy
     */
    public static List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    /**
     * Persist changes to an existing user (e.g., fine payment).
     *
     * @param user the user that was modified
     */
    public static void updateUser(User user) {
        // Ensure parameter is used to avoid unused-parameter warnings
        if (user == null) return;
        if (user.getEmail() != null) user.setEmail(sanitizeEmail(user.getEmail()));
        // In-memory user objects are the same instances returned by the repo,
        // so just saving is sufficient to persist the change.
        saveToFile();
    }

    private static String sanitizeEmail(String email) {
        if (email == null) return null;
        String s = email.trim();
        s = s.replaceAll("\\s+", "");
        return s;
    }
}
