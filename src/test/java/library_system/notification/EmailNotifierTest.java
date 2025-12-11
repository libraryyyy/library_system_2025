package library_system.notification;

import library_system.domain.User;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmailNotifierTest {

    private Session mockSession;
    private EmailNotifier notifier;

    @BeforeEach
    void setup() {
        // Mock SMTP Session
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        mockSession = Session.getInstance(props);

        notifier = new EmailNotifier("testsender@gmail.com", "password123", mockSession);
    }

    @Test
    void testValidEmail() {
        assertTrue(notifier.isValidEmail("user@example.com"));
    }

    @Test
    void testInvalidEmail() {
        assertFalse(notifier.isValidEmail("wrong-email"));
    }

    @Test
    void testNotifierConfigured() {
        assertTrue(notifier.isConfigured());
    }

    @Test
    void testNotifyWithInvalidUserEmail() {
        User user = new User();
        user.setEmail("");  // invalid

        notifier.notify(user, "Hello");

        // No exception should be thrown
    }

    @Test
    void testNotifyWithNullUser() {
        notifier.notify(null, "Hello");

        // Should print “Invalid user email.” but no crash
    }

    @Test
    void testNotifyEmailSending() throws Exception {
        // Mock Transport.send
        Transport transportMock = mock(Transport.class);
        Transport originalTransport = Transport.class.cast(null);

        User user = new User();
        user.setEmail("validuser@example.com");

        // Create message so we can test sending path
        MimeMessage msg = new MimeMessage(mockSession);

        // Use Mockito to intercept the static method Transport.send
        Mockito.mockStatic(Transport.class).when(() -> Transport.send(any(Message.class)))
                .thenAnswer(inv -> {
                    System.out.println("Mock email sent.");
                    return null;
                });

        notifier.notify(user, "This is a test message.");

        // If we reach here with no exception → test passed
    }
}
