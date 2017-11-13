package KioskApplication.controller;

import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;
import KioskApplication.entity.MapEntity;
import KioskApplication.entity.Path;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.io.IOException;

public class MapController {
    // TODO Enable loading different maps via a function

    @FXML private ImageView mapView;
    @FXML private StackPane stackPane;
    @FXML private ScrollPane scrollPane;

    private MapWindowController parent = null;

    private static double DEFAULT_HVALUE = 0.52;
    private static double DEFAULT_VVALUE = 0.3;

    public void setParent(MapWindowController controller) {
        parent = controller;
    }

    public void drawPath(Path path) throws IOException {
        MapEntity mapEntity = MapEntity.getInstance();

        stackPane.getChildren().clear();
        stackPane.getChildren().add(mapView);

        for (Node n : path.getWaypoints()) {
            javafx.scene.Node nodeObject = FXMLLoader.load(getClass().getResource("/KioskApplication/view/NodeView.fxml"));
            nodeObject.setTranslateX(n.getXcoord() - 14);
            nodeObject.setTranslateY(n.getYcoord() - 14);
            stackPane.getChildren().add(nodeObject);
        }

        AnchorPane edgesPane = new AnchorPane();
        for (Edge e : path.getEdges()) {
            Node node1 = mapEntity.getNode(e.getNode1ID());
            Node node2 = mapEntity.getNode(e.getNode2ID());

            // TODO(jerry) make this more modular, maybe into an FXML file??
            Line edgeView = new Line(node1.getXcoord(), node1.getYcoord(), node2.getXcoord(), node2.getYcoord());
            edgeView.setStroke(Color.PURPLE);
            edgeView.setStrokeWidth(10);
            edgesPane.getChildren().add(edgeView);
        }
        stackPane.getChildren().add(edgesPane);
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
        this.scrollPane.setHvalue(DEFAULT_HVALUE);
        this.scrollPane.setVvalue(DEFAULT_VVALUE);
    }
}
