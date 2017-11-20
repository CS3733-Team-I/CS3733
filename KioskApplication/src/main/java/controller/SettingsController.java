package controller;

import database.objects.Edge;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import utility.NodeFloor;

public class SettingsController extends ScreenController {

    public SettingsController(MainWindowController parent, MapController mapController) {
        super(parent, mapController);
    }

    @Override
    public Node getContentView() {
        return null; // TODO change
    }

    @Override
    public void onMapLocationClicked(Point2D location) { }

    @Override
    public void onMapNodeClicked(database.objects.Node node) { }

    @Override
    public void onMapEdgeClicked(Edge edge) { }

    @Override
    public void onMapFloorChanged(NodeFloor floor) { }

    @Override
    public void resetScreen() {
    }
}
