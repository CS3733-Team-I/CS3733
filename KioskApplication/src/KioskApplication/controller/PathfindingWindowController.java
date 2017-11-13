package KioskApplication.controller;

import javafx.beans.DefaultProperty;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class PathfindingWindowController extends MapWindowController {

    public PathfindingWindowController() throws IOException {
        super();
        getMapController().setParent(this);

        FXMLLoader loader = new FXMLLoader(MapWindowController.class.getResource("/KioskApplication/view/PathfindingSidebarView.fxml"));
        loader.setRoot(getSidebarPane());
        loader.setController(new PathfindingSidebarController());
        loader.load();
    }

    void mapLocationClicked(double x, double y) {
        System.out.println(String.format("pathfinder Map Clicked: %f %f\n", x, y));
    }

    void mapNodeClicked() {}
}
