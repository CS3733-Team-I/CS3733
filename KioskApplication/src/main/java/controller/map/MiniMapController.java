package controller.map;

import database.connection.NotFoundException;
import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;
import entity.Path;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import utility.ResourceManager;

import java.util.HashMap;
import java.util.LinkedList;

public class MiniMapController {

    @FXML public ImageView miniMapView;
    @FXML private Rectangle viewportRect;
    @FXML private AnchorPane waypointPane;
    @FXML private AnchorPane pathPane;

    private SimpleObjectProperty<Path> path;

    private ObservableList<Node> waypoints;
    private HashMap<Node, Circle> waypointMap;

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

        waypoints = FXCollections.observableArrayList();
        waypointMap = new HashMap<>();

        path = new SimpleObjectProperty<>();
        path.set(null);

        path.addListener((obj, oldValue, newValue) -> {
            pathPane.getChildren().clear();
            if (newValue != null) {
                drawPath();
            }
        });

        //set the waypoint and path pane cannot be clicked
        waypointPane.setMouseTransparent(true);
        pathPane.setMouseTransparent(true);

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

        // sync waypoints list to ui
        waypoints.addListener((ListChangeListener<Node>) listener -> {
            while (listener.next()) {
                if (listener.wasRemoved()) {
                    for (Node waypoint : listener.getRemoved()) {
                        waypointPane.getChildren().remove(waypointMap.get(waypoint));
                        waypointMap.remove(waypoint);
                    }
                }
                if (listener.wasAdded()) {
                    for (Node waypoint : listener.getAddedSubList()) {
                        Circle waypointIndicator = new Circle(2);
                        waypointIndicator.setFill(Color.RED);

                        waypointIndicator.setCenterX(waypoint.getXcoord() * RAWRatio);
                        waypointIndicator.setCenterY(waypoint.getYcoord() * RAHRatio);

                        if (waypoint.getFloor() != mapController.getCurrentFloor()) {
                            waypointIndicator.setVisible(false);
                        }

                        waypointPane.getChildren().add(waypointIndicator);
                        waypointMap.put(waypoint, waypointIndicator);
                    }
                }
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

        RAHRatio = miniMapView.getFitHeight() / imageHeight;
        RAWRatio = miniMapView.getFitWidth() / imageWidth;

        for (Node node : waypointMap.keySet()) {
            if (node.getFloor() == mapController.getCurrentFloor()) {
                waypointMap.get(node).setVisible(true);
            } else {
                waypointMap.get(node).setVisible(false);
            }
        }

        drawPath();
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
    public void addWaypoint(Node node) {
        this.waypoints.add(node);
    }

    /**
     * Remove the miniWayPoint on the miniMap
     * @param node of th wayPoint
     */
    public void removeWaypoint(Node node) {
        this.waypoints.remove(node);
    }

    /**
     * Clear the miniWayPoints on the miniMap
     */
    public void clearWaypoints() {
        this.waypoints.clear();
    }

    /**
     * Draw a given path
     * @param path the path to draw
     */
    public void showPath(Path path) {
        this.path.set(path);
    }

    /**
     * Draw the current path on the map
     */
    public void drawPath() {
        pathPane.getChildren().clear();

        if (path.get() == null) return;

        int waypointIndex = 0;
        for (Node node : waypoints) {
            Node lastNode = node;
            for (Node thisNode : path.get().getNodesInSegment(node)) {
                // Don't draw a line between the same nodes
                if (thisNode.getUniqueID() != lastNode.getUniqueID() &&
                    thisNode.getFloor() == mapController.getCurrentFloor() &&
                    lastNode.getFloor() == mapController.getCurrentFloor()) {

                    Line line = new Line(lastNode.getXcoord() * RAWRatio, lastNode.getYcoord() * RAHRatio,
                                         thisNode.getXcoord() * RAWRatio, thisNode.getYcoord() * RAHRatio);
                    line.setStroke(path.get().getSegmentColor(waypointIndex));
                    line.setStrokeWidth(2);
                    pathPane.getChildren().add(line);
                }
                lastNode = thisNode;
            }
            waypointIndex++;
        }
    }

    /**
     * clear mini paths on the miniMap
     */
    public void clearPath() {
        this.path.set(null);
    }
}
