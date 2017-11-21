package controller;

import com.jfoenix.controls.JFXCheckBox;
import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;
import entity.Path;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import pathfinder.Pathfinder;
import utility.NodeFloor;

import java.io.IOException;
import java.util.LinkedList;

public class PathfindingSidebarController extends ScreenController {

    @FXML private AnchorPane container;
    @FXML private AnchorPane waypointsContainer;
    @FXML private VBox waypointListVbox;
    @FXML private JFXCheckBox showNodesCheckbox;

    LinkedList<Node> currentNodes;

    public PathfindingSidebarController(MainWindowController parent, MapController map) {
        super(parent, map);

        currentNodes = new LinkedList<>();
    }

    @FXML
    void initialize() {
        // Set containers to be transparent to mouse events
        container.setPickOnBounds(false);
        waypointsContainer.setPickOnBounds(false);
        waypointListVbox.setPickOnBounds(false);
    }

    @FXML
    void showNodes(){
        boolean isS = showNodesCheckbox.isSelected();
        System.out.println(isS);
        getMapController().setShowNodes(isS);
    }

    @FXML
    void onResetPressed() {
        currentNodes.clear();
        waypointListVbox.getChildren().clear();
    }

    @FXML
    void btGeneratePathPressed() throws IOException {
        if (currentNodes.size() > 0) {
            MapEntity map = MapEntity.getInstance();
            Path path = Pathfinder.generatePath(currentNodes);
            getMapController().drawPath(path);

            waypointListVbox.getChildren().clear();

            currentNodes.clear();
        }
    }

    @FXML
    void btClearPathPressed() throws IOException {
        getMapController().clearMap();
    }

    @Override
    public javafx.scene.Node getContentView() {
        if (contentView == null) {
            contentView = loadView("/view/PathfindingSidebarView.fxml");
        }

        return contentView;
    }

    @Override
    public void onMapLocationClicked(Point2D location) {

    }

    @Override
    public void onMapNodeClicked(Node node) {
        if (!currentNodes.contains(node)) {
            currentNodes.add(node);

            Label nodeNameLabel = new Label(node.getNodeID());
            nodeNameLabel.setTextFill(Color.BLACK);
            waypointListVbox.getChildren().add(nodeNameLabel);
        }
    }

    @Override
    public void onMapEdgeClicked(Edge edge) {

    }

    @Override
    public void onMapFloorChanged(NodeFloor floor) {

    }

    @Override
    public void resetScreen() {
        onResetPressed();
        showNodesCheckbox.setSelected(false);
        getMapController().setShowNodes(false);
        getMapController().setShowEdges(false);

        getMapController().setAnchor(0, 0, 0, 0);
        getMapController().setFloorSelectorPosition(new Point2D(325, 20));
    }
}