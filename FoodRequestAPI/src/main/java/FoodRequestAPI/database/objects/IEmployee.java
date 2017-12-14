package FoodRequestAPI.database.objects;

import FoodRequestAPI.utility.KioskPermission;
import FoodRequestAPI.utility.request.RequestType;

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

    String getOptions();

    KioskPermission getPermission();

    RequestType getServiceAbility();
}
