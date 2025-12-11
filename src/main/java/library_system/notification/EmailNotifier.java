package library_system.notification;

import library_system.domain.User;
import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;
import java.util.regex.Pattern;
/**
 * Email notifier which sends notifications to users using Gmail SMTP.
 *
 * Credentials are read from environment variables via System.getenv(). This class
 * handles missing credentials and SMTP/authentication errors and prints clear
 * status messages without throwing exceptions to the caller.
 */
public class EmailNotifier implements Observer {

    private final String senderEmail;
    private final String senderPassword;
    private final Session session;
    private final boolean configured;
    private static final Pattern SIMPLE_EMAIL_REGEX = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    /**
     * Initializes email notifier using environment variables EMAIL_USERNAME and EMAIL_PASSWORD.
     */
    public EmailNotifier() {
        this.senderEmail = System.getenv("EMAIL_USERNAME");
        this.senderPassword = System.getenv("EMAIL_PASSWORD");

        if (senderEmail == null || senderEmail.isBlank() || senderPassword == null || senderPassword.isBlank()) {
            System.out.println("Email notifier not configured: set EMAIL_USERNAME and EMAIL_PASSWORD as environment variables to enable email sending.");
            this.session = null;
            this.configured = false;
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        this.session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });
        this.configured = true;
    }

    public EmailNotifier(String email, String password, Session session) {
        this.senderEmail = email;
        this.senderPassword = password;
        this.session = session;
        this.configured = (email != null && !email.isBlank() && password != null && !password.isBlank() && session != null);
    }

    private static Session createSession(String email, String password) {
        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            return null;
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        });
    }

    @Override
    public void notify(User user, String messageBody) {
        if (!configured) {
            System.out.println("Email notifier not configured.");
            return;
        }

        if (user == null || user.getEmail() == null || user.getEmail().isBlank()) {
            System.out.println("Invalid user email.");
            return;
        }

        String recipient = user.getEmail().trim();
        if (!SIMPLE_EMAIL_REGEX.matcher(recipient).matches()) {
            System.out.println("Invalid email address: " + recipient);
            return;
        }

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject("Library Notification");
            message.setText(messageBody == null ? "" : messageBody);

            Transport.send(message);
            System.out.println("Email sent successfully to: " + recipient);

        } catch (Exception e) {
            System.out.println("Error sending email: " + e.getMessage());
        }
    }

    public boolean isValidEmail(String email) {
        return SIMPLE_EMAIL_REGEX.matcher(email).matches();
    }

    /**
     * Returns true when the notifier was successfully configured with environment credentials.
     *
     * @return true if EMAIL_USERNAME and EMAIL_PASSWORD were provided and session created
     */
    public boolean isConfigured() {
        return this.configured;
    }
}