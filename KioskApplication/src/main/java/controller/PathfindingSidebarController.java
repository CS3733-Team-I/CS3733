package controller;

import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;
import entity.Path;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import pathfinder.Pathfinder;
import utility.NodeFloor;

import java.io.IOException;
import java.util.LinkedList;

public class PathfindingSidebarController extends ScreenController {

    @FXML VBox waypointListVbox;
    @FXML private CheckBox showNodes;

    LinkedList<Node> currentNodes;

    public PathfindingSidebarController(MainWindowController parent, MapController map) {
        super(parent, map);

        currentNodes = new LinkedList<>();
    }

    public void setPathfinderalg(int pathfinderalg){

    }

    @FXML
    void showNodes(){
        boolean isS = showNodes.isSelected();
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

            TextField nodeNameTextField = new TextField(node.getNodeID());
            nodeNameTextField.setEditable(false);
            waypointListVbox.getChildren().add(nodeNameTextField);
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
        showNodes.setSelected(false);
        getMapController().setShowNodes(false);
        getMapController().setShowEdges(false);

        getMapController().setAnchor(0, 200, 0, 0);
    }
}