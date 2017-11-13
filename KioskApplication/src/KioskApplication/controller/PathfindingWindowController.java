package KioskApplication.controller;

import KioskApplication.entity.Path;
import javafx.beans.DefaultProperty;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class PathfindingWindowController extends MapWindowController {

    public PathfindingWindowController() throws IOException {
        super();
        getMapController().setParent(this);

        FXMLLoader loader = new FXMLLoader(MapWindowController.class.getResource("/KioskApplication/view/PathfindingSidebarView.fxml"));
        loader.setRoot(getSidebarPane());
        loader.setController(new PathfindingSidebarController(this));
        loader.load();
    }

    void mapLocationClicked(double x, double y) {
        System.out.println(String.format("pathfinder Map Clicked: %f %f\n", x, y));
    }

    void displayPathOnMap(Path path) throws IOException {
        getMapController().drawPath(path);
    }

    void mapNodeClicked() {}
}
