package utility;

public enum KioskPermission {
    //TODO point of discussion, what should we call each level of the hierarchy?
    NONEMPLOYEE,//only has access to the map and directions
    EMPLOYEE,//everything above and can submit and view service requests
    ADMIN,//everything above and can edit the map, add new employees, and remove employees
    SUPER_USER;//everything above and can add and remove admins

    //TODO: Language support
    @Override
    public String toString() {
        switch(this) {
            case NONEMPLOYEE:
                return "User";
            case EMPLOYEE:
                return "Employee";
            case ADMIN:
                return "Administrator";
            case SUPER_USER:
                return "Super User";
            default:
                return "Not Set: Contact Programmer";
        }
    }
}
