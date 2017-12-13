package entity.SearchEntity;

import database.connection.NotFoundException;
import database.objects.Request;
import entity.MapEntity;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utility.ResourceManager;
import utility.request.RequestProgressStatus;

public class SearchRequest implements ISearchEntity{
    Request databaseRequest;
    SimpleObjectProperty<Request> requestSimpleObjectProperty;
    String searchString;
    ImageView requestIcon;

    public SearchRequest(Request request){
        this.databaseRequest = request;
        this.requestSimpleObjectProperty = new SimpleObjectProperty<>(this.databaseRequest);
        try{
            this.searchString = "Status:" + request.getStatus().toString() + "  At"+ MapEntity.getInstance().getNode(request.getNodeID()).getLongName() + " Assigned by" + request.getAssignerID()
                + " Submitted at" + request.getSubmittedTime().toString();
        }catch (NotFoundException e) {
            e.printStackTrace();
        }
        if(request.getStatus() == RequestProgressStatus.IN_PROGRESS) {
            searchString += "; Started at " + request.getStartedTime().toString() + " by " + request.getCompleterID();
        }
        if(request.getStatus() == RequestProgressStatus.DONE) {
            searchString += "; Started at " + request.getStartedTime().toString() + " by " + request.getCompleterID();
            searchString += "; Completed at " + request.getCompletedTime().toString() + " by " + request.getCompleterID();
        }

        switch (request.getRequestType()) {
            case FOOD:
                Image foodIcon = ResourceManager.getInstance().getImage("/images/icons/foodBlue.png");
                requestIcon = new ImageView(foodIcon);
                break;
            case INTERPRETER:
                Image interpreterIcon = ResourceManager.getInstance().getImage("/images/icons/translateBlue.png");
                requestIcon = new ImageView(interpreterIcon);
                break;
            case SECURITY:
                Image securityIcon = ResourceManager.getInstance().getImage("/images/icons/securityBlue.png");
                requestIcon = new ImageView(securityIcon);
                break;
            case JANITOR:
                Image janitorIcon = ResourceManager.getInstance().getImage("/images/icons/janitorBlue.png");
                requestIcon = new ImageView(janitorIcon);
                break;
            default:
                Image defaultIcon = ResourceManager.getInstance().getImage("/images/icons/nukeIcon.png");
                requestIcon = new ImageView(defaultIcon);
        }
        requestIcon.setFitHeight(48);
        requestIcon.setFitWidth(48);
    }

    public Object getData() {
        return databaseRequest;
    }
    public String getSearchString() {
        return searchString;
    }
    public ImageView getIcon() {
        return requestIcon;
    }
    public String getComparingString() {
        return databaseRequest.getRequestID();
    }
    public String getName() {
        return searchString;
    }
    public Node getLocation() {
        try {
            return MapEntity.getInstance().getNode(databaseRequest.getNodeID());
        }
        catch (NotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Can't found Location" + databaseRequest.getNodeID());
            alert.setContentText(e.toString());
            alert.showAndWait();
        }
        return null;
    }
}
