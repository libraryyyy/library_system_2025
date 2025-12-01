package library_system.notification;

import library_system.domain.User;

/**
 * Observer interface for sending notifications to users.
 * Implemented by classes such as {@link EmailNotifier}.
 */
public interface Observer {

    /**
     * Sends a notification message to a specific user.
     *
     * @param user    user receiving the message
     * @param message message content
     */
    void notify(User user, String message);
}
