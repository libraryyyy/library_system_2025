package library_system.notification;

import library_system.domain.User;

public class EmailNotifier implements Observer {

    @Override
    public void notify(User user, String message) {
        System.out.println("[EMAIL SENT TO] " + user.getUsername() + ": " + message);
    }
}
