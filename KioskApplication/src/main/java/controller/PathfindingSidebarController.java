package controller;

import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;
import entity.SystemSettings;
import entity.Path;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import pathfinder.Pathfinder;
import pathfinder.PathfinderException;
import sun.awt.image.ImageWatched;
import utility.node.NodeFloor;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.LinkedList;

public class PathfindingSidebarController extends ScreenController {

    @FXML private AnchorPane container;
    @FXML private AnchorPane waypointsContainer;
    @FXML private VBox waypointList;

    @FXML private Label exceptionText;

    private LinkedList<Node> currentNodes;

    public PathfindingSidebarController(MainWindowController parent, MapController map) {
        super(parent, map);
        currentNodes = new LinkedList<>();
    }

    @FXML
    void initialize() {
        // Set containers to be transparent to mouse events
        mapController.floorSelector.setValue(NodeFloor.THIRD);
        container.setPickOnBounds(false);
        waypointsContainer.setPickOnBounds(false);
        waypointList.setPickOnBounds(false);
        exceptionText.setText("");
    }

    @FXML
    void onResetPressed() {
        currentNodes.clear();
        waypointList.getChildren().clear();
        exceptionText.setText("");
        getMapController().clearMap();
    }

    @FXML
    void btGeneratePathPressed() throws IOException {
        exceptionText.setText("");
        if (currentNodes.size() > 0) {
            Pathfinder pathfinder = new Pathfinder(SystemSettings.getInstance().getAlgorithm());
            try{
                getMapController().setPath(pathfinder.generatePath(currentNodes));
                waypointList.getChildren().clear();
                LinkedList<LinkedList<String>> directionsList = getMapController().getPath().getDirectionsList();
                for(LinkedList<String> directionSegment: directionsList) {
                    for (String direction : directionSegment) {
                        Label label = new Label(direction);
                        label.setTextFill(Color.BLACK);
                        waypointList.getChildren().add(label);
                    }
                }
                getMapController().drawPath();
            }
            catch(PathfinderException exception){
                exceptionText.setText("ERROR! "+ exception.getMessage());
            }



            currentNodes.clear();
        }
    }

    @FXML
    void btClearPathPressed() throws IOException {
        getMapController().clearMap();
        getMapController().setPath(null);
        exceptionText.setText("");
    }

    @Override
    public javafx.scene.Node getContentView() {
        if (contentView == null) {
            contentView = loadView("/view/PathfindingSidebarView.fxml");
        }

        return contentView;
    }

    @Override
    public void onMapLocationClicked(javafx.scene.input.MouseEvent e, Point2D location) {

    }

    @Override
    public void onMapNodeClicked(Node node) {
        if (!currentNodes.contains(node)) {
            currentNodes.add(node);

            Label nodeNameLabel = new Label(node.getNodeID());
            nodeNameLabel.setTextFill(Color.BLACK);
            waypointList.getChildren().add(nodeNameLabel);

            getMapController().addWaypoint(new Point2D(node.getXcoord(), node.getYcoord()));
        }
    }

    @Override
    public void onMapEdgeClicked(Edge edge) {

    }

    @Override
    public void onMapFloorChanged(NodeFloor floor) {
    }

    @Override
    public void onScreenChanged() {

    }

    @Override
    public void resetScreen() {
        onResetPressed();

        getMapController().reloadDisplay();

        getMapController().setAnchor(0, 300, 0, 0);
    }
}