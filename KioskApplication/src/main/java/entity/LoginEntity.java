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
            permission = KioskPermission.ADMIN;
            dbC = DatabaseController.getTestInstance();
        }
        else {
            permission = KioskPermission.NONEMPLOYEE;
            dbC = DatabaseController.getInstance();
        }
    }

    public KioskPermission getPermission() {
        return permission;
    }

    public String getUserName() {
        return userName;
    }

    //TODO add some sorts of security methods to this
    public void addLogin(String userName, String password, boolean admin){
        if(this.permission==KioskPermission.ADMIN){
            if(admin){
                if (dbC.equals(DatabaseController.getTestInstance())) {
                    admins.putIfAbsent(userName, password);
                }
            }
            else {
                employees.putIfAbsent(userName, password);
            }
        }
    }

    //TODO prevent people from locking themselves and others out in a nontest scenario
    public void deleteLogin(String userName){
        //Verifies that the user is an Admin
        if(this.permission==KioskPermission.ADMIN) {
            if (dbC.equals(DatabaseController.getTestInstance())) {
                employees.remove(userName);
            }
        }
        else if (admins.containsKey(userName)){
            //test cleanup method
            if(dbC.equals(DatabaseController.getTestInstance())){
                admins.remove(userName);
            }
        }
        else {
            return;
        }
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
