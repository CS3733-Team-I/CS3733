package entity;

import database.DatabaseController;
import utility.KioskPermission;

import java.util.HashMap;

import static utility.KioskPermission.ADMIN;
import static utility.KioskPermission.EMPLOYEE;
import static utility.KioskPermission.NONEMPLOYEE;

/**
 * Should handle user credential validation and connection to the user database for LoginController
 * Should also pass information to indicate the MainWindowController's state as Nonemployee, Employee, & Admin
 */
public class LoginEntity {
    private DatabaseController dbC;
    private KioskPermission permission;
    private String loginName;
    // TODO: make these less vulnerable
    private HashMap<String,String> employees;
    private HashMap<String,String> admins;

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
        employees = new HashMap<>();
        admins = new HashMap<>();
        this.loginName="";
        if (test){
            // so tests can add and remove logins as needed
            permission = ADMIN;
            dbC = DatabaseController.getTestInstance();
        }
        else {
            // initial login state, we don't want anyone to restart the application and gain access to admin powers
            permission = NONEMPLOYEE;
            dbC = DatabaseController.getInstance();
            // TODO: remove this backdoor once a more secure method of initially tracking admin logins is developed
            admins.putIfAbsent("boss@hospital.com", "123");
            employees.putIfAbsent("emp@hospital.com", "12");
        }
    }

    public KioskPermission getPermission() {
        return permission;
    }

    public String getLoginName() {
        return loginName;
    }

    // TODO add security methods to this
    public void addLogin(String loginName, String password, boolean admin){
        if(this.permission==ADMIN){
            if(admin){
                if (dbC.equals(DatabaseController.getTestInstance())) {
                    admins.putIfAbsent(loginName, password);
                }
            }
            else {
                employees.putIfAbsent(loginName, password);
            }
        }
    }

    // TODO prevent people from locking themselves and others out in a nontest scenario
    public void deleteLogin(String loginName, String password){
        // Verifies that the user is an Admin
        if(this.permission==ADMIN) {
            // test cleanup method, haven't made this ready to work with the actual application
            if (dbC.equals(DatabaseController.getTestInstance())) {
                if(employees.containsKey(loginName)) {
                    if(employees.get(loginName).equals(password)) {
                        employees.remove(loginName);
                    }
                }
                else if (admins.containsKey(loginName)){
                    if(admins.get(loginName).equals(password)) {
                        admins.remove(loginName);
                    }
                }
            }
        }
        else {
            return;
        }
    }

    // Method for users to update only their passwords, and no one else's
    public boolean updatePassword(String newPassword, String oldPassword){
        boolean updated = false;
        switch (permission) {
            case ADMIN:
                if (oldPassword.equals(admins.get(loginName))) {
                    admins.replace(loginName, oldPassword, newPassword);
                    updated = true;
                }
                break;
            case EMPLOYEE:
                if (oldPassword.equals(employees.get(loginName))) {
                    employees.replace(loginName, oldPassword, newPassword);
                    updated = true;
                }
        }
        return updated;
    }

    // For checking log in credentials
    // THIS IS THE ONLY WAY FOR A USER TO UPGRADE THEIR PERMISSIONS
    public KioskPermission validate(String loginName, String password){
        if(admins.containsKey(loginName)){
            if (admins.get(loginName).equals(password)){
                this.loginName = loginName;
                permission = ADMIN;
            }
            else {
                validationFail();
            }
        }
        else if(employees.containsKey(loginName)){
            if (employees.get(loginName).equals(password)){
                this.loginName = loginName;
                permission = EMPLOYEE;
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
     * Helper method for validate, while it is technically identical in function to logOut(),
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
