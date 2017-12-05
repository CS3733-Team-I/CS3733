package controller.map;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import controller.MainWindowController;
import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;
import entity.Path;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import utility.ResourceManager;
import utility.node.NodeFloor;

import java.io.IOException;
import java.util.LinkedList;

public class MapController {
    @FXML private AnchorPane container;

    private Group zoomGroup;
    @FXML private ScrollPane scrollPane;

    public static final double DEFAULT_HVALUE = 0.52;
    public static final double DEFAULT_VVALUE = 0.3;

    @FXML private StackPane stackPane;
    @FXML private ImageView mapView;
    @FXML private AnchorPane nodesEdgesContainer;
    @FXML private AnchorPane waypointPane;

    @FXML private JFXComboBox<NodeFloor> floorSelector;
    @FXML private JFXSlider zoomSlider;

    @FXML private VBox optionsBox;
    @FXML private JFXCheckBox showNodesBox;
    @FXML private JFXCheckBox showEdgesBox;

    private Path currentPath;
    private NodesEdgesView nodesEdgesView;
    private boolean editMode = false;

    private MiniMapController miniMapController;
    @FXML private AnchorPane miniMapPane;

    private LinkedList<MenuButton> waypoints;

    private MainWindowController parent = null;

    public MapController() {
        waypoints = new LinkedList<>();
    }

    /**
     * Set the parent MainWindowController for this MapController
     * @param controller parent MainWindowController
     */
    public void setParent(MainWindowController controller) {
        parent = controller;
    }

    /**
     * Tell the parent controller that a node was clicked
     * @param node the clicked node
     */
    public void nodeClicked(Node node) {
        if (!this.parent.equals(null)) {
            this.parent.onMapNodeClicked(node);
        }
    }

    /**
     * Tell the parent controller that an edge was clicked
     * @param edge the clicked edge
     */
    public void edgeClicked(Edge edge) {
        if (!this.parent.equals(null)) {
            this.parent.onMapEdgeClicked(edge);
        }
    }

    /**
     * Gets the current path
     * @return the path
     */
    public Path getPath() {
        return currentPath;
    }

    /**
     * Set the current path and draw it
     * @param path the new path
     */
    public void setPath(Path path) {
        if (path != null) {
            this.showNodesBox.setDisable(true);
            this.showEdgesBox.setDisable(true);
        }

        this.currentPath = path;
        nodesEdgesView.drawPath();
    }

    public void setNodesVisible(boolean visible) { this.showNodesBox.setSelected(visible); onNodeBoxToggled(); }
    public boolean areNodesVisible(){ return this.showNodesBox.isSelected(); }

    public void setEdgesVisible(boolean visible) { this.showEdgesBox.setSelected(visible); onEdgeBoxToggled(); }
    public boolean areEdgesVisible(){
        return this.showEdgesBox.isSelected();
    }

    public boolean isEditMode() {
        return editMode;
    }
    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public void setFloorSelector(NodeFloor floorSelector) {
        this.floorSelector.setValue(floorSelector);
        onFloorSelected();
    }

    /**
     * Gets the width of the map
     * @return the map's width
     */
    public double getWidth() {
        return container.getWidth();
    }

    /**
     * Gets the height of the map
     * @return the map's height
     */
    public double getHeight() {
        return container.getHeight();
    }

    public void setScrollbarX(double x) {
        scrollPane.setHvalue(x);
    }

    public void setScrollbarY(double y) {
        scrollPane.setVvalue(y);
    }

    public void reloadDisplay() {
        this.showNodesBox.setDisable(false);
        this.showEdgesBox.setDisable(false);

        nodesEdgesView.reloadDisplay();
    }

    /**
     * Clear the map of waypoints, nodes, and edges
     */
    public void clearMap() {
        this.waypointPane.getChildren().clear();
        this.waypoints.clear();

        this.nodesEdgesView.clear();
    }

