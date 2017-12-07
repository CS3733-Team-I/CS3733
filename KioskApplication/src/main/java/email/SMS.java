package email;

public class SMS extends Email implements MessageTemplate {
    private String phoneNumber;
    private Providers provider;

    public static class Builder {
        private Email.Builder emailBuilder;
        private String phoneNumber;
        private Providers provider;

        public Builder(String phoneNumber, Providers provider) {
            this.phoneNumber = phoneNumber;
            this.provider = provider;

        }
    }
}
