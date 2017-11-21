package controller;

import com.jfoenix.controls.JFXCheckBox;
import database.objects.Edge;
import database.objects.Node;
import entity.AlgorithmSetting;
import entity.MapEntity;
import entity.Path;
import pathfinder.A_star;
import pathfinder.Pathfinder;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import pathfinder.Pathfinder;
import pathfinder.PathfinderException;
import utility.Node.NodeFloor;

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

    public void setPathfinderalg(int pathfinderalg){

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
            Pathfinder pathfinder = new Pathfinder(AlgorithmSetting.getInstance().getAlgorithm());
            try{
                Path path = pathfinder.generatePath(currentNodes);
                getMapController().drawPath(path);
            }
            catch(PathfinderException exception){
                System.out.println(exception.getMessage()); //TODO: print to UI instead of console
            }


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
    public void onScreenChanged() {
        getMapController().setFloorSelectorPosition(new Point2D(10, 10));
    }

    @Override
    public void resetScreen() {
        onResetPressed();

        getMapController().setShowNodes(false);
        getMapController().setShowEdges(false);

        getMapController().setAnchor(0, 0, 0, 0);
        getMapController().setFloorSelectorPosition(new Point2D(325, 20));
    }
}