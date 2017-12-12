package utility.request;

// NOTE don't change the order of these it'll mess up the database
public enum RequestType {
    GENERAL,
    DOCTOR,
    INTERPRETER,
    FOOD,
    SECURITY,
    JANITOR,
    IT,
    MAINTENANCE,
    INTERNAL_TRANSPORTATION,
    EXTERNAL_TRANSPORTATION;

    //TODO: language support
    @Override
    public String toString() {
        switch(this) {
            case GENERAL: return "General";
            case DOCTOR: return "Doctor";
            case INTERPRETER: return "Interpreter";
            case FOOD: return "Food";
            case SECURITY: return "Security";
            case JANITOR: return "Janitorial";
            case IT: return "IT";
            case MAINTENANCE: return "Maintenance";
            case INTERNAL_TRANSPORTATION: return "Internal Transportation";
            case EXTERNAL_TRANSPORTATION: return "External Transportation";
            default: return "Not Set: Contact Programmer";
        }
    }
}
