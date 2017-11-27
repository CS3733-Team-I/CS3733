package controller;

import com.jfoenix.controls.JFXCheckBox;
import database.objects.Edge;
import database.objects.Node;
import entity.AlgorithmSetting;
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
import utility.Node.NodeFloor;

import java.io.IOException;
import java.util.LinkedList;

public class PathfindingSidebarController extends ScreenController {

    @FXML private AnchorPane container;
    @FXML private AnchorPane waypointsContainer;
    @FXML private VBox waypointListVbox;
    @FXML private Label exceptionText;

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
        mapController.floorSelector.setValue(NodeFloor.THIRD);
        container.setPickOnBounds(false);
        waypointsContainer.setPickOnBounds(false);
        waypointListVbox.setPickOnBounds(false);
        exceptionText.setText("");
    }

    @FXML
    void onResetPressed() {
        currentNodes.clear();
        waypointListVbox.getChildren().clear();
        exceptionText.setText("");
        getMapController().clearMap();
    }

    @FXML
    void btGeneratePathPressed() throws IOException {
        exceptionText.setText("");
        if (currentNodes.size() > 0) {
            Pathfinder pathfinder = new Pathfinder(AlgorithmSetting.getInstance().getAlgorithm());
            try{
                Path path = pathfinder.generatePath(currentNodes);
                getMapController().drawPath(path);
            }
            catch(PathfinderException exception){
               // TextBuilder.create().text("This is a text sample").build();
                exceptionText.setText("ERROR!"+ exception.getMessage());
               // System.out.println(exception.getMessage()); //TODO: print to UI instead of console
            }


            waypointListVbox.getChildren().clear();

            currentNodes.clear();
        }
    }

    @FXML
    void btClearPathPressed() throws IOException {
        getMapController().clearMap();
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
    public void onMapLocationClicked(Point2D location) {

    }

    @Override
    public void onMapNodeClicked(Node node) {
        if (!currentNodes.contains(node)) {
            currentNodes.add(node);

            Label nodeNameLabel = new Label(node.getNodeID());
            nodeNameLabel.setTextFill(Color.BLACK);
            waypointListVbox.getChildren().add(nodeNameLabel);

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