package controller;

import database.objects.Edge;
import database.objects.Node;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import utility.node.NodeFloor;

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
    //TODO MINIMAP SHOULD SHOW PATH AND WAYPOINTS
    @FXML
    protected void initialize() {
        //set navigation rectangle's initial position
        navigationRec.setWidth(mapController.container.getWidth() * RAWRatio);
        navigationRec.setHeight(mapController.container.getHeight() * RAHRatio);

        recXOffset = (miniMapView.getFitWidth() - navigationRec.getWidth())/(miniMapView.getFitWidth());
        recYOffset = (miniMapView.getFitHeight() - navigationRec.getHeight())/(miniMapView.getFitHeight());

        navigationRec.setX((DEFAULT_HVALUE * miniMapView.getFitWidth())*recXOffset);
        navigationRec.setY((DEFAULT_VVALUE * miniMapView.getFitHeight())*recYOffset);

        /**
         * sync navigation rectangle's position with viewable region(scroll pane)
         */
        navigationRec.yProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                mapController.scrollPane.setVvalue((double)newValue * mapController.scrollPane.getVmax()/(miniMapView.getFitHeight()*recYOffset));
            }
        });
        navigationRec.xProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                mapController.scrollPane.setHvalue((double)newValue * mapController.scrollPane.getHmax()/(miniMapView.getFitWidth()*recXOffset));
            }
        });

        //DragResizeMod.makeResizable(navigationRec);
        /**
         * Handles drag event on navigation rectangle
         */
        //TODO DRAG NO intermediate MOTION???
        navigationRec.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Dragboard db = navigationRec.startDragAndDrop(TransferMode.MOVE);
                event.consume();
            }
        });
        navigationRec.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.MOVE);
                event.consume();
            }
        });
        navigationRec.setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                navigationRec.setFill(Color.BLUE);
                event.consume();
            }
        });
        navigationRec.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                navigationRec.setFill(Color.TRANSPARENT);
                event.consume();
            }
        });
        navigationRec.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                event.setDropCompleted(true);
                event.consume();
            }
        });
        navigationRec.setOnDragDone(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.consume();
            }
        });
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
    //TODO THIS NEEDS A MULTIPLIER TO MAKE IT MORE ACCURATE
    void NavigationRecZoom(double scaleValue) {
//        System.out.println("ScaleValue: " + scaleValue);
        navigationRec.setScaleX(1/scaleValue);
        navigationRec.setScaleY(1/scaleValue);
//        navigationRec.setWidth(navigationRec.getWidth()/scaleValue);
//        navigationRec.setHeight(navigationRec.getHeight()/scaleValue);
    }
    @FXML
    protected void onMouseClicked(MouseEvent event) {
        navigationRec.setX(event.getX()-navigationRec.getWidth()/2);
        navigationRec.setY(event.getY()-navigationRec.getHeight()/2);
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
