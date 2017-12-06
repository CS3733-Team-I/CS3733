package controller.map;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import controller.MainWindowController;
import database.connection.NotFoundException;
import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;
import entity.Path;
import entity.SystemSettings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import utility.ApplicationScreen;
import utility.ResourceManager;
import utility.node.NodeFloor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MapController {
    @FXML private AnchorPane container;

    private Group zoomGroup;
    @FXML private ScrollPane scrollPane;
    private boolean mouseZoom;

    public static final double DEFAULT_HVALUE = 0.52;
    public static final double DEFAULT_VVALUE = 0.3;
    public static final double DEFAULT_ZOOM = 0.75;

    @FXML private StackPane stackPane;
    @FXML private ImageView mapView;
    @FXML private AnchorPane nodesEdgesContainer;
    @FXML private AnchorPane pathWaypointContainer;

    @FXML private JFXComboBox<NodeFloor> floorSelector;
    @FXML private JFXSlider zoomSlider;
    @FXML private JFXButton recenterButton;

    @FXML private VBox optionsBox;
    @FXML private JFXCheckBox showNodesBox;
    @FXML private JFXCheckBox showEdgesBox;
    @FXML private JFXButton aboutButton;

    @FXML private ObservableList<javafx.scene.Node> visibleWaypoints;

    private NodesEdgesView nodesEdgesView;
    private boolean editMode = false;

    private PathWaypointView pathWaypointView;

    private MiniMapController miniMapController;
    @FXML private AnchorPane miniMapPane;

    private MainWindowController parent = null;

    public MapController() { visibleWaypoints = FXCollections.<javafx.scene.Node>observableArrayList(); }

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
        return this.pathWaypointView.getPath();
    }

    /**
     * Set the current path and draw it
     * @param path the new path
     */
    public void setPath(Path path) {
        if (path != null) {
            this.showNodesBox.setDisable(true);
            this.showEdgesBox.setDisable(true);
            setFloorSelector(pathWaypointView.getStartWaypoint().getFloor());
            pathWaypointView.drawPath(path);
        }
    }

    /**
     * Clear the path drawn
     */
    public void clearPath() {
        this.pathWaypointView.clearPath();
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
        pathWaypointView.reloadDisplay();

        recenterButton.setText(SystemSettings.getInstance().getResourceBundle().getString("my.recenter"));
        //hackey way to reset the comobobox
        int floor = floorSelector.getValue().ordinal();
        floorSelector.getItems().removeAll();
        floorSelector.setValue(NodeFloor.values()[floor]);
    }

    /**
     * Clear the map of waypoints, nodes, and edges
     */
    public void clearMap() {
        this.pathWaypointView.clearAll();
        clearPath();
        this.nodesEdgesView.clear();
    }

    /**
     * Add a waypoint indicator to the map
     * @param location waypoint location
     */
    public void addWaypoint(Point2D location, Node node) {
        this.pathWaypointView.addWaypoint(node);
    }

    public void removeWaypoint(Node node) {
        this.pathWaypointView.removeWaypoint(node);
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

        pathWaypointView.reloadDisplay();

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
     * Focal point is dependent on mouseZoom class-wide boolean
     * if true: it will only change the scale of the map, letting the scrollEvent listener handle the repositioning
     * if false: it will zoom in/out on the center of the screen
     * @param scaleValue zoom value
     */
    private void setZoom(double scaleValue) {
        if(!mouseZoom) {
            Bounds viewPort = scrollPane.getViewportBounds();
            zoomOnFocalPoint(scaleValue, viewPort.getWidth() / 2, viewPort.getHeight() / 2);
        }
    }

    /**
     * handles all zooming operations
     * @param scaleValue
     * @param focalX the x coordinate of the point to zoom on in container
     * @param focalY the y coordinate of the point to zoom on in container
     * @return scaleValue a double that can be modified by the operation
     */
    private double zoomOnFocalPoint(double scaleValue, double focalX, double focalY){
        double widthRatio = container.getWidth() / mapView.getFitWidth();
        double heightRatio = container.getHeight() / mapView.getFitHeight();
        double minScrollValue = Math.max(widthRatio, heightRatio);
        double maxScrollValue = zoomSlider.getMax();

        //bounds the scaleValue within the min and max zoom values
        scaleValue=Math.min(scaleValue,maxScrollValue);
        scaleValue=Math.max(scaleValue,minScrollValue);

        double scaleFactor = scaleValue/zoomGroup.getScaleX();

        if(scaleFactor!=1) {
            // got code from Fabian at https://stackoverflow.com/questions/39529840/javafx-setfitheight-setfitwidth-for-an-image-used-within-a-scrollpane-disabl
            Bounds viewPort = scrollPane.getViewportBounds();
            Bounds contentSize = zoomGroup.getBoundsInParent();

            double focalPosX = (contentSize.getWidth() - viewPort.getWidth()) * scrollPane.getHvalue() + focalX;
            double focalPosY = (contentSize.getHeight() - viewPort.getHeight()) * scrollPane.getVvalue() + focalY;

            double scaledFocusX = focalPosX * scaleFactor;
            double scaledFocusY = focalPosY * scaleFactor;

            zoomGroup.setScaleX(scaleValue);
            zoomGroup.setScaleY(scaleValue);

            scrollPane.setHvalue((scaledFocusX - focalX) / (contentSize.getWidth() * scaleFactor - viewPort.getWidth()));
            scrollPane.setVvalue((scaledFocusY - focalY) / (contentSize.getHeight() * scaleFactor - viewPort.getHeight()));

            miniMapController.setViewportZoom(scaleValue);
        }
        return scaleValue;
    }

    /**
     * Initialize the MapController. Called when the FXML file for this is loaded
     */
    @FXML
    protected void initialize() throws NotFoundException{
        floorSelector.getItems().addAll(NodeFloor.values());
        aboutButton.setVisible(true);

        miniMapController = new MiniMapController(this);

        //initialize paths and waypoints view
        pathWaypointView = new PathWaypointView(this);
        pathWaypointView.setPickOnBounds(false);
        //initialize nodes and egdes view
        nodesEdgesView = new NodesEdgesView(this);
        nodesEdgesView.setPickOnBounds(false);

        recenterButton.setText(SystemSettings.getInstance().getResourceBundle().getString("my.recenter"));
        AnchorPane.setTopAnchor(nodesEdgesView, 0.0);
        AnchorPane.setLeftAnchor(nodesEdgesView, 0.0);
        AnchorPane.setBottomAnchor(nodesEdgesView, 0.0);
        AnchorPane.setRightAnchor(nodesEdgesView, 0.0);

        nodesEdgesContainer.getChildren().add(nodesEdgesView);
        nodesEdgesContainer.setPickOnBounds(false);

        AnchorPane.setTopAnchor(pathWaypointView, 0.0);
        AnchorPane.setLeftAnchor(pathWaypointView, 0.0);
        AnchorPane.setBottomAnchor(pathWaypointView, 0.0);
        AnchorPane.setRightAnchor(pathWaypointView, 0.0);

        pathWaypointContainer.getChildren().add(pathWaypointView);
        pathWaypointContainer.setPickOnBounds(false);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MiniMapView.fxml"));
            loader.setController(miniMapController);
            miniMapPane.getChildren().clear();
            miniMapPane.getChildren().add(loader.load());
        } catch (IOException e) {
            System.err.println("Loading MiniMapView failed.");
        }

        // Wrap scroll content in a Group so ScrollPane re-computes scroll bars
        zoomGroup = new Group();
        zoomGroup.getChildren().add(scrollPane.getContent());
        Group contentGroup = new Group(zoomGroup);
        scrollPane.setContent(contentGroup);

        // Initializes the zoom slider to the current zoom scale
        zoomSlider.setValue(zoomGroup.getScaleX());

        // Initializes the Hvalue & Vvalue to the default values
        scrollPane.setHvalue(DEFAULT_HVALUE);
        scrollPane.setVvalue(DEFAULT_VVALUE);

        // zoomSlider value listener
        zoomSlider.valueProperty().addListener((o, oldVal, newVal) -> setZoom((Double) newVal));

        // MouseWheel zooming event handler
        scrollPane.addEventFilter(ScrollEvent.ANY, event ->  {
            event.consume();
            if(event.getDeltaY() == 0){
                return;
            }
            double scaleFactor = (event.getDeltaY()>0) ? 1.1 : 1/1.1;
            mouseZoom=true;
            zoomSlider.setValue(zoomOnFocalPoint(scaleFactor*zoomSlider.getValue(),event.getX(),event.getY()));
            mouseZoom=false;
        });



        // Update MiniMap on scroll
        scrollPane.hvalueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double value = newValue.doubleValue() / scrollPane.getHmax();
                if (Double.isNaN(value)) value = 0.0;
                //fixes a bug where the scrollPane gets stuck on the left of the screen
                if(Double.isNaN(scrollPane.getHvalue())) scrollPane.setHvalue(0.0);

                miniMapController.setNavigationRecX(value);
            }
        });

        scrollPane.vvalueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double value = newValue.doubleValue() / scrollPane.getVmax();
                if (Double.isNaN(value)) value = 0.0;
                //fixes a bug where the scrollPane gets stuck on the top of the screen
                if (Double.isNaN(scrollPane.getVvalue())) scrollPane.setVvalue(0.0);

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

        scrollPane.vvalueProperty().addListener((obs) -> {
            checkWaypointVisible(scrollPane);
//            System.out.println(visibleWaypoints);
        });
        scrollPane.hvalueProperty().addListener((obs) -> {
            checkWaypointVisible(scrollPane);
//            System.out.println(visibleWaypoints);
        });
        visibleWaypoints.addListener(new ListChangeListener<javafx.scene.Node>() {
            @Override
            public void onChanged(Change<? extends javafx.scene.Node> c) {
                while(c.next()) {
                    if(c.wasRemoved()) {
                        for(javafx.scene.Node lostSightWaypoint : c.getRemoved()) {
                            //TODO handle lose sight action
                        }
                    }
                    else if(c.wasAdded()) {
                        for(javafx.scene.Node RegainedSightWaypoint : c.getAddedSubList()) {
                            //TODO regain lose sight action
                        }
                    }
                }
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
        pathWaypointView.reloadDisplay();

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
    public void zoomInPressed() {
        mouseZoom=false;
        double sliderVal = zoomSlider.getValue();
        zoomSlider.setValue(sliderVal * 1.2);
    }

    @FXML
    public void zoomOutPressed() {
        mouseZoom=false;
        double sliderVal = zoomSlider.getValue();
        zoomSlider.setValue(sliderVal / 1.2);
    }

    /**
     * sets the mouseZoom boolean as false to focus the zoom on the center of the display
     */
    @FXML
    protected void zoomWithSlider(){
        mouseZoom=false;
    }

    @FXML
    protected void recenterPressed() {
        this.scrollPane.setHvalue(DEFAULT_HVALUE);
        this.scrollPane.setVvalue(DEFAULT_VVALUE);
    }

    public void setOptionsBoxVisible(boolean visible) {
        this.optionsBox.setVisible(visible);
    }

    /**
     * Get visible waypoints in the scrollpane
     */
    private List<javafx.scene.Node> getWaypointNodes(ScrollPane pane) {
        List<javafx.scene.Node> visibleNodes = new ArrayList<>();
        Bounds paneBounds = pane.localToScene(pane.getBoundsInParent());
        if (pane.getContent() instanceof Parent) {
            for (javafx.scene.Node n : (pathWaypointView).getChildrenUnmodifiable()) {
                Bounds nodeBounds = n.localToScene(n.getBoundsInLocal());
                //only put in if it's a waypoint
                if (paneBounds.intersects(nodeBounds)) {
                    if(n instanceof WaypointView) {
                        visibleNodes.add(n);
                    }
                }
            }
        }
        return visibleNodes;
    }


    //TODO REFACTOR ALL THESE MAKE PATHWAYPOINTVIEW A DELEGATION IN PATHFINDING SIDEBAR
    private void checkWaypointVisible(ScrollPane pane)
    {
        visibleWaypoints.setAll(getWaypointNodes(pane));
    }

    /**
     * swap the waypoints at targeted indexes
     */
    public void swapWaypoint(int index1, int index2) {
        this.pathWaypointView.swapWaypoint(index1, index2);
    }

    /**
     * return true if there's path displaying
     */
    public boolean isPathShowing() {
        return pathWaypointView.isPathShowing();
    }

    public LinkedList<String> getIndexedDirection(int i) {
        return pathWaypointView.getDirectionForWaypointIndex(i);
    }

    public PathWaypointView getPathWaypointView() {
        return pathWaypointView;
    }

    public ArrayList<Color> getsSegmentColorList() {
        return pathWaypointView.getsSegmentColorList();
    }

    @FXML
    private void onAboutAction(){
        parent.switchToScreen(ApplicationScreen.ADMIN_SETTINGS);
    }
}