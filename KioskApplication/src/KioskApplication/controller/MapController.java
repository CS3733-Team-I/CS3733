package KioskApplication.controller;

import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;
import KioskApplication.entity.MapEntity;
import KioskApplication.entity.Path;
import KioskApplication.utility.NodeFloor;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class MapController {
    @FXML private ImageView mapView;
    @FXML private StackPane stackPane;
    @FXML private ScrollPane scrollPane;
    @FXML private ComboBox<NodeFloor> floorSelector;

    private MapWindowController parent = null;

    private static double DEFAULT_HVALUE = 0.52;
    private static double DEFAULT_VVALUE = 0.3;

    private NodeFloor currentFloor = NodeFloor.THIRD;
    private HashMap<NodeFloor, Point2D> previousPositions;

    public MapController() {
        previousPositions = new HashMap<>();
    }

    public void setParent(MapWindowController controller) {
        parent = controller;
    }

    public void drawPath(Path path) throws IOException {
        MapEntity mapEntity = MapEntity.getInstance();

        // Change to floor of the starting node
        loadFloor(path.getWaypoints().get(0).getFloor());

        stackPane.getChildren().clear();
        stackPane.getChildren().add(mapView);

        // Draw edges
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

        // Draw nodes
        for (Node n : path.getWaypoints()) {
            javafx.scene.Node nodeObject = FXMLLoader.load(getClass().getResource("/KioskApplication/view/NodeView.fxml"));
            nodeObject.setTranslateX(n.getXcoord() - 14); // TODO magic numbers
            nodeObject.setTranslateY(n.getYcoord() - 14); // TODO magic numbers
            stackPane.getChildren().add(nodeObject);
        }

        System.out.println("Path!");
    }

    private void loadFloor(NodeFloor floor) {
        String floorImageURL = "";
        switch (floor) {
            case LOWERLEVEL_2:
                floorImageURL = getClass().getResource("/KioskApplication/resources/images/00_thelowerleve21.png").toString();
                break;
            case LOWERLEVEL_1:
                floorImageURL = getClass().getResource("/KioskApplication/resources/images/00_thelowerlevel1.png").toString();
                break;
            case GROUND:
                floorImageURL = getClass().getResource("/KioskApplication/resources/images/00_thegroundfloor.png").toString();
                break;
            case FIRST:
                floorImageURL = getClass().getResource("/KioskApplication/resources/images/01_thefirstfloor.png").toString();
                break;
            case SECOND:
                floorImageURL = getClass().getResource("/KioskApplication/resources/images/02_thesecondfloor.png").toString();
                break;
            case THIRD:
                floorImageURL = getClass().getResource("/KioskApplication/resources/images/03_thethirdfloor.png").toString();
                break;
        }

        Image floorImage = new Image(floorImageURL);
        mapView.setImage(floorImage);
        mapView.setFitWidth(floorImage.getWidth());
        mapView.setFitHeight(floorImage.getHeight());

        Point2D currentMapPosition = new Point2D(scrollPane.getHvalue(), scrollPane.getVvalue());
        previousPositions.put(currentFloor, currentMapPosition);

        try {
            Point2D lastMapPosition = previousPositions.get(floor);
            scrollPane.setHvalue(lastMapPosition.getX());
            scrollPane.setVvalue(lastMapPosition.getY());
        } catch (Exception e) {
            // Previous position didn't exist, just set default values for now
            scrollPane.setHvalue(0.5);
            scrollPane.setVvalue(0.5);
        }

        currentFloor = floor;
    }

    public NodeFloor getCurrentFloor() {
        return currentFloor;
    }

    @FXML
    public void initialize() {
        floorSelector.getItems().addAll(NodeFloor.values());

        loadFloor(currentFloor);
    }

    @FXML
    void onMapClicked(MouseEvent event) {
        if (!parent.equals(null)) {
            // Check if clicked location is a node
            LinkedList<Node> floorNodes = MapEntity.getInstance().getNodesOnFloor(currentFloor);
            for (Node node : floorNodes) {
                Rectangle2D nodeArea = new Rectangle2D(node.getXcoord() - 15, node.getYcoord() - 15,
                                                      30, 30); // TODO magic numbers
                Point2D clickPosition = new Point2D(event.getX(), event.getY());

                if (nodeArea.contains(clickPosition)) {
                    parent.mapNodeClicked(node);
                    return;
                }
            }

            // Otherwise return the x,y coordinates
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

    @FXML
    public void floorSelected() {
        NodeFloor selectedFloor = floorSelector.getSelectionModel().getSelectedItem();
        loadFloor(selectedFloor);
    }
}
