package library_system.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * Utility class for managing data files used by repository classes.
 * <p>
 * This class handles:
 * <ul>
 *     <li>Ensuring the data directory exists</li>
 *     <li>Reading JSON files safely</li>
 *     <li>Writing JSON files atomically (prevents file corruption)</li>
 *     <li>Overriding data directory during unit tests</li>
 * </ul>
 * <p>
 * NOTE: For tests, use {@link #overrideDataDirForTesting(String)} to isolate test data.
 */
public class FileUtil {

    /**
     * Default directory where real application data is stored.
     */
    private static String DATA_DIR = "src/main/resources";

    /**
     * Overrides the data directory for unit testing.
     * <p>
     * Ensures test runs never touch real JSON files.
     *
     * @param testDir the new directory for test-only JSON files
     */
    public static void overrideDataDirForTesting(String testDir) {
        DATA_DIR = testDir;
        ensureDataDirExists();
    }

    /**
     * Resets the data directory back to production path.
     * <p>
     * Should be used inside @AfterEach of unit tests.
     */
    public static void resetDataDir() {
        DATA_DIR = "src/main/resources";
    }

    /**
     * Ensures that the data directory exists.
     * If not, it will be created along with any missing parent directories.
     */
    public static void ensureDataDirExists() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Returns a File object for the given filename inside the current data directory.
     *
     * @param filename JSON filename (e.g., "users.json")
     * @return File object pointing to data file
     */
    public static File getDataFile(String filename) {
        ensureDataDirExists();
        return new File(DATA_DIR + File.separator + filename);
    }

    /**
     * Reads a list of objects from a JSON file.
     * <p>
     * If the file does not exist or is empty, an empty list is created & returned.
     *
     * @param file   file to read
     * @param type   TypeReference for generic list type
     * @param mapper Jackson ObjectMapper
     * @param <T>    element type
     * @return List of T (never null)
     * @throws IOException if JSON cannot be parsed
     */
    public static <T> List<T> readList(File file,
                                       TypeReference<List<T>> type,
                                       ObjectMapper mapper) throws IOException {

        if (!file.exists() || file.length() == 0) {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, List.of());
            return List.of();
        }

        return mapper.readValue(file, type);
    }

    /**
     * Writes an object to JSON using a temporary file first (atomic write).
     * <p>
     * This prevents partial writes if the JVM crashes.
     *
     * @param file   destination file
     * @param data   data to write
     * @param mapper Jackson ObjectMapper
     * @throws IOException if writing fails
     */
    public static void writeAtomic(File file, Object data, ObjectMapper mapper) throws IOException {
        Path tmp = Files.createTempFile("tmp", ".json");
        mapper.writerWithDefaultPrettyPrinter().writeValue(tmp.toFile(), data);
        Files.move(tmp, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
}
