package database.objects;

import utility.KioskPermission;
import utility.request.RequestType;

import java.util.ArrayList;

public class NullEmployee implements IEmployee {

    private static NullEmployee instance = null;

    public static NullEmployee getInstance(){
        return NullEmployeeSingletonHelper.instance;
    }

    private static class NullEmployeeSingletonHelper {

        private static final NullEmployee instance = new NullEmployee();
    }
    /**
     * Implementation of the null object pattern
     */
    private NullEmployee(){
    }

    /**
     * hno one should have a loginID<1
     * @return -1
     */
    public int getID(){
        return -1;
    }

    public String getUsername() {
        return "";
    }

    public String getFirstName() {
        return "";
    }

    public String getLastName() {
        return "";
    }

    public ArrayList<String> getOptions(){
        return new ArrayList<String>();
    }

    public KioskPermission getPermission() {
        return KioskPermission.NONEMPLOYEE;
    }

    public RequestType getServiceAbility(){
        return RequestType.GENERAL;
    }

    public String getPassword(String password){
        return "";
    }

    public boolean validatePassword(String password){
        return false;
    }

    public boolean setPassword(String newPassword, String oldPassword){
        return false;
    }

    public boolean setUsername(String newUsername, String password){
        return false;
    }
}
