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
    @FXML private Rectangle viewportRect;

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
        viewportRect.setWidth(mapController.container.getWidth() * RAWRatio);
        viewportRect.setHeight(mapController.container.getHeight() * RAHRatio);

        recXOffset = (miniMapView.getFitWidth() - viewportRect.getWidth())/(miniMapView.getFitWidth());
        recYOffset = (miniMapView.getFitHeight() - viewportRect.getHeight())/(miniMapView.getFitHeight());

        viewportRect.setX((DEFAULT_HVALUE * miniMapView.getFitWidth())*recXOffset);
        viewportRect.setY((DEFAULT_VVALUE * miniMapView.getFitHeight())*recYOffset);

        // TODO this is bad oo, we should expose a way to add a listener not directly access scrollPane
        // sync navigation rectangle's position with viewable region(scroll pane)
        viewportRect.yProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                mapController.scrollPane.setVvalue((double)newValue * mapController.scrollPane.getVmax()/(miniMapView.getFitHeight()*recYOffset));
            }
        });

        viewportRect.xProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                mapController.scrollPane.setHvalue((double)newValue * mapController.scrollPane.getHmax()/(miniMapView.getFitWidth()*recXOffset));
            }
        });
    }

    /**
     * Set the current floor by passing an image
     * @param floorImage the image of the new floor
     */
    void switchFloor(Image floorImage) {
        miniMapView.setImage(floorImage);

        imageWidth = floorImage.getWidth();
        imageHeight = floorImage.getHeight();

        RAHRatio = miniMapView.getFitHeight()/imageHeight;
        RAWRatio = miniMapView.getFitWidth()/imageWidth;
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
        viewportRect.setX(event.getX()- viewportRect.getWidth()/2);
        viewportRect.setY(event.getY()- viewportRect.getHeight()/2);
    }

    /**
     * Clamps a value (val) between the min and max
     * @param val value to clamp
     * @param min minimum value
     * @param max maximum value
     * @return the larger value between min and the smallest between max and val.
     */
    public double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }
}
