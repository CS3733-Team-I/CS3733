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

    // Constructor for passwords
    public Employee(String loginID, String loginName, String password,
                    KioskPermission permission, RequestType serviceAbility, boolean passwordAlreadyEncrypted){
        this.loginID = loginID;
        this.loginName = loginName;
        // this is for frontend vs backend use
        if(passwordAlreadyEncrypted){
            this.password=password;
        }
        else {
            this.password = encryptPassword(password);
        }
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

    // this method requires a password to get the encrypted password
    public String getPassword(String password) {
        if (validatePassword(password)){
            return this.password;
        }
        //I hate this method
        else return password;
    }

    // Method to validate passwords
    public boolean validatePassword(String password){
        return (this.password.equals(encryptPassword(password)));
    }

    // Method for encrypting passwords, currently does jack shit LOL
    private String encryptPassword(String password){
        return password;
    }

    // method to update passwords
    public boolean updatePassword(String newPassword, String oldPassword){
        boolean valid = validatePassword(oldPassword);
        if(valid){
            password=encryptPassword(newPassword);
        }
        return valid;
    }

    // for updating the user name
    public boolean updateUserName(String newLoginName, String password){
        boolean updated = validatePassword(password);
        if (updated){
            this.loginName=newLoginName;
        }
        return updated;
    }
}
