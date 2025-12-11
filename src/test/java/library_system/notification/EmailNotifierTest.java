package library_system.notification;

import library_system.domain.User;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailNotifierTest {

    private Session mockSession;
    private Transport mockTransport;

    private Session createMockSession() throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.test.com");
        props.put("mail.smtp.auth", "false");

        Session session = Session.getInstance(props);

        // Mock transport static send
        mockTransport = mock(Transport.class);
        Session.setDefaultInstance(props, null);

        return session;
    }

    @Test
    void testConstructor_notConfigured_whenEnvMissing() {
        EmailNotifier notifier = new EmailNotifier(null, null, null);
        assertFalse(notifier.isConfigured());
    }

    @Test
    void testConstructor_configured_whenEmailPasswordSessionProvided() {
        EmailNotifier notifier = new EmailNotifier("a@test.com", "123", mock(Session.class));
        assertTrue(notifier.isConfigured());
    }

    @Test
    void testIsValidEmail_valid() {
        EmailNotifier notifier = new EmailNotifier("", "", null);
        assertTrue(notifier.isValidEmail("test@gmail.com"));
    }

    @Test
    void testIsValidEmail_invalid() {
        EmailNotifier notifier = new EmailNotifier("", "", null);
        assertFalse(notifier.isValidEmail("notAnEmail"));
    }

    @Test
    void testNotify_notConfigured() {
        EmailNotifier notifier = new EmailNotifier(null, null, null);

        User user = new User();
        user.setEmail("valid@test.com");

        notifier.notify(user, "Hello"); // فقط نتأكد أنه لا يرمي exceptions
    }

    @Test
    void testNotify_invalidUserEmail_null() {
        EmailNotifier notifier = new EmailNotifier("x@y.com", "123", mock(Session.class));

        User user = new User();
        user.setEmail(null);

        notifier.notify(user, "Hello");
    }

    @Test
    void testNotify_invalidUserEmail_empty() {
        EmailNotifier notifier = new EmailNotifier("x@y.com", "123", mock(Session.class));

        User user = new User();
        user.setEmail("");

        notifier.notify(user, "Hello");
    }

    @Test
    void testNotify_invalidEmailFormat() {
        EmailNotifier notifier = new EmailNotifier("x@y.com", "123", mock(Session.class));

        User user = new User();
        user.setEmail("invalid-email");

        notifier.notify(user, "Hello");
    }

    @Test
    void testNotify_successful() throws Exception {
        Session session = createMockSession();

        EmailNotifier notifier = new EmailNotifier("sender@test.com", "pass", session);

        MimeMessage message = spy(new MimeMessage(session));
        doNothing().when(mockTransport).sendMessage(any(), any());

        User user = new User();
        user.setEmail("valid@test.com");

        notifier.notify(user, "Hello");
    }

    @Test
    void testNotify_transportThrowsException() throws Exception {
        Session session = createMockSession();

        EmailNotifier notifier = new EmailNotifier("sender@test.com", "pass", session);

        MimeMessage message = spy(new MimeMessage(session));
        doThrow(new MessagingException("SMTP error"))
                .when(mockTransport).sendMessage(any(), any());

        User user = new User();
        user.setEmail("valid@test.com");

        notifier.notify(user, "Hello"); // يمسك exception ويطبع error
    }
}
