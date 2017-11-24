package entity;

import database.DatabaseController;
import utility.KioskPermission;
import utility.Request.Language;

import java.util.HashMap;

/**
 * Should handle user credential validation and connection to the user database for LoginController
 * Should also pass information to indicate the MainWindowController's state as Nonemployee, Employee, & Admin
 */
public class LoginEntity {
    private DatabaseController dbC;
    private KioskPermission permission;
    private String userName;
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
        if (test){
            dbC = DatabaseController.getTestInstance();
        }
        else {
            dbC = DatabaseController.getInstance();
        }
    }

    public KioskPermission getPermission() {
        return permission;
    }

    public String getUserName() {
        return userName;
    }

    //For checking log in credentials
    public KioskPermission validate(String userName, String password){
        this.admins.putIfAbsent("boss@hospital.com","123");
        this.employees.putIfAbsent("emp@hospital.com","1");
        if(admins.containsKey(userName)){
            if (admins.get(userName).equals(password)){
                this.userName = userName;
                permission = KioskPermission.ADMIN;
            }
            else {
                validationFail();
            }
        }
        else if(employees.containsKey(userName)){
            if (employees.get(userName).equals(password)){
                this.userName = userName;
                permission = KioskPermission.EMPLOYEE;
            }
            else {
                validationFail();
            }
        }
        else {
            validationFail();
        }
        return this.permission;
    }

    //Helper method for validate
    private void validationFail(){
        this.userName = "";
        permission = KioskPermission.NONEMPLOYEE;
    }
}
