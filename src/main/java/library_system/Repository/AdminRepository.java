package library_system.repository;

import library_system.domain.Admin;

/**
 * Repository for accessing administrator-related data.
 * In this simplified version, it holds a single hardcoded admin instance.
 */
public class AdminRepository {

    /** Single admin instance used by the system. */
    private static final Admin admin = new Admin("admin", "1234");

    /**
     * Returns the single administrator instance.
     *
     * @return the system admin.
     */
    public static Admin getAdmin() {
        return admin;
    }
}
