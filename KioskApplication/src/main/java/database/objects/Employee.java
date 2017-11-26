package database.objects;

import utility.KioskPermission;
import utility.Request.RequestType;

public class Employee {
    private String loginID;
    private String loginName;
    // TODO make this somehow encrypted
    private String password;
    private KioskPermission permission;
    private RequestType serviceAbility;

    // Constructor security behind this will be added later
    public Employee(String loginID, String loginName, String password, KioskPermission permission, RequestType serviceAbility){
        this.loginID = loginID;
        this.loginName = loginName;
        this.password = password;
        switch (permission){
            // Upping idiot resistance: in case someone tries putting NONEMPOLYEE in
            case NONEMPLOYEE:
                this.permission=KioskPermission.EMPLOYEE;
                break;
            default:
                this.permission = permission;
                break;
        }
        this.serviceAbility = serviceAbility;
    }

    public String getLoginID() {
        return loginID;
    }

    public String getLoginName() {
        return loginName;
    }

    public KioskPermission getPermission() {
        return permission;
    }

    public RequestType getServiceAbility() {
        return serviceAbility;
    }

    // Method to validate passwords
    // TODO make this more secure
    public boolean validatePassword(String password){
        return (this.password.equals(password));
    }

    // method to update passwords
    public boolean updatePassword(String newPassword, String oldPassword){
        boolean valid = validatePassword(oldPassword);
        if(valid){
            password=newPassword;
        }
        return valid;
    }
}
