package library_system.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import library_system.domain.CD;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class CDRepository {

    /** In-memory list of CDs */
    private static final List<CD> cds = new ArrayList<>();

    private static final String FILE_PATH = "src/main/resources/cds.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    // Load CDs from file at startup
    static {
        loadFromFile();
    }

    /**
     * Adds a new CD and saves to file.
     */
    public static void addCD(CD cd) {
        cds.add(cd);
        saveToFile();
    }

    /**
     * Returns a CD that matches the title (case-insensitive).
     */
    public static CD findOneByTitle(String title) {
        for (CD cd : cds) {
            if (cd.getTitle().equalsIgnoreCase(title)) {
                return cd;
            }
        }
        return null;
    }

    /**
     * Returns a list of CDs matching title (case-insensitive).
     */
    public static List<CD> findByTitle(String title) {
        List<CD> result = new ArrayList<>();
        for (CD cd : cds) {
            if (cd.getTitle().equalsIgnoreCase(title)) {
                result.add(cd);
            }
        }
        return result;
    }

    /**
     * Returns all CDs in the system.
     */
    public static List<CD> getAll() {
        return new ArrayList<>(cds);
    }

    /**
     * Removes a CD and saves the updated list to file.
     */
    public static void removeCD(CD cd) {
        cds.remove(cd);
        saveToFile();
    }

    /**
     * Clears the CD list (used in unit tests).
     */
    public static void clear() {
        cds.clear();
        saveToFile();
    }

    /**
     * Saves the CD list to cds.json.
     */
    private static void saveToFile() {
        try {
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(FILE_PATH), cds);
        } catch (Exception e) {
            System.err.println("ERROR saving cds.json → " + e.getMessage());
        }
    }

    /**
     * Loads CDs from cds.json file on startup.
     */
    private static void loadFromFile() {
        try {
            File file = new File(FILE_PATH);

            if (!file.exists()) {
                mapper.writeValue(file, cds); // Create empty JSON file
                return;
            }

            List<CD> loaded =
                    mapper.readValue(file, new TypeReference<List<CD>>() {});
            cds.clear();
            cds.addAll(loaded);

        } catch (Exception e) {
            System.err.println("ERROR loading cds.json → " + e.getMessage());
        }
    }
}
