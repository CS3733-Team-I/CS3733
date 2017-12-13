package utility.node;

public enum NodeType {
    HALL,
    ELEV,
    REST,
    STAI,
    DEPT,
    LABS,
    INFO,
    CONF,
    EXIT,
    RETL,
    SERV,
    TEMP;

    //TODO: language support
    @Override
    public String toString() {
        switch(this) {
            case HALL: return "Hall";
            case ELEV: return "Elevator";
            case REST: return "Restroom";
            case STAI: return "Stairs";
            case DEPT: return "Department";
            case LABS: return "Lab";
            case INFO: return "Information";
            case CONF: return "Conference Room";
            case EXIT: return "Exit";
            case RETL: return "Retail";
            case SERV: return "Service";
            case TEMP: return "Temporary";
            default: return "Not Set: Contact Programmer";
        }
    }

    public String toLiteralString() {
        return super.toString();
    }
}
