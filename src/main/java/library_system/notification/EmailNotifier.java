package library_system.notification;

import library_system.domain.User;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailNotifier implements Observer {

    private final String username; // البريد لإرسال الرسائل
    private final String password; // كلمة المرور أو App Password

    public EmailNotifier(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void notify(User user, String message) {
        System.out.println("[EMAIL SENT TO] " + user.getUsername() + " (" + user.getEmail() + "): " + message);

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(username));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
            msg.setSubject("Library Overdue Reminder");
            msg.setText(message);

            Transport.send(msg);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
