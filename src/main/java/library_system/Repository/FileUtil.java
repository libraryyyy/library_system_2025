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
 * Small helper to manage a writable data directory and read/write JSON files safely.
 * <p>
 * All data files are stored under {@link #DATA_DIR} (default: src/main/resources).
 * The helper creates missing files and performs atomic writes to prevent corruption.
 * </p>
 */
public class FileUtil {

    public static final String DATA_DIR = "src/main/resources";

    public static void ensureDataDirExists() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            // create directories, including parents
            boolean ok = dir.mkdirs();
            if (!ok && !dir.exists()) {
                // fall back silently; subsequent file writes will fail with a clear exception
            }
        }
    }

    /**
     * Returns a File handle for the given filename within the data directory.
     * Ensures the directory exists before returning.
     *
     * @param filename json filename (e.g., "books.json")
     * @return File object for the data file
     */
    public static File getDataFile(String filename) {
        ensureDataDirExists();
        return new File(DATA_DIR + File.separator + filename);
    }

    /**
     * Reads a list of T from the specified file using the provided ObjectMapper.
     * If the file does not exist or is empty, an empty list is created and returned.
     *
     * @param file   file to read
     * @param type   TypeReference for the list element type
     * @param mapper object mapper to use
     * @param <T>    item type
     * @return loaded list (never null)
     * @throws IOException on IO issues
     */
    public static <T> List<T> readList(File file, TypeReference<List<T>> type, ObjectMapper mapper) throws IOException {
        if (!file.exists() || file.length() == 0) {
            // create empty list file
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, List.of());
            return List.of();
        }
        return mapper.readValue(file, type);
    }

    /**
     * Writes the given data atomically to the file using a temporary file then moving it.
     *
     * @param file   target file
     * @param data   data to serialize
     * @param mapper object mapper to use
     * @throws IOException on IO errors
     */
    public static void writeAtomic(File file, Object data, ObjectMapper mapper) throws IOException {
        // write to temporary file then move to target for atomic-ish save
        Path tmp = Files.createTempFile("tmp", ".json");
        mapper.writerWithDefaultPrettyPrinter().writeValue(tmp.toFile(), data);
        Files.move(tmp, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
}
