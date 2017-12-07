package email;

import java.io.File;

public class Email implements MessageTemplate {
    private String to, from, host, body, subject;
    private File attachment;

    public Email(Email.Builder builder) {
        this.to = builder.to;
        this.from = builder.from;
        this.host = builder.host;
        this.body = builder.body;
        this.subject = builder.subject;
        this.attachment = builder.attachment;
    }

    public Email() {};

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

    public static class Builder {
        private String to, from, host, body, subject;
        private File attachment;

        public Builder(String to) {
            this.to = to;
        }

        public Builder setFrom(String from) {
            this.from = from;
            return this;
        }

        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        public Builder setBody(String body) {
            this.body = body;
            return this;
        }

        public Builder setSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder setAttachment(File file) {
            this.attachment = file;
            return this;
        }

        public Email build() {
            return new Email(this);
        }
    }
}
