package library_system.service;


import library_system.Repository.CDRepository;
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
     * Returns all CDs in the system.
     *
     * @return list containing all CDs
     */
    public List<CD> getAllCDs() {
        return CDRepository.getAll();
    }
}
