package library_system.service;

import library_system.Repository.UserRepository;
import library_system.domain.User;

/**
 * Service that manages user registration, login, and session state.
 */
public class UserService {

    /** Currently logged-in user or null if none. */
    private User loggedUser = null;

    /**
     * Registers a new user if the username is not already taken.
     *
     * @param username desired username.
     * @param password desired password.
     * @return true if registration succeeded; false if username already exists.
     */
    public boolean register(String username, String password) {
        if (UserRepository.findUser(username) != null) {
            return false;
        }
        UserRepository.addUser(new User(username, password));
        return true;
    }

    /**
     * Attempts to log in a user with the given credentials.
     *
     * @param username entered username.
     * @param password entered password.
     * @return true if login is successful; false otherwise.
     */
    public boolean login(String username, String password) {
        User user = UserRepository.findUser(username);
        if (user != null && user.getPassword().equals(password)) {
            loggedUser = user;
            return true;
        }
        return false;
    }

    /**
     * @return the currently logged-in user, or null if none.
     */
    public User getLoggedUser() {
        return loggedUser;
    }

    /**
     * Logs out the current user.
     */
    public void logout() {
        loggedUser = null;
    }
}
