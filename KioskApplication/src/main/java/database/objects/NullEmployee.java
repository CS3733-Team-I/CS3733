package database.objects;

import utility.KioskPermission;
import utility.request.RequestType;

public class NullEmployee extends AbsEmployee {

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
     * hoping this is a valid null value because no one should have a loginID<0
     * @return -1
     */
    public int getLoginID(){
        return -1;
    }

    public String getUsername() {
        return "";
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

    public boolean updatePassword(String newPassword, String oldPassword){
        return false;
    }

    public boolean updateUsername(String newUsername, String password){
        return false;
    }
}
