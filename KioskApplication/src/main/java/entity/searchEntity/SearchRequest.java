package entity.searchEntity;

import database.connection.NotFoundException;
import database.objects.Request;
import database.utility.DatabaseException;
import entity.MapEntity;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utility.ResourceManager;
import utility.request.RequestProgressStatus;

public class SearchRequest implements ISearchEntity{
    Request databaseRequest;
    String searchString;
    ImageView requestIcon;

    public SearchRequest(Request request){
        this.databaseRequest = request;
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
                Image foodIcon = ResourceManager.getInstance().getImage("/images/icons/foodIcon.png");
                requestIcon = new ImageView(foodIcon);
                break;
            case INTERPRETER:
                Image interpreterIcon = ResourceManager.getInstance().getImage("/images/icons/interpreterIcon.png");
                requestIcon = new ImageView(interpreterIcon);
                break;
            case SECURITY:
                Image securityIcon = ResourceManager.getInstance().getImage("/images/icons/securityIcon.png");
                requestIcon = new ImageView(securityIcon);
                break;
            case JANITOR:
                Image janitorIcon = ResourceManager.getInstance().getImage("/images/icons/janitor.png");
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
}
