package library_system.notification;

import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
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
        Properties props = new Properties();
        mockSession = Session.getInstance(props);
        notifier = new EmailNotifier("sender@gmail.com", "pass12345", mockSession);
    }

    // ----------- Email validation ----------
    @Test
    void testValidEmailFormat() {
        assertTrue(notifier.isValidEmail("test@example.com"));
    }

    @Test
    void testInvalidEmailFormat() {
        assertFalse(notifier.isValidEmail("wrong-email"));
    }

    // ----------- configured() branch ----------
    @Test
    void testNotifierConfigured_True() {
        assertTrue(notifier.isConfigured());
    }

    @Test
    void testNotifierConfigured_False() {
        EmailNotifier n = new EmailNotifier("", "", null);
        assertFalse(n.isConfigured());
    }

    // ----------- Constructor using System.getenv() ----------
    @Test
    void testEnvConstructor_NotConfigured() {
        EmailNotifier n = new EmailNotifier(null, null, null);
        assertFalse(n.isConfigured());
    }

    @Test
    void testEnvConstructor_Configured() {
        Session s = Session.getInstance(new Properties());
        EmailNotifier n = new EmailNotifier("sender@gmail.com", "12345678", s);
        assertTrue(n.isConfigured());
    }


    // ----------- notify(): not configured ----------
    @Test
    void testNotify_notConfigured() {
        EmailNotifier n = new EmailNotifier("", "", null);
        assertDoesNotThrow(() -> n.notify(new User(), "msg"));
    }

    // ----------- notify(): user null ----------
    @Test
    void testNotify_userNull() {
        assertDoesNotThrow(() -> notifier.notify(null, "msg"));
    }

    // ----------- notify(): email null ----------
    @Test
    void testNotify_userEmailNull() {
        User u = new User();
        u.setEmail(null);
        assertDoesNotThrow(() -> notifier.notify(u, "msg"));
    }

    // ----------- notify(): blank email ----------
    @Test
    void testNotify_userEmailBlank() {
        User u = new User();
        u.setEmail("   ");
        assertDoesNotThrow(() -> notifier.notify(u, "msg"));
    }

    // ----------- notify(): invalid regex ----------
    @Test
    void testNotify_invalidEmailFormat() {
        User u = new User();
        u.setEmail("invalid@@mail");
        assertDoesNotThrow(() -> notifier.notify(u, "msg"));
    }

    // ----------- notify(): successful send ----------
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

    // ----------- notify(): Transport throws ----------
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
