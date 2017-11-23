package controller;

import database.objects.Edge;
import database.objects.Node;
import javafx.geometry.Point2D;
import utility.Node.NodeFloor;

public class MiniMapController extends ScreenController{
    MiniMapController(MainWindowController parent, MapController mapController) {
        super(parent, mapController);
    }
    @Override
    public void onMapLocationClicked(Point2D location) {

    }
    @Override
    public void onMapNodeClicked(Node node){

    }
    @Override
    public void onMapEdgeClicked(Edge edge){

    }
    @Override
    public void onMapFloorChanged(NodeFloor floor){

    }
    @Override
    public void resetScreen(){
    }

    @Override
    public javafx.scene.Node getContentView() {
        if (contentView == null) {
            contentView = loadView("/view/MiniMapView.fxml");
        }

        return contentView;
    }
}
