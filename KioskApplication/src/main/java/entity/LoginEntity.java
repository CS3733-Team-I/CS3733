package entity;

import database.DatabaseController;
import database.objects.Employee;
import utility.KioskPermission;
import utility.Request.RequestType;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;

import static utility.KioskPermission.*;

/**
 * Should handle user credential validation and connection to the user database for LoginController
 * Should also pass information to indicate the MainWindowController's state as Nonemployee, Employee, & Admin
 * acts as a facade for the Employee class
 */
public class LoginEntity {
    private DatabaseController dbC;
    private KioskPermission permission;
    private String loginName;
    // TODO: make these less vulnerable
    private HashMap<String,Employee> logins;

    private static LoginEntity instance = null;

    public static LoginEntity getInstance(){
        return SingletonHelper.instance;
    }

    public static LoginEntity getTestInstance(){
        return SingletonHelper.testInstance;
    }

    private static class SingletonHelper {
        private static final LoginEntity instance = new LoginEntity(false);
        private static final LoginEntity testInstance = new LoginEntity(true);
    }

    private LoginEntity(Boolean test){
        logins = new HashMap<>();
        this.loginName="";
        if (test){
            // so tests can add and remove logins as needed
            permission = SUPER_USER;
            dbC = DatabaseController.getTestInstance();
        }
        else {
            // remove once we have a better way to initialize things
            permission = SUPER_USER;
            dbC = DatabaseController.getInstance();
            // TODO: remove this backdoor once a more secure method of initially tracking admin logins is developed
            addLogin("boss@hospital.com", "123",ADMIN,RequestType.GENERAL);
            addLogin("emp@hospital.com", "12",EMPLOYEE,RequestType.INTERPRETER);
            // initial employee state, we don't want anyone to restart the application and gain access to admin powers
            permission = NONEMPLOYEE;
        }
    }

    public KioskPermission getPermission() {
        return permission;
    }

    public String getLoginName() {
        return loginName;
    }

    // Adds every employee from the database to the logins hashmap
    private void readAllFromDatabase(){
        LinkedList<Employee> employees = dbC.getAllEmployees();
        for (Employee emp : employees) {
            // the employee hashmap is linked to usernames because of their uniqueness and ease of accessing
            this.logins.putIfAbsent(emp.getLoginName(),emp);
        }
    }

    /**
     * This method should only allow Admins and Super Users to add new logins,
     * New logins can't be added if:
     * 1. the loginName already exists
     * 2. the permission is greater than or equal to that of the current user (excluding Super Users)
     * 3. the permission is NONEMPLOYEE
     *
     * Rules for new IDs, it should be the base username and if that ID is taken, that username+1 or 2 or 3 or...
     *
     * @param loginName
     * @param password
     * @param permission
     * @param serviceAbility
     * @return
     */

    public boolean addLogin(String loginName, String password, KioskPermission permission, RequestType serviceAbility){
        // Idiot resistance
        if(permission==NONEMPLOYEE){
            return false;
        }
        // updates the hashmap in case a employee is missing
        // TODO: make reading from the database more efficient, when someone is adding a lot of users, we don't want this to take forever
        readAllFromDatabase();
        if(logins.containsKey(loginName)){
            return false;
        }
        // limits creating new logins to subordinates in the KioskPermission hierarchy
        else if(this.permission.ordinal()>permission.ordinal()||this.permission==SUPER_USER){
            // fitting it into the table
            if(loginName.length()<=50){
                // Autogenerates a permanent employee ID from the name and current timestamp
                // Fun fact of the day, this code should work until 200ish years from now when the 14th digit gets added
                // to the Java timestamp, that is if this system or even Java survives that long
                String loginID = loginName + System.currentTimeMillis();
                Employee newEmployee=new Employee(loginID, loginName, password, permission, serviceAbility,false);
                logins.put(loginName,newEmployee);
                dbC.addEmployee(newEmployee.getLoginID(),newEmployee.getLoginName(),newEmployee.getPassword(password),
                        newEmployee.getPermission(),newEmployee.getServiceAbility());
                // TODO: Idea, create custom exception to inform the user on errors related to creating their employee
                return true;
            }
        }
        return false;
    }

    /**
     * a employee cannot be deleted from the database unless:
     * 1. It exists in the hashmap
     * 2. The deleter has higher permissions than the deletee or is a Super User
     * 3. The deleter is not deleting their own employee (unless this is a test case)
     * @param loginName
     * @return
     */
    public boolean deleteLogin(String loginName){
        // Idiot resistance to prevent people from removing themselves (for non-tests) and checks if the name is in the hashmap
        if(logins.containsKey(loginName)&&(loginName!=this.loginName||dbC.equals(DatabaseController.getTestInstance()))) {
            Employee delEmp = logins.get(loginName);
            // checks to see if the current user permissions are higher than the one they are deleting
            if(delEmp.getPermission().ordinal()<this.permission.ordinal()||this.permission==SUPER_USER) {
                logins.remove(loginName);
                dbC.removeEmployee(delEmp.getLoginID());
                return true;
            }
        }
        return false;
    }

    // Method for users to update only their passwords, and no one else's
    public boolean updatePassword(String newPassword, String oldPassword){
        boolean updated = false;
        if (logins.get(loginName).updatePassword(newPassword, oldPassword)) {
            Employee emp = logins.get(loginName);
            dbC.updateEmployee(emp.getLoginID(),emp.getLoginName(),emp.getPassword(newPassword),emp.getPermission(),emp.getServiceAbility());
            updated = true;
        }
        return updated;
    }

    // For changing your username
    public boolean updateUserName(String newLoginName, String password){
        //pulling the user from the database seems cumbersome, but this avoids the problem associated with nonemployees
        Employee currUser = logins.get(this.loginName);
        //checks if the current user name is already in active use
        boolean updated = !logins.containsKey(newLoginName);
        if(updated){
            //tries to update the current user's password
            currUser.updatePassword(newLoginName, password);
        }
        if(updated){
            //updates internal hashmap
            logins.remove(loginName);
            logins.put(newLoginName,currUser);
            //updates current userName
            loginName=newLoginName;
            //relays updated information to database
            dbC.updateEmployee(currUser.getLoginID(),currUser.getLoginName(),password,
                    currUser.getPermission(),currUser.getServiceAbility());
        }
        return updated;
    }

    // For checking log in credentials
    // THIS IS THE ONLY WAY FOR A USER TO UPGRADE THEIR ACTIVE PERMISSIONS
    public KioskPermission logIn(String loginName, String password){
        if(logins.containsKey(loginName)){
            if (logins.get(loginName).validatePassword(password)){
                this.loginName = loginName;
                permission = logins.get(loginName).getPermission();
            }
            else {
                validationFail();
            }
        }
        else {
            validationFail();
        }
        return permission;
    }

    /**
     * Helper method for logIn, while it is technically identical in function to logOut(),
     * it doesn't make sense for logOut() to be called when nobody is logged in
     */
    private void validationFail(){
        this.loginName = "";
        permission = NONEMPLOYEE;
    }

    // Logout method
    public KioskPermission logOut(){
        this.loginName = "";
        permission = NONEMPLOYEE;
        return permission;
    }
}
