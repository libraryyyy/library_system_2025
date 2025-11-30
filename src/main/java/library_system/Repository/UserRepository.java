package library_system.Repository;

import library_system.domain.User;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.List;


public class UserRepository {



    private static final String FILE_PATH = "src/main/resources/users.json";

    private static List<User> users = new ArrayList<>();

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();



   //  * Loads users from JSON file into memory.


    public static void loadFromFile() {
        try (FileReader reader = new FileReader(FILE_PATH)) {

            Type listType = new TypeToken<List<User>>() {}.getType();
            users = gson.fromJson(reader, listType);

            if (users == null) {
                users = new ArrayList<>();
            }

        } catch (IOException e) {
            users = new ArrayList<>();
        }
    }

    /**
     * Saves current users list to JSON file.
     */


    public static void saveToFile() {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            System.err.println("Failed to save users.json");
        }
    }

    /**
     * Adds a new user to the system and saves to file.
     *
     * @param user user to add.
     */
    public static void addUser(User user) {
        users.add(user);
        saveToFile();
    }

    /**
     * Finds a user by username.
     *
     * @param username username to search for.
     * @return matching user or {@code null} if not found.
     */
    public static User findUser(String username) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Removes a user from the system.
     *
     * @param username username to remove
     * @return true if deleted, false if not found
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
     * Clears all users from the repository.
     * Used mainly in unit tests.
     */
    public static void clear() {
        users.clear();
        saveToFile();

    }

    /**
     * Completely remove user (used by AdminService).
     */

    public static void deleteUser(User user) {
        users.remove(user);
        saveToFile();

    }
    /**
     * Returns all users — required by ReminderService & OverdueReportService
     */
    public static List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
}

