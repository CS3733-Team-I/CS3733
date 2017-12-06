package email;

import java.io.File;

public class Email {
    private String to, from, host, body, subject;
    private File attachment;

    public Email(String to, String from, String host) {
        this.to = to;
        this.from = from;
        this.host = host;
    }

    public Email(String to, String from) {
        this(to, from, "localhost");
    }

    public Email(String to) {
        this(to, "jfparrick@wpi.edu", "localhost");
    }

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

    public String getHost() {
        return host;
    }

    public String getBody() {
        return body;
    }

    public File getAttachment() {
        return attachment;
    }

    public void setAttachment(File attachment) {
        this.attachment = attachment;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
