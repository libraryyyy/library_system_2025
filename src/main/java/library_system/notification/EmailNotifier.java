package library_system.notification;

import library_system.domain.User;
import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

public class EmailNotifier implements Observer {

    private final String senderEmail;
    private final String senderPassword;

    public EmailNotifier(String senderEmail, String senderPassword) {
        this.senderEmail = senderEmail;
        this.senderPassword = senderPassword;
    }

    @Override
    public void notify(User user, String messageBody) {

        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                            senderEmail,     // <-- from constructor
                            senderPassword   // <-- App Password
                    );
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(user.getEmail()) // <-- real recipient
            );
            message.setSubject("Library Notification");
            message.setText(messageBody);

            Transport.send(message);

            System.out.println("Email sent to: " + user.getEmail());

        } catch (Exception e) {
            System.out.println("Failed to send email to " + user.getEmail() + ": " + e.getMessage());
        }
    }
}
