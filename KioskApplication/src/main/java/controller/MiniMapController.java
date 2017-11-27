package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;

import static controller.MapController.DEFAULT_HVALUE;
import static controller.MapController.DEFAULT_VVALUE;

public class MiniMapController {

    @FXML public ImageView miniMapView;
    @FXML private AnchorPane navigationPane;
    @FXML private Rectangle navigationRec;

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

        /**
         * Handles drag event on navigation rectangle
         */
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
        newHValue = clamp(newHValue, 0, 1.0);

        navigationRec.setX((newHValue * miniMapView.getFitWidth())*recXOffset);
    }

    void setNavigationRecY(double newVValue) {
        newVValue = clamp(newVValue, 0, 1.0);

        navigationRec.setY((newVValue * miniMapView.getFitHeight())*recYOffset);
    }

    public void setViewportWidth(double width) {
        viewportWidth = width;

        setNavigationRecWidth(viewportWidth / currentScale);
    }

    public void setViewportHeight(double height) {
        viewportHeight = height;

        setNavigationRecHeight(viewportHeight / currentScale);
    }

    /**
     * Set Navigation Rectangle's size according to window resize event
     * Width and Height according to the ratio of map image size and viewable region (anchorpane)
     */
    void setNavigationRecWidth(double newWidthValue) {
        newWidthValue = clamp(newWidthValue, 0, imageWidth);

        navigationRec.setWidth(newWidthValue * RAWRatio);
        recXOffset = (miniMapView.getFitWidth() - navigationRec.getWidth())/(miniMapView.getFitWidth());
    }

    void setNavigationRecHeight(double newHeightValue) {
        newHeightValue = clamp(newHeightValue, 0, imageHeight);

        navigationRec.setHeight(newHeightValue * RAHRatio);
        recYOffset = (miniMapView.getFitHeight() - navigationRec.getHeight())/(miniMapView.getFitHeight());
    }

    /**
     * Set Navigation Rectangle's size according to zoom event
     * Width and Height according to the ratio of map image size and viewable region (zoomgroup)
     */
    void NavigationRecZoom(double value) {
        currentScale = value;

        setNavigationRecWidth(viewportWidth / value);
        setNavigationRecHeight(viewportHeight / value);
    }

    @FXML
    protected void changePositionEvent(MouseEvent event) {
        navigationRec.setX(event.getX()-navigationRec.getWidth()/2);
        navigationRec.setY(event.getY()-navigationRec.getHeight()/2);
    }

    public double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }
}
