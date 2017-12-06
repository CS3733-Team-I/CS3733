package email;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.Message;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class EmailSender {
    /** Application name. */
    private static final String APPLICATION_NAME = "Kiosk Application";

    /** Directory to store user credentials for this application. */
    private static final File DATA_STORE_DIR = new File("credentials");

    /** Global instance of the {@link com.google.api.client.util.store.FileDataStoreFactory). */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory */
    private static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this application.
     *
     * If modifying these scopes, delete your previously saved credentials
     */
    private static final List<String> SCOPES =
            Arrays.asList(GmailScopes.GMAIL_LABELS);

    private static Gmail DEFAULT_SERVICE;

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object
     * @return an authorized Credential object
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
                EmailSender.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();
        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Gmail client service.
     * @return an authorized Gmail client service
     * @throws IOException
     */
    private static Gmail getGmailService() throws IOException {
        Credential credential = authorize();
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Create a MimeMessage using the parameters provided.
     *
     * @param email the email to be parsed
     * @return MimeMessage to be used to send email
     * @throws IOException
     * @throws MessagingException
     */
    private static MimeMessage createEmailWithAttachment(Email email)
            throws IOException, MessagingException{
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage mimeMessage = new MimeMessage(session);

        mimeMessage.setFrom(new InternetAddress(email.getFrom()));
        mimeMessage.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(email.getTo()));
        mimeMessage.setSubject(email.getSubject());

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(email.getBody(), "text/plain");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        mimeBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(email.getAttachment());

        mimeBodyPart.setDataHandler(new DataHandler(source));
        mimeBodyPart.setFileName(email.getAttachment().getName());

        multipart.addBodyPart(mimeBodyPart);
        mimeMessage.setContent(multipart);

        return mimeMessage;
    }

    public static void init() {
        try {
            DEFAULT_SERVICE = getGmailService();

            String user = "me";

            ListLabelsResponse listLabelsResponse =
                    DEFAULT_SERVICE.users().labels().list(user).execute();
            List<Label> labels = listLabelsResponse.getLabels();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a MimeMessage using the parameters provided.
     *
     * @param  email to be parsed
     * @return the MimeMessage to be used to send email
     * @throws javax.mail.MessagingException
     */
    private static MimeMessage createEmail(Email email) throws MessagingException{
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage mimeMessage = new MimeMessage(session);

        mimeMessage.setFrom(new InternetAddress(email.getFrom()));
        mimeMessage.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(email.getTo()));
        mimeMessage.setSubject(email.getSubject());
        mimeMessage.setText(email.getBody());
        return mimeMessage;
    }

    /**
     * Create a message from an email
     *
     * @param emailContent Email to bet to raw of message
     * @return a message containing a base64url encoded email
     * @throws MessagingException
     * @throws IOException
     */
    private static Message createMessageWithEmail(MimeMessage emailContent)
        throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    /**
     * Send an email from the user's mailbox to its recipient
     *
     * @param service Authorized Gmail API instance
     * @param userId User's email address.  The special value "me"
     *               can be used to indicate the authenticated user.
     * @param email Email to be sent
     * @return the sent message
     */
    public static Message sendEmail(Gmail service,
                                    String userId,
                                    Email email) {
        try {
            Message message;
            if(email.getAttachment() != null) {
                message = createMessageWithEmail(createEmailWithAttachment(email));
            } else {
                message = createMessageWithEmail(createEmail(email));
            }
                message = service.users().messages().send(userId, message).execute();

                System.out.println("Message id: " + message.getId());
                System.out.println(message.toPrettyString());
            return message;
        } catch (MessagingException mex) {
            mex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Message sendEmail(Gmail service,
                                    Email email) {
        return sendEmail(service, "me", email);
    }

    public static Message sendEmail(String userId,
                                    Email email) {
        return sendEmail(DEFAULT_SERVICE, userId, email);
    }

    public static Message sendEmail(Email email) {
        return sendEmail(DEFAULT_SERVICE, "me", email);
    }

}
