package library_system.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import library_system.domain.CD;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for CD objects with JSON persistence and auto-repair of missing fields.
 */
public class CDRepository {

    private static final List<CD> cds = new ArrayList<>();
    private static final String FILE_PATH = "src/main/resources/cds.json";

    private static final ObjectMapper mapper = MapperProvider.MAPPER;

    /**
     * Loads CDs from file and repairs missing mediaType/quantity fields and removes unwanted fields.
     */
    public static void loadFromFile() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists() || file.length() == 0) {
                FileUtil.ensureDataDirExists();
                mapper.writerWithDefaultPrettyPrinter().writeValue(file, List.of());
                cds.clear();
                return;
            }

            JsonNode root = mapper.readTree(file);
            ArrayNode array;
            if (root == null || root.isNull()) {
                array = mapper.createArrayNode();
            } else if (root.isArray()) {
                array = (ArrayNode) root;
            } else {
                array = mapper.createArrayNode();
                array.add(root);
            }

            boolean fixed = false;
            ArrayNode cleaned = mapper.createArrayNode();
            for (JsonNode node : array) {
                if (node == null || !node.isObject()) continue;
                ObjectNode obj = (ObjectNode) node;
                ObjectNode clean = mapper.createObjectNode();
                // Ensure mediaType
                if (!obj.has("mediaType") || obj.get("mediaType").isNull() || obj.get("mediaType").asText().isEmpty()) {
                    clean.put("mediaType", "CD");
                    fixed = true;
                } else clean.put("mediaType", obj.get("mediaType").asText());
                if (obj.has("title")) clean.put("title", obj.get("title").asText());
                if (obj.has("artist")) clean.put("artist", obj.get("artist").asText());
                if (obj.has("quantity") && obj.get("quantity").canConvertToInt()) clean.put("quantity", obj.get("quantity").asInt());
                else { clean.put("quantity", 1); fixed = true; }
                if (obj.has("borrowDuration")) clean.put("borrowDuration", obj.get("borrowDuration").asInt());
                cleaned.add(clean);
            }

            if (fixed) mapper.writerWithDefaultPrettyPrinter().writeValue(file, cleaned);

            List<CD> loaded = mapper.readValue(file, new TypeReference<List<CD>>() {});
            cds.clear();
            cds.addAll(loaded);

            for (CD c : cds) if (c != null) c.setQuantity(Math.max(0, c.getQuantity()));

        } catch (Exception e) {
            System.err.println("Error loading CDs: " + e.getMessage());
            cds.clear();
        }
    }

    /**
     * Saves CDs to JSON with cleaned structure.
     */
    public static void saveToFile() {
        try {
            ArrayNode arr = mapper.createArrayNode();
            for (CD c : cds) {
                ObjectNode obj = mapper.createObjectNode();
                obj.put("mediaType", "CD");
                if (c.getTitle() != null) obj.put("title", c.getTitle());
                if (c.getArtist() != null) obj.put("artist", c.getArtist());
                obj.put("quantity", c.getQuantity());
                obj.put("borrowDuration", c.getBorrowDuration());
                arr.add(obj);
            }
            FileUtil.writeAtomic(new File(FILE_PATH), arr, mapper);
        } catch (Exception e) {
            System.err.println("Error saving CDs: " + e.getMessage());
        }
    }

    /**
     * Returns the live in-memory list of CDs.
     *
     * @return live list
     */
    public static List<CD> getAll() {
        return cds;
    }

    /**
     * Adds a CD and persists.
     *
     * @param cd CD to add
     */
    public static void addCD(CD cd) {
        if (cd == null) return;
        if (cd.getQuantity() <= 0) cd.setQuantity(1);
        cds.add(cd);
        saveToFile();
    }

    /**
     * Clears CDs and persists.
     */
    public static void clear() {
        cds.clear();
        saveToFile();
    }

    /**
     * Case-insensitive partial match on title.
     *
     * @param part substring
     * @return matching CDs
     */
    public static List<CD> findByTitleContaining(String part) {
        if (part == null || part.isBlank()) return new ArrayList<>(cds);
        String lower = part.toLowerCase();
        List<CD> res = new ArrayList<>();
        for (CD c : cds) {
            if (c.getTitle() != null && c.getTitle().toLowerCase().contains(lower)) res.add(c);
        }
        return res;
    }

    /**
     * Case-insensitive partial match on artist.
     *
     * @param part substring
     * @return matching CDs
     */
    public static List<CD> findByArtistContaining(String part) {
        if (part == null || part.isBlank()) return new ArrayList<>(cds);
        String lower = part.toLowerCase();
        List<CD> res = new ArrayList<>();
        for (CD c : cds) if (c.getArtist() != null && c.getArtist().toLowerCase().contains(lower)) res.add(c);
        return res;
    }

    /**
     * General search across title and artist.
     *
     * @param keyword keyword
     * @return matching CDs
     */
    public static List<CD> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return new ArrayList<>(cds);
        String lower = keyword.toLowerCase();
        List<CD> res = new ArrayList<>();
        for (CD c : cds) if ((c.getTitle() != null && c.getTitle().toLowerCase().contains(lower)) || (c.getArtist() != null && c.getArtist().toLowerCase().contains(lower))) res.add(c);
        return res;
    }

    /**
     * Check existence by exact title (case-insensitive, trimmed).
     * Used to enforce uniqueness when adding CDs.
     *
     * @param title title to check
     * @return true if a CD with the same title exists
     */
    public static boolean existsByTitle(String title) {
        if (title == null) return false;
        String t = title.trim().toLowerCase();
        for (CD c : cds) {
            if (c.getTitle() != null && c.getTitle().trim().toLowerCase().equals(t)) return true;
        }
        return false;
    }

    /**
     * Finds a CD by id.
     *
     * @param id id
     * @return CD or null
     */
    public static CD findById(String id) {
        if (id == null) return null;
        for (CD c : cds) if (c.getId().equals(id)) return c;
        return null;
    }}