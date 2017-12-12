package entity.SearchEntity;

import database.connection.NotFoundException;
import database.objects.Employee;
import database.objects.Node;
import entity.MapEntity;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utility.ResourceManager;
import utility.request.RequestType;

import java.util.Map;

public class SearchEmployee implements ISearchEntity{

    Employee databaseEmploy;
    ImageView employeeIcon;
    String searchString;
    Node officeLocation;
    String officeName;

    public SearchEmployee(Employee databaseEmploy) {
        this.databaseEmploy = databaseEmploy;
        switch(databaseEmploy.getServiceAbility()) {
            case GENERAL:
                Image generalIcon = ResourceManager.getInstance().getImage("/images/icons/Staff/staff_general.png");
                employeeIcon = new ImageView(generalIcon);
                break;
            case DOCTOR:
                Image doctorIcon = ResourceManager.getInstance().getImage("/images/icons/Staff/staff_doctor.png");
                employeeIcon = new ImageView(doctorIcon);
                break;
            case INTERPRETER:
                Image interpreterIcon = ResourceManager.getInstance().getImage("/images/icons/Staff/staff_interpreter.png");
                employeeIcon = new ImageView(interpreterIcon);
                break;
            case SECURITY:
                Image securityIcon = ResourceManager.getInstance().getImage("/images/icons/Staff/staff_security.png");
                employeeIcon = new ImageView(securityIcon);
                break;
            case JANITOR:
                Image janitorIcon = ResourceManager.getInstance().getImage("/images/icons/Staff/staff_janitor.png");
                employeeIcon = new ImageView(janitorIcon);
                break;
            case IT:
                Image ITIcon = ResourceManager.getInstance().getImage("/images/icons/Staff/staff_IT.png");
                employeeIcon = new ImageView(ITIcon);
                break;
            case MAINTENANCE:
                Image maintenanceIcon = ResourceManager.getInstance().getImage("/images/icons/Staff/staff_maintenance.png");
                employeeIcon = new ImageView(maintenanceIcon);
                break;
            case INTERNAL_TRANSPORTATION:
                Image iTransportIcon = ResourceManager.getInstance().getImage("/images/icons/Staff/staff_internalTransport.png");
                employeeIcon = new ImageView(iTransportIcon);
                break;
            case EXTERNAL_TRANSPORTATION:
                Image eTransportIcon = ResourceManager.getInstance().getImage("/images/icons/Staff/staff_externalTransport.png");
                employeeIcon = new ImageView(eTransportIcon);
                break;
            default:
                Image defaultIcon = ResourceManager.getInstance().getImage("/images/icons/nukeIcon.png");
                employeeIcon = new ImageView(defaultIcon);
                break;
        }

        employeeIcon.setFitHeight(48);
        employeeIcon.setFitWidth(48);

        String nodeID = databaseEmploy.getOptions().get(0);
        try{
            this.officeLocation = MapEntity.getInstance().getNode(nodeID);
            this.officeName = officeLocation.getLongName();
        }catch (NotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Can't found Location" + nodeID);
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
        this.searchString = databaseEmploy.getLastName() + ", " + databaseEmploy.getFirstName() + " (" + officeName + ")";
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
        return this.databaseEmploy.getServiceAbility().toString() + this.databaseEmploy.getFirstName() + this.databaseEmploy.getLastName();
    }

    public Node getLocation() {
        return officeLocation;
    }
}
