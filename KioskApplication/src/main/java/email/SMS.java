package email;

import java.io.File;

public class SMS extends Email implements MessageTemplate {
    private String phoneNumber;
    private Providers provider;

    public SMS(Builder builder) throws InvalidPhoneNumberException, InvalidMMSException{
        builder.to = createEmailAddress(builder.phoneNumber, builder.provider, (builder.body + builder.subject).length(), builder.attachment != null);

        super.setTo(builder.to);
        super.setFrom(builder.from);
        super.setBody(builder.body);
        super.setSubject(builder.subject);
        super.setAttachment(builder.attachment);
    }

    private String createEmailAddress(String phoneNumber, Providers provider, int total, boolean attachment) throws InvalidMMSException, InvalidPhoneNumberException {
        phoneNumber = validatePhoneNumber(phoneNumber);

        if(total > 100 || attachment) {
            return  phoneNumber + ProvidersUtil.providerToMMS(provider);
        }
        else {
            return phoneNumber + ProvidersUtil.providerToSMS(provider);
        }
    }

    private String validatePhoneNumber(String phoneNumber) throws InvalidPhoneNumberException {
        if(phoneNumber.length() < 9 || phoneNumber.length() > 10) {
            throw new InvalidPhoneNumberException();
        }
        if(phoneNumber.length() == 9) {
            phoneNumber = "1" + phoneNumber;
        }
        else {
            if(!phoneNumber.startsWith("1")) {
                throw new InvalidPhoneNumberException();
            }
        }
        return phoneNumber;
    }

    public static class Builder {
        private String to, from, body, subject;
        private File attachment;
        private String phoneNumber;
        private Providers provider;

        public Builder(String phoneNumber, Providers provider) {
            this.phoneNumber = phoneNumber;
            this.provider = provider;
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

        public Builder setAttachment(File attachment) {
            this.attachment = attachment;
            return this;
        }

        public SMS build() throws InvalidPhoneNumberException, InvalidMMSException {
            return new SMS(this);
        }
    }
}
