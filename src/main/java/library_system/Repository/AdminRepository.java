package library_system.Repository;

import library_system.domain.Admin;

public class AdminRepository {

    private static final Admin admin = new Admin("admin", "1234");

    public static Admin getAdmin() {
        return admin;
    }
}
