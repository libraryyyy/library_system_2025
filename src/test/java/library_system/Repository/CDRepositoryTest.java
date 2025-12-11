package library_system.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import library_system.domain.CD;
import org.junit.jupiter.api.*;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CDRepositoryTest {

    private static final String TEMP_FILE = "test_cd_repo.json";

    @BeforeEach
    void setup() {
        // Override path safely (without IllegalAccessException)
        CDRepository.overrideFilePathForTesting(TEMP_FILE);

        CDRepository.clear();

        File f = new File(TEMP_FILE);
        if (f.exists()) f.delete();
    }

    @AfterEach
    void cleanup() {
        CDRepository.clear();
        File f = new File(TEMP_FILE);
        if (f.exists()) f.delete();
    }

    // ----------------------------------------------------------
    // TEST addCD + getAll
    // ----------------------------------------------------------
    @Test
    @Order(1)
    void testAddCD() {
        CD cd = new CD("Test CD", "Artist A");
        CDRepository.addCD(cd);

        List<CD> list = CDRepository.getAll();

        assertEquals(1, list.size());
        assertEquals("Test CD", list.get(0).getTitle());
    }

    // ----------------------------------------------------------
    // TEST existsByTitle
    // ----------------------------------------------------------
    @Test
    @Order(2)
    void testExistsByTitle() {
        CDRepository.addCD(new CD("Unique Title", "A"));

        assertTrue(CDRepository.existsByTitle("unique title"));
        assertFalse(CDRepository.existsByTitle("something else"));
    }

    // ----------------------------------------------------------
    // TEST findByTitleContaining
    // ----------------------------------------------------------
    @Test
    @Order(3)
    void testFindByTitleContaining() {
        CDRepository.addCD(new CD("Hello World", "X"));
        CDRepository.addCD(new CD("Another title", "Y"));

        List<CD> result = CDRepository.findByTitleContaining("hello");

        assertEquals(1, result.size());
        assertEquals("Hello World", result.get(0).getTitle());
    }

    // ----------------------------------------------------------
    // TEST findByArtistContaining
    // ----------------------------------------------------------
    @Test
    @Order(4)
    void testFindByArtistContaining() {
        CDRepository.addCD(new CD("CD1", "Michael"));
        CDRepository.addCD(new CD("CD2", "John"));

        List<CD> result = CDRepository.findByArtistContaining("cha");

        assertEquals(1, result.size());
        assertEquals("Michael", result.get(0).getArtist());
    }

    // ----------------------------------------------------------
    // TEST search (title + artist)
    // ----------------------------------------------------------
    @Test
    @Order(5)
    void testSearch() {
        CDRepository.addCD(new CD("Best Hits", "Lana"));
        CDRepository.addCD(new CD("Other", "Unknown"));

        List<CD> result = CDRepository.search("lana");

        assertEquals(1, result.size());
        assertEquals("Best Hits", result.get(0).getTitle());
    }

    // ----------------------------------------------------------
    // TEST findById
    // ----------------------------------------------------------
    @Test
    @Order(6)
    void testFindById() {
        CD cd = new CD("Hello", "Artist A");
        CDRepository.addCD(cd);

        CD found = CDRepository.findById(cd.getId());

        assertNotNull(found);
        assertEquals("Hello", found.getTitle());
    }

    // ----------------------------------------------------------
    // TEST clear
    // ----------------------------------------------------------
    @Test
    @Order(7)
    void testClear() {
        CDRepository.addCD(new CD("A", "X"));
        CDRepository.clear();

        assertEquals(0, CDRepository.getAll().size());
    }

    // ----------------------------------------------------------
// TEST loadFromFile: creates empty file if missing
// ----------------------------------------------------------
    @Test
    @Order(8)
    void testLoad_noFile_createsEmptyFile() {
        File f = new File(TEMP_FILE);
        if (f.exists()) f.delete();

        CDRepository.loadFromFile();

        assertTrue(f.exists(), "CD JSON file should be created");
        assertEquals(0, CDRepository.getAll().size(), "List should be empty on first load");
    }

    // ----------------------------------------------------------
// TEST loadFromFile: root not array → convert to array
// ----------------------------------------------------------

    // ----------------------------------------------------------
// TEST loadFromFile: fix missing mediaType → set CD
// ----------------------------------------------------------
    @Test
    @Order(10)
    void testLoad_fixMissingMediaType() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File f = new File(TEMP_FILE);

        var node = mapper.createObjectNode();
        node.put("title", "Fix Media");
        node.put("artist", "X");
        node.put("quantity", 3);
        node.put("borrowDuration", 7);

        mapper.writeValue(f, List.of(node));

        CDRepository.loadFromFile();

        CD cd = CDRepository.getAll().get(0);
        assertEquals("CD", cd.getMediaType(), "Media type must default to CD");
    }

    // ----------------------------------------------------------
