package entity;

import database.DatabaseController;
import database.objects.Employee;
import utility.KioskPermission;
import utility.request.RequestType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import static utility.KioskPermission.*;

/**
 * Should handle user credential validation and connection to the user database for LoginController
 * Should also pass information to indicate the MainWindowController's state as Nonemployee, Employee, & Admin
 * acts as a facade for the Employee class
 */
public class LoginEntity {
    private DatabaseController database;
    private KioskPermission currentPermission;
    private String username;

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
        database = DatabaseController.getInstance();

        logins = new HashMap<>();
        this.username ="";
        if (test){
            // so tests can add and remove logins as needed
            currentPermission = SUPER_USER;
        } else {
            // remove once we have a better way to initialize things
            readAllFromDatabase();

            if (database.getAllEmployees().size() == 0) {
                // if there are no employees in the database, start as a super user
                currentPermission = SUPER_USER;
            } else {
                // initial employee state, we don't want anyone to restart the application and gain access to admin powers
                currentPermission = NONEMPLOYEE;
            }
        }
    }

    public KioskPermission getCurrentPermission() {
        return currentPermission;
    }

    public String getUsername() {
        return username;
    }

    public String getUserID(){
        if(currentPermission==NONEMPLOYEE) {
            return "";
        }
        else{
            return logins.get(username).getLoginID();
        }
    }

    // Adds every employee from the database to the logins hashmap
    private void readAllFromDatabase(){
        LinkedList<Employee> employees = database.getAllEmployees();
        for (Employee emp : employees) {
            // the employee hashmap is linked to usernames because of their uniqueness and ease of accessing
            this.logins.putIfAbsent(emp.getUserName(),emp);
        }
    }

    /**
     * Gets all the login info from the entity, only allowed if currently a super user.
     */
    public ArrayList<Employee> getAllLogins() {
        // Return the logins if we're a super user
        if (getCurrentPermission().equals(KioskPermission.SUPER_USER)) {
            return new ArrayList<>(logins.values());
        }

        // Otherwise return an empty list
        return new ArrayList<>();
    }

    /**
     * This method should only allow Admins and Super Users to add new logins,
     * New logins can't be added if:
     * 1. the username already exists
     * 2. the currentPermission is greater than or equal to that of the current user (excluding Super Users)
     * 3. the currentPermission is NONEMPLOYEE
     *
     * Rules for new IDs, it should be the base username and if that ID is taken, that username+1 or 2 or 3 or...
     *
     * @param userName
     * @param password
     * @param permission
     * @param serviceAbility
     * @return
     */
    public boolean addUser(String userName, String password, KioskPermission permission, RequestType serviceAbility){
        // Idiot resistance
        if(permission==NONEMPLOYEE){
            return false;
        }
        // updates the hashmap in case a employee is missing
        // TODO: make reading from the database more efficient, when someone is adding a lot of users, we don't want this to take forever
        readAllFromDatabase();
        if(logins.containsKey(userName)){
            return false;
        }
        // limits creating new logins to subordinates in the KioskPermission hierarchy
        else if(this.currentPermission.ordinal()>permission.ordinal()||this.currentPermission ==SUPER_USER){
            // fitting it into the table
            if(userName.length()<=50){
                // Autogenerates a permanent employee ID from the name and current timestamp
                // Fun fact of the day, this code should work until 200ish years from now when the 14th digit gets added
                // to the Java timestamp, that is if this system or even Java survives that long
                String loginID = userName + System.currentTimeMillis();
                Employee newEmployee=new Employee(loginID, userName, password, permission, serviceAbility,false);
                logins.put(userName,newEmployee);
                database.addEmployee(newEmployee.getLoginID(),newEmployee.getUserName(),newEmployee.getPassword(password),
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
     * @param userName
     * @return
     */
    public boolean deleteLogin(String userName){
        // Idiot resistance to prevent people from removing themselves (for non-tests) and checks if the name is in the hashmap
        if(logins.containsKey(userName)&&(userName!=this.username || database.equals(DatabaseController.getInstance()))) {
            Employee delEmp = logins.get(userName);
            // checks to see if the current user permissions are higher than the one they are deleting
            if(delEmp.getPermission().ordinal()<this.currentPermission.ordinal()||this.currentPermission ==SUPER_USER) {
                logins.remove(userName);
                database.removeEmployee(delEmp.getLoginID());
                return true;
            }
        }
        return false;
    }

    // Method for users to update only their passwords, and no one else's
    public boolean updatePassword(String newPassword, String oldPassword){
        boolean updated = false;
        if (logins.get(username).updatePassword(newPassword, oldPassword)) {
            Employee emp = logins.get(username);
            database.updateEmployee(emp.getLoginID(),emp.getUserName(),emp.getPassword(newPassword),emp.getPermission(),emp.getServiceAbility());
            updated = true;
        }
        return updated;
    }

    // For changing your username
    public boolean updateUserName(String newUserName, String password){
        //pulling the user from the database seems cumbersome, but this avoids the problem associated with nonemployees
        Employee currUser = logins.get(this.username);
        //checks if the current user name is already in active use
        boolean updated = !logins.containsKey(newUserName);
        if(updated){
            //tries to update the current user's password
            currUser.updatePassword(newUserName, password);
        }
        if(updated){
            //updates internal hashmap
            logins.remove(username);
            logins.put(newUserName,currUser);
            //updates current username
            username =newUserName;
            //relays updated information to database
            database.updateEmployee(currUser.getLoginID(),currUser.getUserName(),password,
                    currUser.getPermission(),currUser.getServiceAbility());
        }
        return updated;
    }

    // For checking log in credentials
    // THIS IS THE ONLY WAY FOR A USER TO UPGRADE THEIR ACTIVE PERMISSIONS
    public KioskPermission logIn(String userName, String password){
        if(logins.containsKey(userName)){
            if (logins.get(userName).validatePassword(password)){
                this.username = userName;
                currentPermission = logins.get(userName).getPermission();
            }
            else {
                validationFail();
            }
        }
        else {
            validationFail();
        }
        return currentPermission;
    }

    /**
     * Helper method for logIn, while it is technically identical in function to logOut(),
     * it doesn't make sense for logOut() to be called when nobody is logged in
     */
    private void validationFail(){
        this.username = "";
        currentPermission = NONEMPLOYEE;
    }

    // Logout method
    public KioskPermission logOut(){
        this.username = "";
        currentPermission = NONEMPLOYEE;
        return currentPermission;
    }
}
