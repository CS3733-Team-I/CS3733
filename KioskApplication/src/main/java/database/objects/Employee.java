package database.objects;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import utility.KioskPermission;
import utility.request.RequestType;
import org.springframework.security.crypto.bcrypt.*;

public class Employee extends RecursiveTreeObject<Employee> implements IEmployee {
    private int loginID;
    private String username;
    private String password;
    private KioskPermission permission;
    private RequestType serviceAbility;

    /**
     * Constructor for new employees. This is only for the LoginEntity
     * @param username
     * @param password is encypted through Bcrypt
     * @param permission if nonEmployee, it changes to employee
     * @param serviceAbility
     */
    public Employee(String username, String password, KioskPermission permission, RequestType serviceAbility){
        this.username = username;
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
        if(permission==KioskPermission.NONEMPLOYEE){
            this.permission=KioskPermission.EMPLOYEE;
        }
        this.permission = permission;
        this.serviceAbility = serviceAbility;
    }

    /**
     * This is for database side use,
     * @param loginID retrieves it from the database
     * @param username
     * @param password
     * @param permission
     * @param serviceAbility
     */
    public Employee(int loginID, String username, String password,
                    KioskPermission permission, RequestType serviceAbility){
        this.loginID = loginID;
        this.username = username;
        this.password = password;
        this.permission=permission;
        this.serviceAbility = serviceAbility;
    }

    public int getLoginID() {
        return loginID;
    }

    public String getUsername() {
        return username;
    }

    public KioskPermission getPermission() {
        return permission;
    }

    public RequestType getServiceAbility() {
        return serviceAbility;
    }

    /**
     * To get the password, the correct password must be presented
     * @param password
     * @return encrypted password if password is correct or "" otherwise
     */
    public String getPassword(String password) {
        if (validatePassword(password)){
            return this.password;
        }
        else return "";
    }

    /**
     *
     * @param password
     * @return boolean on the validity of the password
     */
    public boolean validatePassword(String password){
        return (BCrypt.checkpw(password, this.password));
    }

    /**
     *
     * @param newPassword
     * @param oldPassword
     * @return true = Old Password is correct and Password has been updated
     */
    public boolean updatePassword(String newPassword, String oldPassword){
        boolean valid = validatePassword(oldPassword);
        if(valid){
            password=BCrypt.hashpw(newPassword, BCrypt.gensalt());
        }
        return valid;
    }

    /**
     *
     * @param newUsername
     * @param password
     * @return true = password is correct and username is changed
     */
    public boolean updateUsername(String newUsername, String password){
        boolean updated = validatePassword(password);
        if (updated){
            this.username =newUsername;
        }
        return updated;
    }
}
