package entity.SearchEntity;

import database.connection.NotFoundException;
import database.objects.Employee;
import database.objects.Node;
import entity.MapEntity;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utility.ResourceManager;
import utility.request.RequestType;

public class SearchEmployee implements ISearchEntity{

    Employee databaseEmployee;
    SimpleObjectProperty<Employee> employeeSimpleObjectProperty;
    ImageView employeeIcon;
    String searchString;
    Node officeLocation;
    String officeName;

    public SearchEmployee(Employee databaseEmployee) {
        this.databaseEmployee = databaseEmployee;
        this.employeeSimpleObjectProperty = new SimpleObjectProperty<>(this.databaseEmployee);
        switch(databaseEmployee.getServiceAbility()) {
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

        if(databaseEmployee.getServiceAbility() == RequestType.DOCTOR) {
            try{
                String nodeID = databaseEmployee.getOptions().get(0);
                this.officeLocation = MapEntity.getInstance().getNode(nodeID);
                this.officeName = officeLocation.getLongName();
            }catch (NotFoundException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                String nodeID = databaseEmployee.getOptions().get(0);
                alert.setTitle("Can't found Location" + nodeID);
                alert.setContentText(e.toString());
                alert.showAndWait();
            }
        }
        else {
            officeLocation = null;
        }
        this.searchString = databaseEmployee.getLastName() + ", " + databaseEmployee.getFirstName() + " (" + officeName + ")";

        employeeSimpleObjectProperty.addListener(new ChangeListener<Employee>() {
            @Override
            public void changed(ObservableValue<? extends Employee> observable, Employee oldValue, Employee newValue) {
                System.out.println("employeeChanged");
            }
        });
    }


    public Object getData() {
        return databaseEmployee;
    }

    public String getSearchString() {
        return this.searchString;
    }

    public ImageView getIcon() {
        return this.employeeIcon;
    }

    public String getComparingString() {
        return Integer.toString(this.databaseEmployee.getID());
    }

    public String getName() {
        return this.databaseEmployee.getFirstName() + this.databaseEmployee.getLastName();
    }

    public Node getLocation() {
        return officeLocation;
    }
}
