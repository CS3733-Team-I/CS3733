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

import static controller.MapController.DEFAULT_HVALUE;
import static controller.MapController.DEFAULT_VVALUE;

public class MiniMapController extends ScreenController{

    @FXML public ImageView miniMapView;
    @FXML private AnchorPane navigationPane;
    @FXML private Rectangle navigationRec;

    private double imageWidth;
    private double imageHeight;
    private double RAHRatio; //navigation and main map's anchorpane
    private double RAWRatio; //navigation and main map's anchorpane
    private double recXOffset; //navigation X scrollable region offset
    private double recYOffset; //navigation Y scrollable region offset

    MiniMapController(MainWindowController parent, MapController mapController) {
        super(parent, mapController);
        //set default value
        //default value
        imageWidth = 5000;
        imageHeight = 3400;

        RAWRatio = 200/imageWidth;
        RAHRatio = 136/imageHeight;

        recXOffset = (200 - 39.2)/(200);
        recYOffset = (136 - 39.2)/(136);
    }

    @FXML
    protected void initialize() {
        //set navigation rectangle's initial position
        navigationRec.setWidth(mapController.container.getWidth() * RAWRatio);
        navigationRec.setHeight(mapController.container.getHeight() * RAHRatio);
//        System.out.println("Xoffset: " + recXOffset);
//        System.out.println("Yoffset: " + recYOffset);
        recXOffset = (miniMapView.getFitWidth() - navigationRec.getWidth())/(miniMapView.getFitWidth());
        recYOffset = (miniMapView.getFitHeight() - navigationRec.getHeight())/(miniMapView.getFitHeight());
//        System.out.println("Xoffset: " + recXOffset);
//        System.out.println("Yoffset: " + recYOffset);
        navigationRec.setX((DEFAULT_HVALUE * miniMapView.getFitWidth())*recXOffset);
        navigationRec.setY((DEFAULT_VVALUE * miniMapView.getFitHeight())*recYOffset);
    }

    void switchFloor(Image floorImage) {
        miniMapView.setImage(floorImage);

        imageWidth = floorImage.getWidth();
        imageHeight = floorImage.getHeight();

        RAHRatio = miniMapView.getFitHeight()/imageHeight;
        RAWRatio = miniMapView.getFitWidth()/imageWidth;
    }
    /**
     * Set Navigation Rectangle's position
     * Based on viewable region (scrollpane) coordinates
     */
    void setNavigationRecX(double newHValue) {

        navigationRec.setX((newHValue * miniMapView.getFitWidth())*recXOffset);
//        System.out.println("Xoffset: " + recXOffset);
//        System.out.println("New X: " + (newHValue * miniMapView.getFitWidth())*recXOffset);
    }

    void setNavigationRecY(double newVValue) {

        navigationRec.setY((newVValue * miniMapView.getFitHeight())*recYOffset);
//        System.out.println("Yoffset: " + recYOffset);
//        System.out.println("New Y: " + (newVValue * miniMapView.getFitHeight())*recYOffset);
    }
    /**
     * Set Navigation Rectangle's size according to window resize event
     * Width and Height according to the ratio of map image size and viewable region (anchorpane)
     */
    void setNavigationRecWidth(double newWidthValue) {
        //For reference, do not remove comments
//        System.out.println("New Width "+ newWidthValue * RAWRatio);
//        System.out.println("W"+newWValue);
//        System.out.println("Hratio"+RAHRatio);
        navigationRec.setWidth(newWidthValue * RAWRatio);
        recXOffset = (miniMapView.getFitWidth() - navigationRec.getWidth())/(miniMapView.getFitWidth());
    }

    void setNavigationRecHeight(double newHeightValue) {
        //For reference, do not remove comments
//        System.out.println("New Height "+ newHeightValue * RAHRatio);
//        System.out.println("Anchor height"+newWValue);
//        System.out.println("Wratio"+RAWRatio);
        navigationRec.setHeight(newHeightValue * RAHRatio);
        recYOffset = (miniMapView.getFitHeight() - navigationRec.getHeight())/(miniMapView.getFitHeight());
    }

    /**
     * Set Navigation Rectangle's size according to zoom event
     * Width and Height according to the ratio of map image size and viewable region (zoomgroup)
     */
    void NavigationRecZoom(double scaleValue) {
        navigationRec.setWidth(navigationRec.getWidth()/scaleValue);
        navigationRec.setHeight(navigationRec.getHeight()/scaleValue);
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
