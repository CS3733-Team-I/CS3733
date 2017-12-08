package controller.map;


import database.objects.Node;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import utility.node.NodeSelectionType;

import java.io.IOException;

public class WaypointView extends StackPane {

    PathWaypointView parent;

    Node node;

    TranslateTransition waypointPutTransition;

    @FXML private MenuButton waypoint;
    @FXML private StackPane container;
    @FXML private ImageView imageView;
    @FXML private Label waypointCount;

    public WaypointView(PathWaypointView parent, Node node, int count) {
        this.parent = parent;
        waypointPutTransition = new TranslateTransition();
        this.node = node;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/WaypointView.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.setWaypointCount(count);
    }

    @FXML
    public void initialize() throws  IOException{
        // put the pin and set it's info

//        waypoint.setTranslateX(node.getXcoord() - 24);
//        waypoint.setTranslateY(- 60 + node.getYcoord() - 60);
//        this.setAccessibleText(node.getNodeID());
        waypoint.setStyle("-fx-background-color: #ff1d13;");
        waypoint.setAccessibleText(node.getNodeID());
        waypoint.setAccessibleHelp("waypoint");

        waypointPutTransition.setDuration(Duration.millis(400));
        waypointPutTransition.setNode(waypoint);
        waypointPutTransition.setFromY(waypoint.getLayoutY()-60);
        waypointPutTransition.setToY(waypoint.getLayoutY());

        //TODO handle waypoint option
        Tooltip nodeInfo = new Tooltip(node.getLongName());
        Tooltip.install(waypoint, nodeInfo);
        nodeInfo.setStyle("-fx-font-weight:bold; " +
                "-fx-background-color: #ff1d13;" +
                "-fx-font-size: 16pt; ");

        container.setLayoutX(node.getXcoord() - 24);
        container.setLayoutY(node.getYcoord()- 60);
    }


    public void playWaypointPutTransition()
    {
        waypointPutTransition.play();
    }

    public void setWaypointCount(int i) {
        this.waypointCount.setText(Integer.toString(i+1));
    }

    public void copy(WaypointView waypointView) {

    }
}
