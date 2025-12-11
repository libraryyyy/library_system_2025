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

}
