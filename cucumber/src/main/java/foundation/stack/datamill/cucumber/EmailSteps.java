package foundation.stack.datamill.cucumber;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class EmailSteps {

    private final static Logger logger = LoggerFactory.getLogger(EmailSteps.class);

    private final PlaceholderResolver placeholderResolver;
    private final PropertyStore propertyStore;
    private Wiser smtpServer;

    private final int DEFAULT_SMTP_PORT = 1025;
    public final static String SMTP_PORT = "SMTP_PORT";

    public EmailSteps(PropertyStore propertyStore, PlaceholderResolver placeholderResolver) {
        this.propertyStore = propertyStore;
        this.placeholderResolver = placeholderResolver;
    }

    @Before("@emailing")
    public void startUpServer() {
        smtpServer = new Wiser(getSmtpPort());
        smtpServer.start();
    }

    private int getSmtpPort() {
        String smtpPort = System.getProperty(SMTP_PORT);
        if (smtpPort != null) {
            try {
                return Integer.valueOf(smtpPort);
            } catch (Exception e) {
                logger.debug("Could not parse SMTP port {}", smtpPort);
            }
        }
        return DEFAULT_SMTP_PORT;
    }

    private String getCompleteContent(Message message) throws IOException, MessagingException {
        StringBuilder completeContent = new StringBuilder();

        MimeMultipart contents = (MimeMultipart) message.getContent();
        for (int i = 0; i < contents.getCount(); i++) {
            BodyPart part = contents.getBodyPart(i);
            String partText = getPartTextContent(part);

            completeContent.append(partText);
        }

        return completeContent.toString();
    }

    private String getPartTextContent(Part part) throws
            MessagingException, IOException {
        if (part.isMimeType("text/*")) {
            return (String) part.getContent();
        }

        if (part.isMimeType("multipart/alternative")) {
            Multipart multipart = (Multipart) part.getContent();
            String text = null;
            for (int i = 0; i < multipart.getCount(); i++) {
                Part body = multipart.getBodyPart(i);
                if (body.isMimeType("text/plain")) {
                    if (text == null) {
                        text = getPartTextContent(body);
                    }

                    continue;
                } else if (body.isMimeType("text/html")) {
                    String html = getPartTextContent(body);
                    if (html != null) {
                        return html;
                    }
                } else {
                    return getPartTextContent(body);
                }
            }
            return text;
        } else if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                String text = getPartTextContent(multipart.getBodyPart(i));
                if (text != null) {
                    return text;
                }
            }
        }

        return null;
    }

    @Then("^" + Phrases.SUBJECT + " should receive an email containing subject (.+) for (.+)$")
    public void userShouldReceiveEmailWithSubject(String subject, String address) {
        boolean receivedExpectedMessage = false;

        String resolvedAddress = placeholderResolver.resolve(address);
        String resolvedSubject = placeholderResolver.resolve(subject);

        for (WiserMessage message : smtpServer.getMessages()) {
            if (message.getEnvelopeReceiver().equals(resolvedAddress)) {
                try {
                    if (message.getMimeMessage().getSubject().contains(resolvedSubject)) {
                        receivedExpectedMessage = true;
                    }
                } catch (MessagingException e) {
                }
            }
        }

        assertTrue("Did not receive email for " + resolvedAddress + " with subject containing " + resolvedSubject,
                receivedExpectedMessage);
    }

    @Then("^" + Phrases.SUBJECT + " should receive an email for (.+)$")
    public void userShouldReceiveEmail(String address) {
        boolean receivedExpectedMessage = false;

        String resolvedAddress = placeholderResolver.resolve(address);

        for (WiserMessage message : smtpServer.getMessages()) {
            if (message.getEnvelopeReceiver().equals(resolvedAddress)) {
                receivedExpectedMessage = true;
            }
        }

        assertTrue("Did not receive email for " + address, receivedExpectedMessage);
    }

    @And("^the email containing subject (.+) for (.+) is stored as " + Phrases.PROPERTY_KEY + "$")
    public void storeReceivedEmailWithSubject(String subject, String address, String propertyKey) throws Exception {
        String resolvedAddress = placeholderResolver.resolve(address);
        for (WiserMessage message : smtpServer.getMessages()) {
            if (message.getEnvelopeReceiver().equals(resolvedAddress) &&
                    message.getMimeMessage().getSubject().contains(subject)) {
                propertyStore.put(propertyKey, getCompleteContent(message.getMimeMessage()));
                return;
            }
        }

        fail("No message was found for " + address + " with subject " + subject);
    }

    @And("^the email for (.+) is stored as " + Phrases.PROPERTY_KEY + "$")
    public void storeReceivedEmail(String address, String propertyKey) throws Exception {
        String resolvedAddress = placeholderResolver.resolve(address);
        for (WiserMessage message : smtpServer.getMessages()) {
            if (message.getEnvelopeReceiver().equals(resolvedAddress)) {
                propertyStore.put(propertyKey, getCompleteContent(message.getMimeMessage()));
                return;
            }
        }

        fail("No message was found for " + address);
    }

    @After("@emailing")
    public void stopServer() {
        smtpServer.stop();
    }
}
