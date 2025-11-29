package library_system.Repository;

import library_system.domain.CD;

import java.util.ArrayList;
import java.util.List;

public class CDRepository {

    private static final List<CD> cds = new ArrayList<>();

    public static void addCD(CD cd) {
        cds.add(cd);
    }

    public static CD findOneByTitle(String title) {
        for (CD cd : cds) {
            if (cd.getTitle().equalsIgnoreCase(title)) {
                return cd;
            }
        }
        return null;
    }

    public static List<CD> findByTitle(String title) {
        List<CD> result = new ArrayList<>();
        for (CD cd : cds) {
            if (cd.getTitle().equalsIgnoreCase(title)) {
                result.add(cd);
            }
        }
        return result;
    }

    public static List<CD> getAll() {
        return cds;
    }

    public static void removeCD(CD cd) {
        cds.remove(cd);
    }

    public static void clear() {
        cds.clear();
    }
}
