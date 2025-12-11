package library_system.service;

import library_system.repository.CDRepository;
import library_system.domain.CD;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CDServiceTest {

    private CDService cdService;

    @BeforeEach
    void setup() throws Exception {
        cdService = new CDService();

        // Clear repository before every test
        var cdsField = CDRepository.class.getDeclaredField("cds");
        cdsField.setAccessible(true);
        ((List<?>) cdsField.get(null)).clear();
    }

    @Test
    void testAddCD_Successful() {
        CD cd = new CD("Thriller", "Michael Jackson");
        cdService.addCD(cd);

        List<CD> all = cdService.getAllCDs();
        assertEquals(1, all.size());
        assertEquals("Thriller", all.get(0).getTitle());
        assertEquals("Michael Jackson", all.get(0).getArtist());
    }

    @Test
    void testAddCD_NullIgnored() {
        cdService.addCD(null);
        assertEquals(0, cdService.getAllCDs().size());
    }

    @Test
    void testAddCD_BlankFieldsRejected() {
        CD cd = new CD("", "");
        cdService.addCD(cd);

        assertEquals(0, cdService.getAllCDs().size());
    }

    @Test
    void testAddCD_DuplicateTitleRejected() {
        CD cd1 = new CD("Hybrid Theory", "Linkin Park");
        CD cd2 = new CD("Hybrid Theory", "Different Artist");

        cdService.addCD(cd1);
        cdService.addCD(cd2);

        // Only one should exist
        assertEquals(1, cdService.getAllCDs().size());
    }

    @Test
    void testSearch_ByTitleOrArtist() {
        cdService.addCD(new CD("Fearless", "Taylor Swift"));
        cdService.addCD(new CD("1989", "Taylor Swift"));
        cdService.addCD(new CD("Divide", "Ed Sheeran"));

        List<CD> result = cdService.search("taylor");

        assertEquals(2, result.size());
    }

    @Test
    void testSearchByTitle() {
        cdService.addCD(new CD("The Wall", "Pink Floyd"));
        cdService.addCD(new CD("Wallflower", "Diana Krall"));

        List<CD> result = cdService.searchByTitle("Wall");

        assertEquals(2, result.size());
    }

    @Test
    void testSearchByArtist() {
        cdService.addCD(new CD("Revival", "Eminem"));
        cdService.addCD(new CD("Kamikaze", "Eminem"));
        cdService.addCD(new CD("Scorpion", "Drake"));

        List<CD> result = cdService.searchByArtist("Eminem");

        assertEquals(2, result.size());
    }
}
