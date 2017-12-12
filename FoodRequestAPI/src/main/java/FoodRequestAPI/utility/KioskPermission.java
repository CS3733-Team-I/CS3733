package FoodRequestAPI.utility;

public enum KioskPermission {
    //TODO point of discussion, what should we call each level of the hierarchy?
    NONEMPLOYEE,//only has access to the map and directions
    EMPLOYEE,//everything above and can submit and view service requests
    ADMIN,//everything above and can edit the map, add new employees, and remove employees
    SUPER_USER//everything above and can add and remove admins
}
