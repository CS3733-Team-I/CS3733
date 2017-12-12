package FoodRequestAPI.entity;

import FoodRequestAPI.database.objects.Node;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import FoodRequestAPI.utility.ResourceManager;


public class SearchNode {
    Node databaseNode;
    String searchString;
    ImageView nodeIcon;
    SimpleIntegerProperty distance;

    public SearchNode(Node node) {
        this.databaseNode = node;
        this.distance = new SimpleIntegerProperty(MapEntity.getInstance().getDistanceFromKiosk(node));
        //TODO implement distance
        this.searchString = distance.get() + "ft " + node.getLongName() + " (" + node.getFloor() + ")";

        switch (node.getNodeType()) {
            case REST:
                Image restroomIcon = ResourceManager.getInstance().getImage("/images/icons/restroom.png");
                this.nodeIcon = new ImageView(restroomIcon);
                nodeIcon.setFitHeight(48);
                nodeIcon.setFitWidth(48);
                break;
            case ELEV:
                Image elevIcon = ResourceManager.getInstance().getImage("/images/icons/elevator.png");
                nodeIcon= new ImageView(elevIcon);
                nodeIcon.setFitHeight(48);
                nodeIcon.setFitWidth(48);
                break;
            case EXIT:
                Image exitIcon = ResourceManager.getInstance().getImage("/images/icons/exit.png");
                nodeIcon = new ImageView(exitIcon);
                nodeIcon.setFitHeight(48);
                nodeIcon.setFitWidth(48);
                break;
            case RETL:
                Image foodIcon = ResourceManager.getInstance().getImage("/images/icons/food.png");
                nodeIcon = new ImageView(foodIcon);
                nodeIcon.setFitHeight(48);
                nodeIcon.setFitWidth(48);
                break;
            case CONF:
                Image confIcon = ResourceManager.getInstance().getImage("/images/icons/conf.png");
                nodeIcon = new ImageView(confIcon);
                nodeIcon.setFitHeight(48);
                nodeIcon.setFitWidth(48);
                break;
            case DEPT:
                Image deptIcon = ResourceManager.getInstance().getImage("/images/icons/department.png");
                nodeIcon = new ImageView(deptIcon);
                nodeIcon.setFitHeight(48);
                nodeIcon.setFitWidth(48);
                break;
            case LABS:
                Image labIcon = ResourceManager.getInstance().getImage("/images/icons/lab.png");
                nodeIcon = new ImageView(labIcon);
                nodeIcon.setFitHeight(48);
                nodeIcon.setFitWidth(48);
                break;
            case INFO:
                Image nodeInfoIcon = ResourceManager.getInstance().getImage("/images/icons/nodeInfo.png");
                nodeIcon = new ImageView(nodeInfoIcon);
                nodeIcon.setFitHeight(48);
                nodeIcon.setFitWidth(48);
                break;
            case SERV:
                Image servIcon = ResourceManager.getInstance().getImage("/images/icons/service.png");
                nodeIcon = new ImageView(servIcon);
                nodeIcon.setFitHeight(48);
                nodeIcon.setFitWidth(48);
                break;
            case STAI:
                Image staiIcon = ResourceManager.getInstance().getImage("/images/icons/stairs.png");
                nodeIcon = new ImageView(staiIcon);
                nodeIcon.setFitHeight(48);
                nodeIcon.setFitWidth(48);
                break;
            default:
                //TODO change this
                Image defaultIcon = ResourceManager.getInstance().getImage("/images/icons/nukeIcon.png");
                nodeIcon = new ImageView(defaultIcon);
                nodeIcon.setFitHeight(48);
                nodeIcon.setFitWidth(48);
        }
    }

    public Node getDatabaseNode() {
        return databaseNode;
    }

    public String getSearchString() {
        return searchString;
    }

    public ImageView getNodeIcon() {
        return nodeIcon;
    }
}
