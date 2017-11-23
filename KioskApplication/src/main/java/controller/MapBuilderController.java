package controller;

import database.objects.Edge;
import javafx.geometry.Point2D;
import utility.Node.NodeFloor;

public class MapBuilderController extends ScreenController {

    MapBuilderController(MainWindowController parent, MapController map) {
        super(parent, map);
    }

    @Override
    public javafx.scene.Node getContentView() {
        if (contentView == null) {
            contentView = loadView("/view/MapBuilderView.fxml");
        }

        return contentView;
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
        getMapController().setAnchor(0, 200, 0, 0);
    }
}
