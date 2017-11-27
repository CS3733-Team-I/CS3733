package database.objects;

import utility.KioskPermission;
import utility.Request.RequestType;
import org.springframework.security.crypto.bcrypt.*;

public class Employee {
    private String loginID;
    private String userName;
    private String password;
    private String salt;
    private KioskPermission permission;
    private RequestType serviceAbility;

    // Constructor for passwords
    public Employee(String loginID, String userName, String password,
                    KioskPermission permission, RequestType serviceAbility, boolean passwordAlreadyEncrypted){
        this.loginID = loginID;
        this.userName = userName;
        // this is for frontend vs backend use
        if(passwordAlreadyEncrypted){
            this.password = password;
        }
        else {
            this.password = encryptPassword(password);
        }
        if(permission==KioskPermission.NONEMPLOYEE){
            permission=KioskPermission.EMPLOYEE;
        }
        this.permission=permission;
        this.serviceAbility = serviceAbility;
    }

    public String getLoginID() {
        return loginID;
    }

    public String getUserName() {
        return userName;
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

    // Method to logIn passwords
    public boolean validatePassword(String password){
        return (BCrypt.checkpw(password,this.password));
    }

    // Method for encrypting passwords, currently does jack shit LOL
    private String encryptPassword(String password){
        String salt = BCrypt.gensalt();
        String encPassword = BCrypt.hashpw(password, salt);
        return encPassword;
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
    public boolean updateUserName(String newUserName, String password){
        boolean updated = validatePassword(password);
        if (updated){
            this.userName =newUserName;
        }
        return updated;
    }
}
