package KioskApplication.controller;

import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;
import KioskApplication.entity.Path;
import KioskApplication.utility.NodeFloor;
import javafx.beans.DefaultProperty;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class PathfindingWindowController extends MapWindowController {

    PathfindingSidebarController sidebarController;

    public PathfindingWindowController() throws IOException {
        super();
        getMapController().setParent(this);

        sidebarController = new PathfindingSidebarController(this);

        FXMLLoader loader = new FXMLLoader(MapWindowController.class.getResource("/KioskApplication/view/PathfindingSidebarView.fxml"));
        loader.setRoot(getSidebarPane());
        loader.setController(sidebarController);
        loader.load();
    }

    void displayPathOnMap(Path path) throws IOException {
        getMapController().drawPath(path);
    }

    public void setShowNodes(boolean toggle) {
        getMapController().setShowNodes(toggle);
    }

    void ClearPathOnMap() throws IOException {
        getMapController().clearPath();
    }

    @Override
    void mapLocationClicked(double x, double y) {
        System.out.println(String.format("pathfinder Map Clicked: %f %f\n", x, y));
    }

    @Override
    void mapNodeClicked(Node node) {
        sidebarController.addNode(node);
    }

    @Override
    void mapFloorChanged(NodeFloor floor) { }

    @Override
    void mapEdgeClicked(Edge edge) { }
}
