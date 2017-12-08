package email;

import java.io.File;

public class Email implements MessageTemplate {
    private String to, from, body, subject;
    private File attachment;

    public Email(Email.Builder builder) {
        this.to = builder.to;
        this.from = builder.from;
        this.body = builder.body;
        this.subject = builder.subject;
        this.attachment = builder.attachment;
    }

    protected Email() {};

    protected void setTo(String to) {
        this.to = to;
    }

    protected void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
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

    protected void setBody(String body) {
        this.body = body;
    }

    public String getSubject() {
        return subject;
    }

    protected void setSubject(String subject) {
        this.subject = subject;
    }

    public static class Builder {
        private String to, from = "me", body, subject;
        private File attachment;

        public Builder(String to) {
            this.to = to;
        }

        public Builder setFrom(String from) {
            this.from = from;
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

        public Builder setTo(String to) {
            this.to = to;
            return this;
        }

        public Email build() {
            return new Email(this);
        }
    }
}
