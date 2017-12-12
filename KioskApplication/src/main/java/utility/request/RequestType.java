package utility.request;

// NOTE don't change the order of these it'll mess up the database
public enum RequestType {
    GENERAL,
    INTERPRETER,
    FOOD,
    SECURITY,
    JANITOR,
    NONE;

    //TODO: language support
    @Override
    public String toString() {
        switch(this) {
            case GENERAL: return "General";
            case INTERPRETER: return "Interpreter";
            case FOOD: return "Food";
            case SECURITY: return "Security";
            case JANITOR: return "Janitorial";
            case NONE: return "None";
            default: return "Not Set: Contact Programmer";
        }
    }
}