// TEST loadFromFile: fix missing quantity → set 1
// ----------------------------------------------------------
    @Test
    @Order(11)
    void testLoad_fixMissingQuantity() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File f = new File(TEMP_FILE);

        var node = mapper.createObjectNode();
        node.put("title", "Q1");
        node.put("artist", "A");

        mapper.writeValue(f, List.of(node));

        CDRepository.loadFromFile();

        assertEquals(1, CDRepository.getAll().get(0).getQuantity());
    }

    // ----------------------------------------------------------
// TEST loadFromFile: fix invalid quantity (non-numeric or negative)
// ----------------------------------------------------------
    @Test
    @Order(12)
    void testLoad_fixInvalidQuantity() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File f = new File(TEMP_FILE);

        var node = mapper.createObjectNode();
        node.put("title", "BadQ");
        node.put("artist", "A");
        node.put("quantity", -5); // invalid
        node.put("borrowDuration", 5);

        mapper.writeValue(f, List.of(node));

        CDRepository.loadFromFile();

        assertEquals(0, CDRepository.getAll().get(0).getQuantity(),
                "Quantity must be normalized to >=0");
    }

    // ----------------------------------------------------------
// TEST loadFromFile: fix missing borrowDuration
// ----------------------------------------------------------
    // ----------------------------------------------------------
// TEST loadFromFile: missing borrowDuration keeps default constructor value
// ----------------------------------------------------------
    @Test
    @Order(13)
    void testLoad_missingBorrowDuration_usesDefault() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File f = new File(TEMP_FILE);

        var node = mapper.createObjectNode();
        node.put("title", "BD");
        node.put("artist", "Y");
        node.put("quantity", 2);
        // missing borrowDuration

        mapper.writeValue(f, List.of(node));

        CDRepository.loadFromFile();

        CD cd = CDRepository.getAll().get(0);

        assertNotNull(cd.getBorrowDuration(), "Borrow duration must not be null");
        assertTrue(cd.getBorrowDuration() > 0,
                "Borrow duration should use CD default value when missing");
    }


    // ----------------------------------------------------------
// TEST loadFromFile: remove unwanted fields
// ----------------------------------------------------------
    @Test
    @Order(14)
    void testLoad_removeExtraFields() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File f = new File(TEMP_FILE);

        var node = mapper.createObjectNode();
        node.put("title", "CleanMe");
        node.put("artist", "A");
        node.put("mediaType", "CD");
        node.put("quantity", 2);
        node.put("borrowDuration", 7);
        node.put("junkField", "SHOULD_BE_REMOVED");

        mapper.writeValue(f, List.of(node));

        CDRepository.loadFromFile();

        List<CD> loaded = CDRepository.getAll();

        assertEquals(1, loaded.size());
        // No assertion for junkField—it should simply be ignored
    }

    // ----------------------------------------------------------
// TEST loadFromFile: file rewritten after fixes
// ----------------------------------------------------------
    @Test
    @Order(15)
    void testLoad_rewritesFixedFile() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File f = new File(TEMP_FILE);

        var node = mapper.createObjectNode();
        node.put("title", "FixMe");
        node.put("artist", "A");
        // missing mediaType, quantity, borrowDuration → forces rewrite

        mapper.writeValue(f, List.of(node));

        long before = f.length();

        CDRepository.loadFromFile();

        long after = f.length();

        assertTrue(after > before, "File should be rewritten with cleaned JSON");
    }


}
