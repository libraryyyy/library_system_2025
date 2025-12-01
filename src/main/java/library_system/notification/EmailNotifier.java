package library_system.notification;

import library_system.domain.User;
import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

/**
 * Notification channel that sends overdue reminders via email.
 * Uses SMTP (Gmail supported) to deliver messages to users.
 */
public class EmailNotifier implements Observer {

    /** Sender email address used for SMTP authentication. */
    private final String senderEmail;

    /** Sender App Password (Gmail App Password recommended). */
    private final String senderPassword;

    /**
     * Creates a new EmailNotifier with authentication credentials.
     *
     * @param senderEmail    email address used to send notifications
     * @param senderPassword app password or SMTP password
     */
    public EmailNotifier(String senderEmail, String senderPassword) {
        this.senderEmail = senderEmail;
        this.senderPassword = senderPassword;
    }

    /**
     * Sends an HTML formatted email reminder.
     *
     * @param user    user receiving the email
     * @param message overdue reminder message
     */
    @Override
    public void notify(User user, String message) {

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            System.err.println("Cannot send email: user has no email address.");
            return;
        }

        // Basic email validation
        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            System.err.println("Invalid user email address: " + user.getEmail());
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(senderEmail));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
            msg.setSubject("Library Overdue Reminder");

            String html = """
                    <html>
                        <body style="font-family: Arial, sans-serif">
                            <h2 style="color:#2c3e50">Library Reminder</h2>
                            <p>%s</p>
                        </body>
                    </html>
                    """.formatted(message);

            msg.setContent(html, "text/html; charset=UTF-8");

            Transport.send(msg);
            System.out.println("Email sent to " + user.getEmail());

        } catch (MessagingException e) {
            System.err.println("Failed to send email to " + user.getEmail() + ": " + e.getMessage());
            // Do not rethrow — notifications should not crash the main program.
        }
    }
}
