package database.objects;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import utility.KioskPermission;
import utility.request.RequestType;

/**
 * This abstract class lets us implement the null object pattern for the loginEntity
 */
public abstract class AbsEmployee extends RecursiveTreeObject<Employee> {

    public abstract String getPassword(String password);

    public abstract boolean validatePassword(String password);

    public abstract boolean updatePassword(String newPassword, String oldPassword);

    public abstract boolean updateUsername(String newUsername, String password);

    public abstract int getLoginID();

    public abstract String getUsername();

    public abstract KioskPermission getPermission();

    public abstract RequestType getServiceAbility();
}
