package entity.SearchEntity;

import database.objects.Employee;
import javafx.scene.image.ImageView;

public class SearchEmployee implements ISearchEntity{

    Employee databaseEmploy;
    ImageView employeeIcon;
    String searchString;

    public SearchEmployee(Employee databaseEmploy) {
        this.databaseEmploy = databaseEmploy;
    }

    public Object getData() {
        return databaseEmploy;
    }

    public String getSearchString() {
        return this.searchString;
    }

    public ImageView getIcon() {
        return this.employeeIcon;
    }

    public String getComparingString() {
        return Integer.toString(this.databaseEmploy.getID());
    }

    public String getName() {
        return this.databaseEmploy.getFirstName() + this.databaseEmploy.getLastName();
    }
}
