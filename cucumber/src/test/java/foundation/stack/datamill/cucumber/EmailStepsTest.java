package foundation.stack.datamill.cucumber;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


/**
 * @author Israel Colomer (israelcolomer@gmail.com)
 */
public class EmailStepsTest {

    private final static Logger logger = LoggerFactory.getLogger(EmailStepsTest.class);
    private static final int DEFAULT_SMTP_PORT = 10025;
    private static final String EMAIL_KEY = "EMAIL_KEY";

    private static int smtpPort;
    private static String smtpHost;
    private static EmailSteps emailSteps;

    private static PropertyStore propertyStore;
    private static PlaceholderResolver placeholderResolver;

    private final static String MAIL_SUBJECT = "MAIL_SUBJECT";
    private final static String MAIL_BODY = "MAIL_BODY";
    private final static String FROM_ADDRESS = "from@mail.com";
    private final static String TO_ADDRESS = "to@mail.com";

    @BeforeClass
    public static void setUp() {
        smtpPort = TestUtil.findRandomPort(DEFAULT_SMTP_PORT);
        System.setProperty(EmailSteps.SMTP_PORT, String.valueOf(smtpPort));
        smtpHost = "localhost";

        propertyStore = new PropertyStore();
        placeholderResolver = new PlaceholderResolver(propertyStore);
        emailSteps = new EmailSteps(propertyStore, placeholderResolver);

        emailSteps.startUpServer();
    }

    @AfterClass
    public static void tearDown() {
        emailSteps.stopServer();
    }

    @Test
    public void userShouldReceiveEmailWithSubject() {
        sendMail(TO_ADDRESS, FROM_ADDRESS, MAIL_SUBJECT, MAIL_BODY);
        emailSteps.userShouldReceiveEmailWithSubject(MAIL_SUBJECT, TO_ADDRESS);
    }

    @Test
    public void userShouldReceiveEmail() {
        sendMail(TO_ADDRESS, FROM_ADDRESS, MAIL_SUBJECT, MAIL_BODY);
        emailSteps.userShouldReceiveEmail(TO_ADDRESS);
    }

    @Test
    public void storeReceivedEmail() throws Exception {
        sendMail(TO_ADDRESS, FROM_ADDRESS, MAIL_SUBJECT, MAIL_BODY);
        emailSteps.storeReceivedEmail(TO_ADDRESS, EMAIL_KEY);
        String emailContent = (String) propertyStore.get(EMAIL_KEY);
        assertThat(MAIL_BODY, is(emailContent));
    }

    public void sendMail(String to, String from, String subject, String text) {
        Session session = Session.getDefaultInstance(setupSmtpProperties());
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(text, "text/html");

            MimeMultipart mimeMultipart = new MimeMultipart();
            mimeMultipart.addBodyPart(messageBodyPart);

            message.setContent(mimeMultipart);

            Transport.send(message);
        } catch (MessagingException e) {
            logger.error("Could not send mail", e);
        }
    }

    private Properties setupSmtpProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);
        return props;
    }
}
