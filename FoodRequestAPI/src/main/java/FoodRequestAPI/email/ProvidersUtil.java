package FoodRequestAPI.email;

public class ProvidersUtil {

    public static String providerToSMS(Providers provider) {
        switch (provider) {
            case ALLTEL:
                return "@message.alltel.com";
            case ATT:
                return "@txt.att.net";
            case BOOSTMOBILE:
                return "@myboostmobile.com";
            case CRICKETWIRELESS:
                return "@mms.cricketwireless.net";
            case PROJECTFI:
                return "@msg.fi.google.com";
            case SPRINT:
                return "@messaging.sprintpcs.com";
            case TMOBILE:
                return "@tmomail.net";
            case USCELLULAR:
                return "@FoodRequestAPI.email.uscc.net";
            case VERIZON:
                return "@vtext.com";
            case VIRGINMOBILE:
                return "@vmobl.com";
            case REPUBLICWIRELESS:
                return "@text.republicwireless.com";
            default:
                return null;
        }
    }

    public static String providerToMMS(Providers provider) throws InvalidMMSException {
        switch (provider) {
            case ALLTEL:
                return "@mms.alltelwireless.com";
            case ATT:
                return "@mms.att.net";
            case BOOSTMOBILE:
                return "@myboostmobile.com";
            case CRICKETWIRELESS:
                return "@mms.cricketwireless.net";
            case PROJECTFI:
                return "@msg.fi.google.com";
            case SPRINT:
                return "@pm.sprint.com";
            case TMOBILE:
                return "@tmomail.net";
            case USCELLULAR:
                return "@mms.uscc.net";
            case VERIZON:
                return "@vzwpix.com";
            case VIRGINMOBILE:
                return "@vmpix.com";
            case REPUBLICWIRELESS:
                throw new InvalidMMSException();
            default:
                return null;
        }
    }
}
