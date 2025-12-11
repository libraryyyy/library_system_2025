package library_system.repository;

import library_system.domain.CD;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class CDRepositoryTest {

    @BeforeEach
    public void setup() {
        CDRepository.clear();
    }

    @AfterEach
    public void tearDown() {
        CDRepository.clear();
    }

    @Test
    public void testAddAndSearchCD() {
        CDRepository.addCD(new CD("Greatest Hits", "Artist A"));
        CDRepository.addCD(new CD("Hits Vol 2", "Artist B"));

        List<CD> byTitle = CDRepository.search("hits");
        Assertions.assertEquals(2, byTitle.size());

        List<CD> byArtist = CDRepository.search("Artist A");
        Assertions.assertEquals(1, byArtist.size());
    }
}

