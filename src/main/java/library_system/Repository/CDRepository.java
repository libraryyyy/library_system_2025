package library_system.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import library_system.domain.CD;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for storing and retrieving CDs.
 */
public class CDRepository {

    private static final List<CD> cds = new ArrayList<>();
    private static final ObjectMapper mapper = MapperProvider.MAPPER;
    private static final String FILE_NAME = "cds.json";
    private static final File FILE = FileUtil.getDataFile(FILE_NAME);

    /**
     * Loads CDs from JSON — repairs missing mediaType values.
     */
    public static void loadFromFile() {
        try {
            if (!FILE.exists() || FILE.length() == 0) {
                mapper.writerWithDefaultPrettyPrinter()
                        .writeValue(FILE, new ArrayList<>());
                cds.clear();
                return;
            }

            // read raw JSON
            JsonNode root = mapper.readTree(FILE);

            if (root == null || !root.isArray()) {
                cds.clear();
                mapper.writerWithDefaultPrettyPrinter()
                        .writeValue(FILE, new ArrayList<>());
                return;
            }

            ArrayNode array = (ArrayNode) root;
            boolean fixed = false;

            for (JsonNode node : array) {
                if (node instanceof ObjectNode obj) {
                    if (!obj.has("mediaType")) {
                        obj.put("mediaType", "CD");
                        fixed = true;
                    }
                }
            }

            if (fixed) {
                mapper.writerWithDefaultPrettyPrinter()
                        .writeValue(FILE, array);
            }

            // deserialize PROPERLY (important)
            List<CD> loaded = mapper.readValue(FILE, new TypeReference<List<CD>>() {});
            cds.clear();
            cds.addAll(loaded);

        } catch (Exception e) {
            System.err.println("Error loading cds.json: " + e.getMessage());
            cds.clear();
        }
    }

    /**
     * Writes CDs to JSON.
     */
    public static void saveToFile() {
        try {
            ArrayNode array = mapper.createArrayNode();
            for (CD cd : cds) {
                ObjectNode obj = mapper.valueToTree(cd);
                obj.put("mediaType", "CD");
                array.add(obj);
            }
            mapper.writerWithDefaultPrettyPrinter().writeValue(FILE, array);
        } catch (Exception e) {
            System.err.println("Error saving cds.json: " + e.getMessage());
        }
    }

    /** Add CD */
    public static void addCD(CD cd) {
        cds.add(cd);
        saveToFile();
    }

    /** Search by title or artist */
    public static List<CD> search(String keyword) {
        if (keyword == null) return new ArrayList<>();
        keyword = keyword.toLowerCase();
        List<CD> result = new ArrayList<>();
        for (CD cd : cds) {
            if (cd.getTitle().toLowerCase().contains(keyword)
                    || cd.getArtist().toLowerCase().contains(keyword)) {
                result.add(cd);
            }
        }
        return result;
    }

    /** Partial title search */
    public static List<CD> findByTitle(String title) {
        if (title == null) return new ArrayList<>();
        title = title.toLowerCase();
        List<CD> result = new ArrayList<>();
        for (CD cd : cds) {
            if (cd.getTitle().toLowerCase().contains(title)) {
                result.add(cd);
            }
        }
        return result;
    }

    /** Return ALL CDs — SAME instances */
    public static List<CD> getAll() {
        return new ArrayList<>(cds);
    }

    /** Remove CD */
    public static void removeCD(CD cd) {
        cds.remove(cd);
        saveToFile();
    }

    /** Clear repository */
    public static void clear() {
        cds.clear();
        saveToFile();
    }
}
