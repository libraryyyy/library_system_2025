package library_system.notification;

import library_system.domain.User;

public interface Observer {
    void notify(User user, String message);
}
