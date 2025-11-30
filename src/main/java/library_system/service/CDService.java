package library_system.service;


import library_system.Repository.CDRepository;
import library_system.domain.CD;

import java.util.List;

public class CDService {

    /**
     * @param cd CD to add.
     */
    public void addCD(CD cd) {

        if (cd == null) return;
        CDRepository.addCD(cd);
    }

    /**

     * @param title title to search for.
     * @return list of matching CDs.
     */
    public List<CD> searchByTitle(String title) {
        return CDRepository.findByTitle(title);
    }

    /**
     * Returns all CDs in the system.
     *
     * @return list of all CDs.
     */
    public List<CD> getAllCDs() {
        return CDRepository.getAll();
    }
}
