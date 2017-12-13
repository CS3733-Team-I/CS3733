package controller.map;


import database.objects.Node;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;

public class WaypointView extends AnchorPane {

    PathWaypointView parent;

    Node node;
    int count;

    TranslateTransition waypointPutTransition;

    @FXML private ImageView imageView;
    @FXML private Label waypointCount;

    public WaypointView(PathWaypointView parent, Node node, int count) {
        this.parent = parent;
        this.node = node;
        this.count = count;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/WaypointView.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // put the pin and set it's info
        AnchorPane.setLeftAnchor(this, (double)node.getXcoord() - imageView.getFitWidth() / 2);
        AnchorPane.setTopAnchor(this, (double)node.getYcoord() - imageView.getFitHeight());

        waypointPutTransition = new TranslateTransition();
        waypointPutTransition.setDuration(Duration.millis(400));
        waypointPutTransition.setNode(this);
        waypointPutTransition.setFromY(this.getLayoutY()-60);
        waypointPutTransition.setToY(this.getLayoutY());

        this.setWaypointCount(count);
    }

    public void playWaypointPutTransition() {
        waypointPutTransition.play();
    }

    public void setWaypointCount(int i) {
        this.waypointCount.setText(Integer.toString(i+1));
    }
}