    /**
     * Add a waypoint indicator to the map
     * @param location waypoint location
     */
    public void addWaypoint(Point2D location) {
        try {
            // put the pin and set it's info
            MenuButton wayPointObject = FXMLLoader.load(getClass().getResource("/view/WaypointView.fxml"));

            // TODO magic numbers
            wayPointObject.setTranslateX(location.getX() - 24);
            wayPointObject.setTranslateY(location.getY() - 60);

            //TODO handle waypoint option
            //wayPointObject.setOnAction(ActionEvent -> WaypointOptions());

            waypoints.add(wayPointObject);
            waypointPane.getChildren().add(wayPointObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load a new floor image and display it. Additionally re-renders the current path based on the floor being viewed
     * @param floor the floor to load
     */
    private void loadFloor(NodeFloor floor) {
        String floorImageURL = "";
        switch (floor) {
            case LOWERLEVEL_2:
                floorImageURL = "/images/00_thelowerlevel2.png";
                break;
            case LOWERLEVEL_1:
                floorImageURL = "/images/00_thelowerlevel1.png";
                break;
            case GROUND:
                floorImageURL = "/images/00_thegroundfloor.png";
                break;
            case FIRST:
                floorImageURL = "/images/01_thefirstfloor.png";
                break;
            case SECOND:
                floorImageURL = "/images/02_thesecondfloor.png";
                break;
            case THIRD:
                floorImageURL = "/images/03_thethirdfloor.png";
                break;
        }

        Image floorImage = ResourceManager.getInstance().getImage(floorImageURL);
        mapView.setImage(floorImage);
        mapView.setFitWidth(floorImage.getWidth());
        mapView.setFitHeight(floorImage.getHeight());

        nodesEdgesView.drawPath();

        miniMapController.switchFloor(floorImageURL);
    }

    /**
     * Gets the current floor the map is viewing
     * @return the current floor
     */
    public NodeFloor getCurrentFloor() {
        return floorSelector.getValue();
    }

    /**
     * Sets the position of the map relative to the sides of the application window.
     * @param top position relative to top
     * @param left position relative to left
     * @param bottom position relative to bottom
     * @param right position relative to right
     */
    public void setAnchor(double top, double left, double bottom, double right) {
        if (container != null) {
            AnchorPane.setTopAnchor(container, top);
            AnchorPane.setLeftAnchor(container, left);
            AnchorPane.setRightAnchor(container, bottom);
            AnchorPane.setBottomAnchor(container, right);
        }
    }

    /**
     * Sets the current zoom value
     * @param scaleValue zoom value
     */
    private void setZoom(double scaleValue) {
        double scrollH = scrollPane.getHvalue();
        double scrollV = scrollPane.getVvalue();

        zoomGroup.setScaleX(scaleValue);
        zoomGroup.setScaleY(scaleValue);

        scrollPane.setHvalue(scrollH);
        scrollPane.setVvalue(scrollV);

        miniMapController.setViewportZoom(scaleValue);
    }

    /**
     * Initialize the MapController. Called when the FXML file for this is loaded
     */
    @FXML
    protected void initialize() {
        waypointPane.setPickOnBounds(false);

        floorSelector.getItems().addAll(NodeFloor.values());

        miniMapController = new MiniMapController(this);

        nodesEdgesView = new NodesEdgesView(this);
        nodesEdgesView.setPickOnBounds(false);

        AnchorPane.setTopAnchor(nodesEdgesView, 0.0);
        AnchorPane.setLeftAnchor(nodesEdgesView, 0.0);
        AnchorPane.setBottomAnchor(nodesEdgesView, 0.0);
        AnchorPane.setRightAnchor(nodesEdgesView, 0.0);

        nodesEdgesContainer.getChildren().add(nodesEdgesView);
        nodesEdgesContainer.setMouseTransparent(true);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MiniMapView.fxml"));
            loader.setController(miniMapController);
            miniMapPane.getChildren().clear();
            miniMapPane.getChildren().add(loader.load());
        } catch (IOException e) {
            System.err.println("Loading MiniMapView failed.");
        }

        zoomSlider.valueProperty().addListener((o, oldVal, newVal) -> setZoom((Double) newVal));

        // Wrap scroll content in a Group so ScrollPane re-computes scroll bars
        Group contentGroup = new Group();
        zoomGroup = new Group();
        contentGroup.getChildren().add(zoomGroup);
        zoomGroup.getChildren().add(scrollPane.getContent());
        scrollPane.setContent(contentGroup);

        // Update MiniMap on scroll
        scrollPane.hvalueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double value = newValue.doubleValue() / scrollPane.getHmax();
                if (Double.isNaN(value)) value = 0.0;

                miniMapController.setNavigationRecX(value);
            }
        });

