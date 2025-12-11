package library_system.notification;

import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;
import library_system.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmailNotifierTest {

    private Session mockSession;
    private EmailNotifier notifier;

    @BeforeEach
    void setup() {
        // Fake SMTP session
        Properties props = new Properties();
        mockSession = Session.getInstance(props);

        notifier = new EmailNotifier("sender@gmail.com", "pass12345", mockSession);
    }

    // ---------- Email Validation ----------
    @Test
    void testValidEmailFormat() {
        assertTrue(notifier.isValidEmail("test@example.com"));
    }

    @Test
    void testInvalidEmailFormat() {
        assertFalse(notifier.isValidEmail("wrong-email"));
    }

    // ---------- Constructor Branch ----------
    @Test
    void testNotifierConfigured_True() {
        assertTrue(notifier.isConfigured());
    }

    @Test
    void testNotifierConfigured_False() {
        EmailNotifier n = new EmailNotifier("", "", null);
        assertFalse(n.isConfigured());
    }

    // ---------- notify(): Branch 1 → Not Configured ----------
    @Test
    void testNotify_notConfigured() {
        EmailNotifier n = new EmailNotifier("", "", null);
        assertDoesNotThrow(() -> n.notify(new User(), "msg"));
    }

    // ---------- notify(): Branch 2 → user = null ----------
    @Test
    void testNotify_userNull() {
        assertDoesNotThrow(() -> notifier.notify(null, "msg"));
    }

    // ---------- notify(): Branch 3 → user email = null ----------
    @Test
    void testNotify_userEmailNull() {
        User u = new User();
        u.setEmail(null);
        assertDoesNotThrow(() -> notifier.notify(u, "msg"));
    }

    // ---------- notify(): Branch 4 → user email blank ----------
    @Test
    void testNotify_userEmailBlank() {
        User u = new User();
        u.setEmail("  ");  // blank email
        assertDoesNotThrow(() -> notifier.notify(u, "msg"));
    }

    // ---------- notify(): Branch 5 → invalid regex ----------
    @Test
    void testNotify_invalidEmailFormat() {
        User u = new User();
        u.setEmail("invalid@@mail");
        assertDoesNotThrow(() -> notifier.notify(u, "msg"));
    }


    // ---------- notify(): Branch 7 → Transport.send successful ----------
    @Test
    void testNotify_successfulSend() {
        User u = new User();
        u.setEmail("valid@example.com");

        try (MockedStatic<Transport> mocked = mockStatic(Transport.class)) {
            mocked.when(() -> Transport.send(any(Message.class)))
                    .then(inv -> {
                        System.out.println("Mock send OK");
                        return null;
                    });

            assertDoesNotThrow(() -> notifier.notify(u, "Hello!"));

            mocked.verify(() -> Transport.send(any(Message.class)), times(1));
        }
    }

    // ---------- notify(): Branch 8 → Exception inside Transport.send ----------
    @Test
    void testNotify_sendThrowsException() {
        User u = new User();
        u.setEmail("valid@example.com");

        try (MockedStatic<Transport> mocked = mockStatic(Transport.class)) {
            mocked.when(() -> Transport.send(any(Message.class)))
                    .thenThrow(new RuntimeException("SMTP error"));

            assertDoesNotThrow(() -> notifier.notify(u, "Hello!"));
        }
    }
}
