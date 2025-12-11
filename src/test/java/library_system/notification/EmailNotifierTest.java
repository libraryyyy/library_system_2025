package library_system.notification;

import library_system.domain.User;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailNotifierTest {

    private Session createFakeSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.test.com");
        props.put("mail.smtp.auth", "false");
        return Session.getInstance(props);
    }

    @Test
    void testConstructor_notConfigured() {
        EmailNotifier notifier = new EmailNotifier(null, null, null);
        assertFalse(notifier.isConfigured());
    }

    @Test
    void testConstructor_configured() {
        Session session = createFakeSession();
        EmailNotifier notifier = new EmailNotifier("sender@test.com", "123", session);
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
        assertFalse(notifier.isValidEmail("badEmail"));
    }

    @Test
    void testNotify_notConfigured() {
        EmailNotifier notifier = new EmailNotifier(null, null, null);
        notifier.notify(new User(), "Hello");
    }

    @Test
    void testNotify_userNullEmail() {
        EmailNotifier notifier = new EmailNotifier("x@y.com", "123", createFakeSession());

        User user = new User();
        user.setEmail(null);

        notifier.notify(user, "Hello");
    }

    @Test
    void testNotify_invalidEmailFormat() {
        EmailNotifier notifier = new EmailNotifier("x@y.com", "123", createFakeSession());

        User user = new User();
        user.setEmail("invalid-email");

        notifier.notify(user, "Hello");
    }

    @Test
    void testNotify_success_mockedTransport() {
        Session session = createFakeSession();

        EmailNotifier notifier = new EmailNotifier(
                "sender@test.com",
                "123",
                session
        );

        User user = new User();
        user.setEmail("valid@test.com");

        // === أهم جزء: Mock لدالة Transport.send() ===
        try (MockedStatic<Transport> mockedTransport = mockStatic(Transport.class)) {

            mockedTransport
                    .when(() -> Transport.send(any(Message.class)))
                    .thenAnswer(invocation -> null);

            notifier.notify(user, "Hello!");

            // التأكد من استدعاء الإرسال
            mockedTransport.verify(() -> Transport.send(any(Message.class)), times(1));
        }
    }

    @Test
    void testNotify_exceptionDuringMessageBuilding() throws Exception {

        Session session = createFakeSession();
        EmailNotifier notifier = new EmailNotifier("sender@test.com", "123", session);

        MimeMessage msg = spy(new MimeMessage(session));
        doThrow(new MessagingException("forced error")).when(msg).saveChanges();

        User user = new User();
        user.setEmail("valid@test.com");

        // Mock Transport.send لكي لا يخرج Error حقيقي
        try (MockedStatic<Transport> mockedTransport = mockStatic(Transport.class)) {
            mockedTransport.when(() -> Transport.send(any(Message.class))).thenAnswer(invocation -> null);

            notifier.notify(user, "Hello");
        }
    }
}
