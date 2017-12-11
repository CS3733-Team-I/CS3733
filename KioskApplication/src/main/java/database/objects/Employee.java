package database.objects;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import utility.KioskPermission;
import utility.request.RequestType;
import org.springframework.security.crypto.bcrypt.*;

import java.util.ArrayList;

public class Employee extends RecursiveTreeObject<Employee> implements IEmployee {
    private int id;
    private String username;
    private String lastName;
    private String firstName;
    private String password;
    private ArrayList<String> options;
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
    public Employee(String username, String lastName, String firstName, String password, ArrayList<String> options, KioskPermission permission, RequestType serviceAbility){
        this.username = username;
        this.lastName=lastName;
        this.firstName=firstName;
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
        if(permission==KioskPermission.NONEMPLOYEE){
            this.permission=KioskPermission.EMPLOYEE;
        }
        this.options=options;
        this.permission = permission;
        this.serviceAbility = serviceAbility;
    }

    /**
     * This is for database side use,
     * @param id retrieves it from the database
     * @param username
     * @param lastName
     * @param firstName
     * @param password
     * @param optionsString
     * @param permission
     * @param serviceAbility
     */
    public Employee(int id, String username, String lastName, String firstName, String password, String optionsString,
                    KioskPermission permission, RequestType serviceAbility){
        this.id = id;
        this.username = username;
        this.lastName=lastName;
        this.firstName=firstName;
        this.password = password;
        this.options = new ArrayList<String>();
            String subOption="";
            for(char c : optionsString.toCharArray()){
                if(c==':'){
                    options.add(subOption);
                    subOption="";
                }
                else{
                    subOption=subOption+c;
                }
            }
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
    public boolean setPassword(String newPassword, String oldPassword){
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
    public boolean setUsername(String newUsername, String password){
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

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<String> getOptions() {
        return options;
        /*ArrayList<String> optionsList = new ArrayList<String>();
            String inLanguage="";
            for(char c : options.toCharArray()){
                if(c==':'){
                    optionsList.add(inLanguage);
                    inLanguage="";
                }
                else{
                    inLanguage=inLanguage+c;
                }
            }
        return optionsList;*/
    }

    public String getOptionsForDatabase(){
        String optionsString = "";
        for (String subOption :
                options) {
            optionsString=optionsString+subOption+":";
        }
        return optionsString;
    }
}
