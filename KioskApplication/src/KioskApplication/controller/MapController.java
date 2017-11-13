package KioskApplication.controller;

import KioskApplication.database.objects.Node;
import KioskApplication.entity.MapEntity;
import KioskApplication.utility.MathUtility;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.ArrayList;

public class MapController {
    // TODO Enable loading different maps via a function

    @FXML private ImageView mapView;
    @FXML private StackPane stackPane;
    @FXML private ScrollPane scrollPane;

    private MapWindowController parent = null;
    private Rectangle2D defaultViewport = new Rectangle2D(2250, 875, 600, 500);

    private static int MIN_PIXELS = 600;

    public void setParent(MapWindowController controller) {
        parent = controller;
    }

    @FXML
    public void initialize() throws IOException {
        MapEntity mapEntity = MapEntity.getInstance();
        ArrayList<Node> nodes = mapEntity.getAllNodes();

        stackPane.getChildren().clear();
        stackPane.getChildren().add(mapView);

        for (Node n : nodes) {
            javafx.scene.Node nodeObject = FXMLLoader.load(getClass().getResource("/KioskApplication/view/NodeView.fxml"));
            nodeObject.setTranslateX(n.getXcoord());
            nodeObject.setTranslateY(n.getYcoord());
            stackPane.getChildren().add(nodeObject);
        }

        System.out.println("MapController");
    }

    @FXML
    void onMapClicked(MouseEvent event) {
        if (!parent.equals(null)) {
            parent.mapLocationClicked(event.getX(), event.getY());
        }
    }

    @FXML
    public void zoomInPressed() {
        System.out.println("Zoom in pressed.");
    }

    @FXML
    public void zoomOutPressed() {
        System.out.println("Zoom out pressed.");
    }

    @FXML
    public void recenterPressed() {
        mapView.setViewport(defaultViewport);
    }
}
