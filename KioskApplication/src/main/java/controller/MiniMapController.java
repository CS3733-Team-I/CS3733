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

    private double imageWidth;
    private double imageHeight;
    private double RAHRatio; //navigation and main map's anchorpane
    private double RAWRatio; //navigation and main map's anchorpane

    MiniMapController(MainWindowController parent, MapController mapController) {
        super(parent, mapController);
        //set default value
        //default value
        imageWidth = 5000;
        imageHeight = 3400;

        RAWRatio = 200/imageWidth;
        RAHRatio = 136/imageHeight;
    }

    @FXML
    protected void initialize() {
    }

    //Todo NULL POINTER???
    void switchFloor(Image floorImage) {
        miniMapView.setImage(floorImage);

        imageWidth = floorImage.getWidth();
        imageHeight = floorImage.getHeight();

        RAHRatio = miniMapView.getFitHeight()/imageHeight;
        RAWRatio = miniMapView.getFitWidth()/imageWidth;
    }
    /**
     * Set Navigation Rectangle's position
     *
     */
    void setNavigationRecH(double newHValue) {
        System.out.println("New HValue: " + newHValue);
    }

    void setNavigationRecV(double newVValue) {
        System.out.println("New HValue: " + newVValue);
    }
    /**
     * Set Navigation Rectangle's size
     * Width and Height according to the ratio of map image size and viewable region (anchorpane)
     */
    void setNavigationRecWidth(double newWidthValue) {
//        System.out.println("New height "+ newWValue * RAHRatio);
//        System.out.println("W"+newWValue);
//        System.out.println("Hratio"+RAHRatio);
        navigationRec.setWidth(newWidthValue * RAWRatio);
    }

    void setNavigationRecHeight(double newHeightValue) {
//        System.out.println("New Width "+ newWValue * RAWRatio);
//        System.out.println("Anchor height"+newWValue);
//        System.out.println("Wratio"+RAWRatio);
        navigationRec.setHeight(newHeightValue * RAHRatio);

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
