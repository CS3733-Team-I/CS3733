package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import controller.map.MapController;
import database.objects.Edge;
import database.objects.Node;
import entity.SystemSettings;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import pathfinder.Pathfinder;
import pathfinder.PathfinderException;
import utility.node.NodeFloor;

import java.io.IOException;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class PathfindingSidebarController extends ScreenController {

    @FXML private AnchorPane container;
    @FXML private GridPane waypointsContainer;
    @FXML private JFXListView waypointList;
    @FXML private JFXButton btnSubmit;
    @FXML private JFXTextField searchBar;
    @FXML private Label waypointLabel;
    @FXML private JFXButton navigateButton;
    @FXML private JFXButton clearButton;

    @FXML private Label exceptionText;

    private LinkedList<Node> currentNodes;

    public PathfindingSidebarController(MainWindowController parent, MapController map) {
        super(parent, map);
        currentNodes = new LinkedList<>();
    }

    @FXML
    void initialize() {
        // Set containers to be transparent to mouse events
        ResourceBundle rB = SystemSettings.getInstance().getResourceBundle();
        getMapController().setFloorSelector(NodeFloor.THIRD);
        container.setPickOnBounds(false);
        waypointsContainer.setPickOnBounds(false);
        waypointList.setPickOnBounds(false);
        exceptionText.setText("");
        // for setting the pathfinding sidebar to the internationalized language
        btnSubmit.setText(rB.getString("my.search"));
        searchBar.setText(rB.getString("my.search"));
        clearButton.setText(rB.getString("my.clear"));
        navigateButton.setText(rB.getString("my.navigate"));
        waypointLabel.setText(rB.getString("my.waypoints"));
    }

    @FXML
    void onResetPressed() {
        currentNodes.clear();
        waypointList.getItems().clear();
        exceptionText.setText("");

        getMapController().setPath(null);
        getMapController().clearMap();
        getMapController().reloadDisplay();
    }

    @FXML
    void btGeneratePathPressed() throws IOException {
        exceptionText.setText("");
        if (currentNodes.size() > 0) {
            Pathfinder pathfinder = new Pathfinder(SystemSettings.getInstance().getAlgorithm());
            try{
                getMapController().setPath(pathfinder.generatePath(currentNodes));
                waypointList.getItems().clear();
                LinkedList<LinkedList<String>> directionsList = getMapController().getPath().getDirectionsList();
                for(LinkedList<String> directionSegment: directionsList) {
                    for (String direction : directionSegment) {
                        Label label = new Label(direction);
                        label.setTextFill(Color.BLACK);
                        waypointList.getItems().add(label);
                    }
                }
            }
            catch(PathfinderException exception){
                exceptionText.setText("ERROR! "+ exception.getMessage());
            }

            currentNodes.clear();
        }
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
            waypointList.getItems().add(nodeNameLabel);

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
        // Set the map size
        getMapController().setAnchor(0, 300, 0, 0);

        // Reset mapcontroller
        onResetPressed();

        // Set default nodes/edges visibility
        getMapController().setNodesVisible(true);
        getMapController().setEdgesVisible(true);

        // Set if the options box is visible
        getMapController().setOptionsBoxVisible(false);
        ResourceBundle rB = SystemSettings.getInstance().getResourceBundle();
        // for setting the pathfinding sidebar to the internationalized language
        btnSubmit.setText(rB.getString("my.search"));
        searchBar.setText(rB.getString("my.search"));
        clearButton.setText(rB.getString("my.clear"));
        navigateButton.setText(rB.getString("my.navigate"));
        waypointLabel.setText(rB.getString("my.waypoints"));
    }
}