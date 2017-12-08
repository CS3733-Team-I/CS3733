package controller.map;

import database.objects.Node;
import entity.MapEntity;
import entity.Path;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;
import utility.ResourceManager;

import java.util.LinkedList;

public class MiniMapController {

    @FXML public ImageView miniMapView;
    @FXML private Rectangle viewportRect;
    @FXML private AnchorPane miniWaypointPane;
    @FXML private AnchorPane miniPathPane;

    // Width and Height of the Floor Image (px)
    private double imageWidth;
    private double imageHeight;

    // Width and Height of the Map Viewport (px)
    private double viewportWidth;
    private double viewportHeight;
    private double currentScale = 1.0;

    private double RAHRatio; //navigation and main map's anchorpane
    private double RAWRatio; //navigation and main map's anchorpane
    private double recXOffset; //navigation X scrollable region offset
    private double recYOffset; //navigation Y scrollable region offset

    MapController mapController;

    MiniMapController(MapController mapController) {
        this.mapController = mapController;

        // Set default valyes
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
        viewportRect.setWidth(mapController.getWidth() * RAWRatio);
        viewportRect.setHeight(mapController.getHeight() * RAHRatio);

        recXOffset = (miniMapView.getFitWidth() - viewportRect.getWidth())/(miniMapView.getFitWidth());
        recYOffset = (miniMapView.getFitHeight() - viewportRect.getHeight())/(miniMapView.getFitHeight());

        viewportRect.setX((MapController.DEFAULT_HVALUE * miniMapView.getFitWidth())*recXOffset);
        viewportRect.setY((MapController.DEFAULT_VVALUE * miniMapView.getFitHeight())*recYOffset);

        //set the waypoint and path pane cannot be clicked
        miniWaypointPane.setMouseTransparent(true);
        miniPathPane.setMouseTransparent(true);

        // TODO this is bad oo, we should expose a way to add a listener not directly access scrollPane
        // sync navigation rectangle's position with viewable region(scroll pane)
        viewportRect.yProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                mapController.setScrollbarY((double)newValue * 1 / (miniMapView.getFitHeight() * recYOffset));
            }
        });

        viewportRect.xProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                mapController.setScrollbarX((double)newValue * 1 / (miniMapView.getFitWidth() * recXOffset));
            }
        });
    }

    /**
     * Set the current floor by passing an image
     * @param floorImagePath the image of the new floor
     */
    void switchFloor(String floorImagePath) {
        Image floorImage = ResourceManager.getInstance().getImage(floorImagePath);
        miniMapView.setImage(floorImage);

        imageWidth = floorImage.getWidth();
        imageHeight = floorImage.getHeight();

        RAHRatio = miniMapView.getFitHeight()/imageHeight;
        RAWRatio = miniMapView.getFitWidth()/imageWidth;

        //handle miniwaypoint display when switching floors
        for(javafx.scene.Node miniWawypoint : miniWaypointPane.getChildren()){
            if(!mapController.getCurrentFloor().toString().equals(miniWawypoint.getAccessibleHelp())) {
                miniWawypoint.setVisible(false);
            }
            else {
                miniWawypoint.setVisible(true);
            }
        }
    }

    /**
     * Set the viewport rectangle's x position
     * @param newHValue x position from 0.0 to 1.0
     */
    void setNavigationRecX(double newHValue) {
        newHValue = clamp(newHValue, 0, 1.0);
        double newX = (newHValue * miniMapView.getFitWidth())*recXOffset;

        newX = clamp(newX, 0, miniMapView.getFitWidth() - viewportRect.getWidth());

        viewportRect.setX(newX);
    }

    /**
     * Set the viewports rectangle's y position
     * @param newVValue y position from 0.0 to 1.0
     */
    void setNavigationRecY(double newVValue) {
        newVValue = clamp(newVValue, 0, 1.0);
        double newY = (newVValue * miniMapView.getFitHeight())*recYOffset;

        newY = clamp(newY, 0, miniMapView.getFitHeight() - viewportRect.getHeight());
        viewportRect.setY(newY);
    }

    /**
     * Set the viewport width
     * @param width width relative to the current map image
     */
    public void setViewportWidth(double width) {
        viewportWidth = width;

        setViewportRectangleWidth(viewportWidth / currentScale);
    }

    /**
     * Set the viewport height
     * @param height height relative to the current map image
     */
    public void setViewportHeight(double height) {
        viewportHeight = height;

        setViewportRectangleHeight(viewportHeight / currentScale);
    }

    /**
     * Set the viewport rectangle's width
     * @param newWidthValue width relative to the map image
     */
    void setViewportRectangleWidth(double newWidthValue) {
        newWidthValue = clamp(newWidthValue, 0, imageWidth);

        viewportRect.setWidth(newWidthValue * RAWRatio);
        recXOffset = (miniMapView.getFitWidth() - viewportRect.getWidth())/(miniMapView.getFitWidth());
    }

    /**
     * Set the viewport rectangle's height
     * @param newHeightValue height relative to the map image
     */
    void setViewportRectangleHeight(double newHeightValue) {
        newHeightValue = clamp(newHeightValue, 0, imageHeight);

        viewportRect.setHeight(newHeightValue * RAHRatio);
        recYOffset = (miniMapView.getFitHeight() - viewportRect.getHeight())/(miniMapView.getFitHeight());
    }

    /**
     * Set the current zoom scale. Resizes the current viewport rect according to the zoom scale
     * @param zoomValue zoom scale
     */
    void setViewportZoom(double zoomValue) {
        currentScale = zoomValue;

        setViewportRectangleWidth(viewportWidth / zoomValue);
        setViewportRectangleHeight(viewportHeight / zoomValue);
    }

    @FXML
    protected void changePositionEvent(MouseEvent event) {
        viewportRect.setX(event.getX() - viewportRect.getWidth()/2);
        viewportRect.setY(event.getY() - viewportRect.getHeight()/2);
    }

    /**
     * Clamps a value (val) between the min and max
     * @param val value to clamp
     * @param min minimum value
     * @param max maximum value
     * @return the larger value between min and the smallest between max and val.
     */
    private double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    /**
     * Show the miniWayPoint on the miniMap
     * @param node of th wayPoint
     */
    public void showMiniWayPoint(Node node) {
        Circle miniMapWaypoint = new Circle(3);
        miniMapWaypoint.setAccessibleText(node.getNodeID());
        miniMapWaypoint.setAccessibleHelp(node.getFloor().toString());
        miniMapWaypoint.setFill(Color.RED);

        miniMapWaypoint.setCenterX(node.getXcoord()*RAWRatio);
        miniMapWaypoint.setCenterY(node.getYcoord()*RAHRatio);

        TranslateTransition miniWaypointPutTransition = new TranslateTransition();
        miniWaypointPutTransition.setDuration(Duration.millis(400));
        miniWaypointPutTransition.setNode(miniMapWaypoint);
        miniWaypointPutTransition.setFromY((miniMapWaypoint.getCenterY()-10)*RAHRatio);
        miniWaypointPutTransition.setToY(miniMapWaypoint.getCenterY()*RAHRatio);

        miniWaypointPane.getChildren().add(miniMapWaypoint);


        miniWaypointPutTransition.play();
    }

    /**
     * Remove the miniWayPoint on the miniMap
     * @param node of th wayPoint
     */
    public void removeMiniWayPoint(Node node) {
        for(javafx.scene.Node removedMiniWaypoint : miniWaypointPane.getChildren()) {
            if(removedMiniWaypoint.getAccessibleText().equals(node.getNodeID())) {
                FadeTransition miniWaypointTransition = new FadeTransition();
                miniWaypointTransition.setNode(removedMiniWaypoint);
                miniWaypointTransition.setDuration(Duration.millis(300));
                miniWaypointTransition.setFromValue(1.0);
                miniWaypointTransition.setFromValue(0.0);
                miniWaypointTransition.setAutoReverse(false);
                miniWaypointTransition.play();
                miniWaypointPane.getChildren().remove(removedMiniWaypoint);
            }
        }
    }

    /**
     * Clear the miniWayPoints on the miniMap
     */
    public void clearMiniWaypoint() {
        for(javafx.scene.Node removedMiniWaypoint : miniWaypointPane.getChildren()) {
            FadeTransition miniWaypointTransition = new FadeTransition();
            miniWaypointTransition.setNode(removedMiniWaypoint);
            miniWaypointTransition.setDuration(Duration.millis(300));
            miniWaypointTransition.setFromValue(1.0);
            miniWaypointTransition.setFromValue(0.0);
            miniWaypointTransition.setAutoReverse(false);
            miniWaypointTransition.play();
        }
        miniWaypointPane.getChildren().clear();
    }

    /**
     * draw the mini path on the miniMap
     * @param path to draw
     */
    public void showPath(javafx.scene.shape.Path path) {
        javafx.scene.shape.Path miniJFXPath = new javafx.scene.shape.Path();
        for(PathElement element : path.getElements()) {
            if(element instanceof MoveTo) {
                MoveTo moveTo = new MoveTo();
                moveTo.setX(((MoveTo) element).getX()*RAWRatio);
                moveTo.setY(((MoveTo) element).getY()*RAHRatio);
                miniJFXPath.getElements().add(moveTo);
            }
            else if(element instanceof LineTo) {
                LineTo lineTo = new LineTo();
                lineTo.setX(((LineTo) element).getX()*RAWRatio);
                lineTo.setY(((LineTo) element).getY()*RAHRatio);
                miniJFXPath.getElements().add(lineTo);
            }
        }
        miniJFXPath.setFill(Color.TRANSPARENT);
        miniJFXPath.setStroke(Color.BLUE);
        miniJFXPath.setStrokeWidth(2);
        miniPathPane.getChildren().add(miniJFXPath);
    }

    /**
     * clear mini paths on the miniMap
     */
    public void clearPath() {
        miniPathPane.getChildren().clear();
    }
}
