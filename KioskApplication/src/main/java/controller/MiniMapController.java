package controller;

import database.objects.Edge;
import database.objects.Node;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import utility.Node.NodeFloor;

public class MiniMapController extends ScreenController{

    @FXML public ImageView miniMapView;
    @FXML private AnchorPane navigationPane;
    @FXML private Rectangle navigationRec;

    MiniMapController(MainWindowController parent, MapController mapController) {
        super(parent, mapController);
    }

    @FXML
    protected void initialize() {
    }

    //Todo NULL POINTER???
    void switchFloor(Image floorImage) {
        miniMapView.setImage(floorImage);
        miniMapView.setFitWidth(200);
        miniMapView.setFitHeight(150);
    }

    void setNavigationRecH(double newHValue) {
        System.out.println("New HValue: " + newHValue);
    }

    void setNavigationRecV(double newVValue) {
        System.out.println("New HValue: " + newVValue);
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
    //TODO can't use this because arguement is not a image
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
