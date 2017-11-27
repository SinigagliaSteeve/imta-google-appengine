package com.zenika.zencontact.email;

import com.zenika.zencontact.domain.Email;
import com.zenika.zencontact.resource.auth.AuthenticationService;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by ssinigag on 27/11/17.
 */
public class EmailService {
    private static final Logger LOG = Logger.getLogger(EmailService.class.getName());
    private static EmailService ourInstance = new EmailService();

    public static EmailService getInstance() {
        return ourInstance;
    }

    private EmailService() {
    }

    public void sendEmail(Email email) {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(
                    AuthenticationService.getInstance().getUser().getEmail(),
                    AuthenticationService.getInstance().getUsername()));
            msg.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(email.to, email.toName));

            msg.setReplyTo(new Address[]{
                    new InternetAddress("team@imt-2017-11-steeve.appspotmail.com",
                            "Application team")});
            msg.setSubject(email.subject);
            msg.setText(email.body);

            Transport.send(msg);
            LOG.warning("mail envoyé!");
        } catch (AddressException e) {
        } catch (MessagingException e) {
        } catch (UnsupportedEncodingException e) {
        }
    }
}
