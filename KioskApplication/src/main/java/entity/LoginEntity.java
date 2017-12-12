package entity;

import database.DatabaseController;
import database.connection.NotFoundException;
import database.objects.*;
import database.utility.DatabaseException;
import utility.KioskPermission;
import utility.request.Language;
import utility.request.RequestType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import static utility.KioskPermission.*;
import static utility.request.RequestType.INTERPRETER;

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
            currentLogin = new Employee("firstTimeSetup","","","root",new ArrayList<>(),
                    SUPER_USER,RequestType.GENERAL);
        } else {
            readAllFromDatabase();

            if (logins.size()==0) {
                // if there are no employees in the database, start as a super user
                currentLogin = new Employee("firstTimeSetup","","","root",new ArrayList<>(),
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

    public String getCurrentUsername() {
        return currentLogin.getUsername();
    }

    /**
     *
     * @return integer LoginID
     */
    public int getCurrentLoginID(){
        return currentLogin.getID();
    }

    /**
     * Adds every employee from the database to the logins hashmap
     * @return
     */
    public void readAllFromDatabase(){
        try {
            LinkedList<Employee> employees = database.getAllEmployees();
            for (Employee emp : employees) {
                // the employee hashmap is linked to usernames because of their uniqueness and ease of accessing
                this.logins.putIfAbsent(emp.getUsername(),emp);
            }
        } catch (DatabaseException e){

        }
    }

    /**
     * Gets all the login info from the entity, only allowed if currently a super user.
     * @return
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

    /**
     * Creates a list of employee Usernames for all the employees in the database
     * @return
     */
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
            loginIDs.add(employee.getID());
        }
        return loginIDs;
    }

    public LinkedList<Employee> getAllEmployeeType(Request request){
        LinkedList<Employee> employees = new LinkedList<>(logins.values());
        LinkedList<Employee> filterEmployees = new LinkedList<>();
        RequestType type = request.getRequestType();
        if(type.equals(RequestType.GENERAL)){
            return employees;
        }
        //for filtering out interpreters by language
        else if(request instanceof InterpreterRequest){
            InterpreterRequest iRequest = ((InterpreterRequest) request);
            for(Employee employee: employees){
                if(employee.getServiceAbility()==INTERPRETER){
                    ArrayList<Language> interpretersLanguages = new ArrayList<Language>();
                    for(String language : employee.getOptions()){
                            interpretersLanguages.add(Language.values()[Integer.parseInt(language)]);
                    }
                    if(interpretersLanguages.contains(iRequest.getLanguage())){
                        filterEmployees.add(employee);
                    }
                }
            }
        }
        else{
            for(Employee employee: employees){
                if(employee.getServiceAbility().equals(type)){
                    filterEmployees.add(employee);
                }
            }
        }
        return filterEmployees;
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
     * @param options a list of strings from EmployeeSettingsController
     * @param permission
     * @param serviceAbility
     * @return
     */
    public boolean addUser(String userName, String lastName, String firstName, String password, ArrayList<String> options,
                           KioskPermission permission, RequestType serviceAbility){
        // Idiot resistance
        if(currentLogin.getPermission()==NONEMPLOYEE||permission==NONEMPLOYEE){
            return false;
        }
        // updates the hashmap in case a employee is missing
        if(logins.containsKey(userName)){
            return false;
        }
        // limits creating new logins to subordinates in the KioskPermission hierarchy
        else if(currentLogin.getPermission().ordinal()>permission.ordinal()||
                currentLogin.getPermission() == SUPER_USER){
            // fitting it into the table
            if(userName.length()<=50){
                //different cases for different employee RequestTypes
                switch (serviceAbility){
                    case INTERPRETER:
                        for (int i = 0; i < options.size(); i++) {
                            String language = options.get(i);
                            language = String.valueOf(Language.valueOf(language).ordinal());
                            options.set(i,language);
                        }
                        break;
                }
                //constructs a temporary employee for database insertion
                Employee newEmployee=new Employee(userName,lastName,firstName, password, options, permission,
                        serviceAbility);
                try {
                    int ID = database.addEmployee(newEmployee,password);
                    newEmployee.setId(ID);
                    logins.put(userName, newEmployee);
                } catch (DatabaseException e){
                    return false;
                }
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
                try {
                    database.removeEmployee(delEmp.getID());
                    logins.remove(username);
                } catch (DatabaseException e){
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * only for the current login
     * @return the RequestType of the current log in
     */
    public RequestType getCurrentServiceAbility() {
        return currentLogin.getServiceAbility();
    }

    // Method for users to update only their passwords, and no one else's
    public boolean updatePassword(String newPassword, String oldPassword)throws NotFoundException{
        boolean updated = false;
        try {
            if (this.currentLogin.setPassword(newPassword, oldPassword)) {
                if (currentLogin instanceof Employee) {
                    Employee currentLogin = ((Employee) this.currentLogin);
                    database.updateEmployee(currentLogin, newPassword);
                    logins.replace(currentLogin.getUsername(), currentLogin);
                    updated = true;
                }
            }
        } catch (DatabaseException e){
            new NotFoundException("Employee not found");
        }
        return updated;
    }

    /**
     * for updating the current user's username
     * @param newUsername
     * @param password
     * @return
     */
    public boolean updateUsername(String newUsername, String password) {
        boolean updated=false;
        String oldUsername = currentLogin.getUsername();
        //pulling the user from the database seems cumbersome, but this avoids the problem associated with nonemployees
        //checks if the current user name is already in active use
        if(!logins.containsKey(newUsername)){
            updated=currentLogin.setUsername(newUsername,password);
        }
        if(updated) {
            if (currentLogin instanceof Employee) {
                try{
                Employee currentLogin = ((Employee) this.currentLogin);
                database.updateEmployee(currentLogin, password);
                logins.remove(oldUsername);
                logins.put(currentLogin.getUsername(), currentLogin);
                updated = true;
                } catch (DatabaseException e){
                    updated=false;
                }
            }
        }
        return updated;
    }

    /**
     * For checking log in credentials
     * THIS IS THE ONLY WAY FOR A USER TO UPGRADE THEIR ACTIVE PERMISSIONS
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

    /**
     * Used to return the list of languages an interpreter can speak for request filtering
     * @return ArrayList of Languages an interpreter can speak
     */
    public ArrayList<Language> getCurrentInterpreterLanguages(){
        ArrayList<Language> languages = new ArrayList<>();
        if(currentLogin.getServiceAbility()==INTERPRETER) {
            //parses the list of strings representing the ordinal language values into languages
            for (String languageValueOption : currentLogin.getOptions()) {
                languages.add(Language.values()[Integer.parseInt(languageValueOption)]);
            }
        }
        return languages;
    }
}
