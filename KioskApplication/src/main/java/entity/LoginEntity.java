package entity;

import database.DatabaseController;
import database.objects.IEmployee;
import database.objects.Employee;
import database.objects.NullEmployee;
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
    // holds all relevant information for the current user
    private IEmployee currentLogin;
    private HashMap<String,Employee> logins;

    public static LoginEntity getInstance(){
        return LoginEntitySingletonHelper.instance;
    }

    public static LoginEntity getTestInstance(){
        return LoginEntitySingletonHelper.testInstance;
    }

    private static class LoginEntitySingletonHelper {
        private static final LoginEntity instance = new LoginEntity(false);
        private static final LoginEntity testInstance = new LoginEntity(true);
    }

    private LoginEntity(Boolean test){
        database = DatabaseController.getInstance();

        logins = new HashMap<>();
        if (test){
            // so tests can add and remove logins as needed
            currentLogin = new Employee("firstTimeSetup","","","root",
                    SUPER_USER,RequestType.GENERAL);
        } else {
            readAllFromDatabase();

            if (database.getAllEmployees().size() == 0) {
                // if there are no employees in the database, start as a super user
                currentLogin = new Employee("firstTimeSetup","","","root",
                        SUPER_USER,RequestType.GENERAL);
            } else {
                // initial employee state, we don't want anyone to restart the application and gain access to admin powers
                currentLogin = NullEmployee.getInstance();
            }
        }
    }

    public KioskPermission getCurrentPermission() {
        return currentLogin.getPermission();
    }

    public String getUsername() {
        return currentLogin.getUsername();
    }

    /**
     *
     * @return integer LoginID
     */
    public int getLoginID(){
        return currentLogin.getLoginID();
    }

    // Adds every employee from the database to the logins hashmap
    private void readAllFromDatabase(){
        LinkedList<Employee> employees = database.getAllEmployees();
        for (Employee emp : employees) {
            // the employee hashmap is linked to usernames because of their uniqueness and ease of accessing
            this.logins.putIfAbsent(emp.getUsername(),emp);
        }
    }

    /**
     * Gets all the login info from the entity, only allowed if currently a super user.
     */
    public ArrayList<Employee> getAllLogins() {
        ArrayList<Employee> emps = new ArrayList<Employee>();
        // Return the logins if we're a super user
        if (getCurrentPermission()==SUPER_USER) {
            return new ArrayList<Employee>(logins.values());
        }

        // Otherwise return an empty list
        return emps;
    }

    public ArrayList<String> getAllUserNames(){
        ArrayList<Employee> employees = new ArrayList<>(logins.values());
        ArrayList<String> userNames = new ArrayList<String>();
        for(Employee employee: employees){
            userNames.add(employee.getUsername());
        }
        return userNames;
    }

    public ArrayList<Integer> getAllLoginIDs(){
        ArrayList<Employee> employees = new ArrayList<>(logins.values());
        ArrayList<Integer> loginIDs= new ArrayList<Integer>();
        for(Employee employee: employees){
            loginIDs.add(employee.getLoginID());
        }
        return loginIDs;
    }

    public ArrayList<Integer> getAllEmployeeType(RequestType type){
        ArrayList<Employee> employees = new ArrayList<>(logins.values());
        ArrayList<Integer> loginIDs = new ArrayList<Integer>();
        if(type.equals(RequestType.GENERAL)){
            loginIDs = getAllLoginIDs();
        }else{
            for(Employee employee: employees){
                if(employee.getServiceAbility().equals(type)){
                    loginIDs.add(employee.getLoginID());
                }
            }
        }
        return loginIDs;
    }

    /**
     * This method should only allow Admins and Super Users to add new logins,
     * New logins can't be added if:
     * 1. the username already exists
     * 2. the currentPermission is greater than or equal to that of the current user (excluding Super Users)
     * 3. the currentPermission is NONEMPLOYEE
     *
     * @param userName
     * @param lastName
     * @param firstName
     * @param password
     * @param permission
     * @param serviceAbility
     * @return
     */
    public boolean addUser(String userName, String lastName, String firstName, String password,
                           KioskPermission permission, RequestType serviceAbility){
        // Idiot resistance
        if(currentLogin.getPermission()==NONEMPLOYEE||permission==NONEMPLOYEE){
            return false;
        }
        // updates the hashmap in case a employee is missing
        // TODO: make reading from the database more efficient, when someone is adding a lot of users, we don't want this to take forever
        readAllFromDatabase();
        if(logins.containsKey(userName)){
            return false;
        }
        // limits creating new logins to subordinates in the KioskPermission hierarchy
        else if(currentLogin.getPermission().ordinal()>permission.ordinal()||
                currentLogin.getPermission() == SUPER_USER){
            // fitting it into the table
            if(userName.length()<=50){
                //constructs a temporary employee for database insertion
                Employee tempEmployee=new Employee(userName,lastName,firstName, password, permission,
                        serviceAbility);
                int ID = database.addEmployee(tempEmployee,password);
                logins.put(userName,database.getEmployee(ID));
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
     * @param username
     * @return
     */
    public boolean deleteLogin(String username){
        // Idiot resistance to prevent people from removing themselves (for non-tests) and checks if the name is in the hashmap
        if(logins.containsKey(username)&&(username!=currentLogin.getUsername())) {
            IEmployee delEmp = logins.get(username);
            // checks to see if the current user permissions are higher than the one they are deleting
            if(delEmp.getPermission().ordinal()<currentLogin.getPermission().ordinal()||
                    currentLogin.getPermission()==SUPER_USER) {
                logins.remove(username);
                database.removeEmployee(delEmp.getLoginID());
                return true;
            }
        }
        return false;
    }

    /**
     * only for the current login
     * @return the RequestType of the current log in
     */
    public RequestType getServiceAbility() {
        return currentLogin.getServiceAbility();
    }

    // Method for users to update only their passwords, and no one else's
    public boolean updatePassword(String newPassword, String oldPassword){
        boolean updated = false;
        if (currentLogin.updatePassword(newPassword, oldPassword)) {
            updateCurrentLogin(newPassword,currentLogin.getUsername());
            updated = true;
        }
        return updated;
    }

    /**
     * for updating the current user's username
     * @param newUsername
     * @param password
     * @return
     */
    public boolean updateUsername(String newUsername, String password){
        boolean updated=false;
        String oldUsername = currentLogin.getUsername();
        //pulling the user from the database seems cumbersome, but this avoids the problem associated with nonemployees
        //checks if the current user name is already in active use
        if(!logins.containsKey(newUsername)){
            updated=currentLogin.updateUsername(newUsername,password);
        }
        if(updated){
            //updates internal hashmap
            updateCurrentLogin(password,oldUsername);
        }
        return updated;
    }

    /**
     * Helper method for the methods that update the current login
     * @param oldUsername can also be the current userName
     * @param password
     */
    private void updateCurrentLogin(String password,String oldUsername) {
        logins.remove(oldUsername);
        Employee tempEmp = new Employee(currentLogin.getLoginID(), currentLogin.getLastName(),
                currentLogin.getFirstName(), currentLogin.getUsername(), currentLogin.getPassword(password),
                currentLogin.getPermission(), currentLogin.getServiceAbility());
        logins.put(currentLogin.getUsername(), tempEmp);
                //haven't figured out how to safely get things from an interface to the class
                //so yeah...
        database.updateEmployee(tempEmp,password);
    }

    // For checking log in credentials
    // THIS IS THE ONLY WAY FOR A USER TO UPGRADE THEIR ACTIVE PERMISSIONS

    /**
     *
     * @param username
     * @param password
     * @return returns true if valid login, and sets the current login as that employee
     */
    public boolean logIn(String username, String password){
        if(logins.containsKey(username)) {
            if (logins.get(username).validatePassword(password)) {
                currentLogin = logins.get(username);
                return true;
            }
        }
        //failure state
        currentLogin=NullEmployee.getInstance();
        return false;
    }

    // Logout method
    public void logOut(){
        currentLogin=NullEmployee.getInstance();
    }
}
