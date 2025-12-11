package library_system.service;


import library_system.repository.CDRepository;
import library_system.domain.CD;

import java.util.List;

/**
 * Service layer for CD operations.
 */
public class CDService {

    /**
     * Adds a CD to the repository.
     *
     * @param cd CD to add; ignored if null
     */
    public void addCD(CD cd) {

        if (cd == null) return;

        // Validate required fields
        if (cd.getTitle() == null || cd.getTitle().isBlank() || cd.getArtist() == null || cd.getArtist().isBlank()) {
            System.out.println("Error: CD title and artist are required and must not be blank.");
            return;
        }

        // Uniqueness check: title must be unique
        if (CDRepository.existsByTitle(cd.getTitle())) {
            System.out.println("A CD with this title already exists.");
            return;
        }

        CDRepository.addCD(cd);
    }

    /**
     * Searches for CDs by keyword in title or artist.
     *
     * @param keyword search keyword (partial, case-insensitive)
     * @return list of matching CDs (may be empty)
     */
    public List<CD> search(String keyword) {
        return CDRepository.search(keyword);
    }

    /**
     * Search CDs by title only (does not match artist).
     *
     * @param title substring to search in title
     * @return matching CDs
     */
    public List<CD> searchByTitle(String title) {
        return CDRepository.findByTitleContaining(title);
    }

    /**
     * Search CDs by artist only (does not match title).
     *
     * @param artist substring to search in artist
     * @return matching CDs
     */
    public List<CD> searchByArtist(String artist) {
        return CDRepository.findByArtistContaining(artist);
    }

    /**
     * Returns all CDs in the system.
     *
     * @return list containing all CDs
     */
    public List<CD> getAllCDs() {
        return CDRepository.getAll();
    }
}