        scrollPane.vvalueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double value = newValue.doubleValue() / scrollPane.getVmax();
                if (Double.isNaN(value)) value = 0.0;

                miniMapController.setNavigationRecY(value);
            }
        });

        // Update MiniMap size when the container gets larger/smaller
        container.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                miniMapController.setViewportWidth(Double.isNaN(newValue.doubleValue()) ? 0 : newValue.doubleValue());

                calculateMinZoom();
            }
        });

        container.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                miniMapController.setViewportHeight(Double.isNaN(newValue.doubleValue()) ? 0 : newValue.doubleValue());

                calculateMinZoom();
            }
        });
    }

    /**
     * Calculates the minimum zoom value for the scroll bar
     */
    private void calculateMinZoom() {
        double widthRatio = container.getWidth() / mapView.getFitWidth();
        double heightRatio = container.getHeight() / mapView.getFitHeight();
        double minScrollValue = Math.max(widthRatio, heightRatio);

        zoomSlider.setMin(minScrollValue);

        if (zoomSlider.getValue() < minScrollValue) zoomSlider.setValue(minScrollValue);
    }

    /**
     * Changes the floor when the floor selector ComboBox is changed.
     */
    @FXML
    protected void onFloorSelected() {
        NodeFloor floor = floorSelector.getSelectionModel().getSelectedItem();

        // Load the image and reload our display based on the new floor
        loadFloor(floor);
        nodesEdgesView.reloadDisplay();

        // Notify parent
        parent.onMapFloorChanged(floor);
    }

    @FXML
    protected void onMapClicked(MouseEvent event) throws IOException {
        if (parent != null) {
            // Check if clicked location is a node
            LinkedList<Node> floorNodes = MapEntity.getInstance().getNodesOnFloor(floorSelector.getValue());
            for (Node node : floorNodes) {
                Rectangle2D nodeArea = new Rectangle2D(node.getXcoord() - 15, node.getYcoord() - 15,
                        30, 30); // TODO magic numbers
                Point2D clickPosition = new Point2D(event.getX(), event.getY());

                if (nodeArea.contains(clickPosition)) {
                    parent.onMapNodeClicked(node);
                    return;
                }
            }
            // Otherwise return the x,y coordinates
            parent.onMapLocationClicked(event, new Point2D(event.getX(), event.getY()));
        }
    }

    @FXML
    protected void onNodeBoxToggled() {
        nodesEdgesView.setShowNodes(showNodesBox.isSelected());
    }

    @FXML
    protected void onEdgeBoxToggled() {
        nodesEdgesView.setShowEdges(showEdgesBox.isSelected());
    }

    @FXML
    protected void zoomInPressed() {
        //System.out.println("Zoom in clicked");
        double sliderVal = zoomSlider.getValue();
        zoomSlider.setValue(sliderVal + 0.1);
    }

    @FXML
    protected void zoomOutPressed() {
        //System.out.println("Zoom out clicked");
        double sliderVal = zoomSlider.getValue();
        zoomSlider.setValue(sliderVal - 0.1);
    }

    @FXML
    protected void recenterPressed() {
        this.scrollPane.setHvalue(DEFAULT_HVALUE);
        this.scrollPane.setVvalue(DEFAULT_VVALUE);
    }

    public void setOptionsBoxVisible(boolean visible) {
        this.optionsBox.setVisible(visible);
    }
}