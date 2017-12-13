package FoodRequestAPI.database.objects;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import org.springframework.security.crypto.bcrypt.BCrypt;
import FoodRequestAPI.utility.KioskPermission;
import FoodRequestAPI.utility.request.RequestType;

public class Employee extends RecursiveTreeObject<Employee> implements IEmployee {
    private int id;
    private String username;
    private String lastName;
    private String firstName;
    private String password;
    private String options;
    private KioskPermission permission;
    private RequestType serviceAbility;

    /**
     * Constructor for new employees. This is only for the LoginEntity
     * @param username
     * @param lastName
     * @param firstName
     * @param password is encypted through Bcrypt
     * @param options
     * @param permission if nonEmployee, it changes to employee
     * @param serviceAbility
     */
    public Employee(String username, String lastName, String firstName, String password, String options, KioskPermission permission, RequestType serviceAbility){
        this.username = username;
        this.lastName=lastName;
        this.firstName=firstName;
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
        if(permission== KioskPermission.NONEMPLOYEE){
            this.permission= KioskPermission.EMPLOYEE;
        }
        this.options=options;
        this.permission = permission;
        this.serviceAbility = serviceAbility;
    }

    /**
     * This is for FoodRequestAPI.database side use,
     * @param id retrieves it from the FoodRequestAPI.database
     * @param username
     * @param lastName
     * @param firstName
     * @param password
     * @param options
     * @param permission
     * @param serviceAbility
     */
    public Employee(int id, String username, String lastName, String firstName, String password, String options,
                    KioskPermission permission, RequestType serviceAbility){
        this.id = id;
        this.username = username;
        this.lastName=lastName;
        this.firstName=firstName;
        this.password = password;
        this.options = options;
        this.permission=permission;
        this.serviceAbility = serviceAbility;
    }

    public int getID() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
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
            password= BCrypt.hashpw(newPassword, BCrypt.gensalt());
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

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getOptions() {
        return options;
    }
}
