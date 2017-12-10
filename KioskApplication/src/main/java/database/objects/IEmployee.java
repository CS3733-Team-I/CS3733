package database.objects;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import utility.KioskPermission;
import utility.request.RequestType;

import java.util.ArrayList;

/**
 * This Interface lets us implement the null object pattern for the loginEntity
 */
public interface IEmployee {

    String getPassword(String password);

    boolean validatePassword(String password);

    boolean updatePassword(String newPassword, String oldPassword);

    boolean updateUsername(String newUsername, String password);

    int getID();

    String getUsername();

    String getLastName();

    String getFirstName();

    ArrayList<String> getOptions();

    KioskPermission getPermission();

    RequestType getServiceAbility();
}
