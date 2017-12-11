package controller.map;

import com.jfoenix.controls.*;
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
import utility.node.NodeSelectionType;

import java.io.IOException;
import java.util.*;

public class MapController {
    @FXML private AnchorPane container;

    private Group zoomGroup;
    @FXML private ScrollPane scrollPane;

    public static final double DEFAULT_HVALUE = 0.52;
    public static final double DEFAULT_VVALUE = 0.3;
    public static final double DEFAULT_ZOOM = 0.75;

    @FXML private StackPane stackPane;
    @FXML private ImageView mapView;
    @FXML private AnchorPane nodesEdgesContainer;
    @FXML private AnchorPane pathWaypointContainer;

    @FXML private JFXComboBox<String> languageSelector;
    @FXML private JFXComboBox<NodeFloor> floorSelector;
    @FXML private JFXSlider zoomSlider;
    @FXML private JFXButton recenterButton;
    private boolean ignoreZoomSliderListener;

    @FXML private VBox optionsBox;
    @FXML private JFXCheckBox showNodesBox;
    @FXML private JFXCheckBox showEdgesBox;
    @FXML private JFXButton aboutButton;

    @FXML private ObservableList<javafx.scene.Node> visibleWaypoints;

    @FXML private JFXButton keyButton;
    @FXML private JFXDialog keyDialog;
    @FXML private JFXDialogLayout keyDialogContainer;

    private Path currentPath;
    private NodesEdgesView nodesEdgesView;
    private boolean editMode = false;

    private PathWaypointView pathWaypointView;

    private MiniMapController miniMapController;
    @FXML private AnchorPane miniMapPane;
    private SystemSettings systemSettings;

    private MainWindowController parent = null;

    public MapController() {
        visibleWaypoints = FXCollections.<javafx.scene.Node>observableArrayList();
        systemSettings = SystemSettings.getInstance();
    }

    /**
     * Initialize the MapController. Called when the FXML file for this is loaded
     */
    @FXML
    protected void initialize() throws NotFoundException{
        floorSelector.getItems().addAll(NodeFloor.values());
        aboutButton.setVisible(true);
        languageSelector.getItems().addAll("English","French");

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

        keyDialog.setDialogContainer(keyDialogContainer);
        keyDialogContainer.setDisable(true);

        // Controller-wide localization observer
        systemSettings.addObserver((o, arg) -> {
            ResourceBundle rB = systemSettings.getResourceBundle();
            recenterButton.setText(rB.getString("my.recenter"));
        });

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
        //zoomSlider.setValue(zoomGroup.getScaleX());

        // zoomSlider value listener
        zoomSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double widthRatio = container.getWidth() / mapView.getFitWidth();
                double heightRatio = container.getHeight() / mapView.getFitHeight();
                double minScrollValue = Math.max(widthRatio, heightRatio);
                zoomSlider.setMin(minScrollValue);
                if(!ignoreZoomSliderListener){
                    Bounds viewPort = scrollPane.getViewportBounds();
                    zoomOnFocalPoint(newValue.doubleValue(),viewPort.getWidth()/2,viewPort.getHeight()/2);
                }
                else {
                }
            }
        });

        // MouseWheel zooming event handler
        scrollPane.addEventFilter(ScrollEvent.ANY, event ->  {
            event.consume();
            if(event.getDeltaY() == 0){
                return;
            }
            double scaleFactor = (event.getDeltaY()>0) ? 1.1 : 1/1.1;
            ignoreZoomSliderListener=true;
            double scaleValue= scaleFactor*zoomSlider.getValue();
            zoomSlider.setValue(scaleValue);
            zoomOnFocalPoint(scaleValue,event.getX(),event.getY());
            ignoreZoomSliderListener=false;
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
     * Add a node to the map
     * @param node the node to add
     * @param type what selection state it is
     */
    public void addNode(Node node, NodeSelectionType type) {
        nodesEdgesView.drawNodesOnMap(Arrays.asList(node));
        setNodeSelected(node, type);
    }

    /**
     * Remove a node from the map
     * @param node the node to remove
     */
    public void removeNode(Node node) {
        nodesEdgesView.removeNode(node);
    }

    /**
     * Tell the NodesEdgesView to highlight a node
     * @param node the Node to highlight
     * @param type the type to set the selection to
     */
    public void setNodeSelected(Node node, NodeSelectionType type) {
        nodesEdgesView.setNodeSelected(node, type);
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
        recenterButton.setText(SystemSettings.getInstance().getResourceBundle().getString("my.recenter"));
        pathWaypointView.reloadDisplay();

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
     * handles all zooming operations
     * @param scaleValue
     * @param focalX the x coordinate of the point to zoom on in container
     * @param focalY the y coordinate of the point to zoom on in container
     * @return scaleValue a double that can be modified by the operation
     */
    private double zoomOnFocalPoint(double scaleValue, double focalX, double focalY){
        double oldScale, scaleFactor;
        Bounds viewPort = scrollPane.getViewportBounds();
        Bounds contentSize = zoomGroup.getBoundsInParent();

        double focalPosX = (contentSize.getWidth() - viewPort.getWidth()) * scrollPane.getHvalue() + focalX;
        double focalPosY = (contentSize.getHeight() - viewPort.getHeight()) * scrollPane.getVvalue() + focalY;
        oldScale = zoomGroup.getScaleX();
        scaleFactor = scaleValue / oldScale;

        if(setZoom(scaleValue)) {
            // got code from Fabian at https://stackoverflow.com/questions/39529840/javafx-setfitheight-setfitwidth-for-an-image-used-within-a-scrollpane-disabl

            double scaledFocusX = focalPosX * scaleFactor;
            double scaledFocusY = focalPosY * scaleFactor;

            scrollPane.setHvalue((scaledFocusX - focalX) / (contentSize.getWidth() * scaleFactor - viewPort.getWidth()));
            scrollPane.setVvalue((scaledFocusY - focalY) / (contentSize.getHeight() * scaleFactor - viewPort.getHeight()));

            miniMapController.setViewportZoom(scaleValue);
        }
        return scaleValue;
    }

    /**
     * Moves the map view and adjusts the zoom factor to contain the selected nodes
     * @param viewedNodes sets the floor to the most common floor value
     */
    public void zoomOnSelectedNodes(LinkedList<Node> viewedNodes){
        //TODO: make the floor selection method more solid
        double minX=mapView.getImage().getWidth();
        double minY=mapView.getImage().getHeight();
        double maxX=0.0;
        double maxY=0.0;
        // Runs through all inserted nodes and gets the max bounds
        for(Node node: viewedNodes){
            if(node.getXcoord()>maxX){
                maxX=node.getXcoord();
            }
            if(node.getXcoord()<minX){
                minX=node.getXcoord();
            }
            if(node.getYcoord()>maxY){
                maxY=node.getYcoord();
            }
            if(node.getYcoord()<minY){
                minY=node.getYcoord();
            }
        }
        // adds a border of sorts to the viewed area
        double border=Math.max((maxX-minX)/10,(maxY-minY)/10);
        minX-=border;
        minY-=border;
        maxX+=border;
        maxY+=border;
        minX=Math.max(minX,0.0);
        minY=Math.max(minY,0.0);
        maxX=Math.min(maxX,mapView.getImage().getWidth());
        maxY=Math.min(maxY,mapView.getImage().getHeight());

        //zooming phase
        Bounds viewPort = scrollPane.getViewportBounds();
        double scaleValue = Math.min(viewPort.getWidth()/(maxX-minX), viewPort.getHeight()/(maxY-minY));
        setZoom(scaleValue);
        ignoreZoomSliderListener=true;
        zoomSlider.setValue(scaleValue);
        ignoreZoomSliderListener=false;

        //scroll bar adjusting stage
        double centerXOnImage=(minX+maxX)/2.0;
        double centerYOnImage=(minY+maxY)/2.0;

        Bounds contentSize = zoomGroup.getBoundsInParent();
        double centerXOnZoomGroup=centerXOnImage*contentSize.getWidth()/mapView.getImage().getWidth();
        double centerYOnZoomGroup=centerYOnImage*contentSize.getHeight()/mapView.getImage().getHeight();

        scrollPane.setHvalue((centerXOnZoomGroup-viewPort.getWidth()/2)/(contentSize.getWidth()-viewPort.getWidth()));
        scrollPane.setVvalue((centerYOnZoomGroup-viewPort.getHeight()/2)/(contentSize.getHeight()-viewPort.getHeight()));
        System.out.println("Hvalue: "+scrollPane.getHvalue());
        System.out.println("Vvalue: "+scrollPane.getVvalue());
        System.out.println("ZoomGroupWidth: "+contentSize.getWidth());
        System.out.println("ZoomGroupHeight: "+contentSize.getHeight());
    }

    /**
     * Helper method for the zoom methods
     * @param scaleValue adjusts the screen size
     * @return boolean to indicate if the scale has changed
     */
    private boolean setZoom(double scaleValue){
        //calculates the farthest out someone can zoom
        double widthRatio = container.getWidth() / mapView.getFitWidth();
        double heightRatio = container.getHeight() / mapView.getFitHeight();
        double minScrollValue = Math.max(widthRatio, heightRatio);
        //calculates the farthest in someone can zoom
        double maxScrollValue = 1;

        //bounds the scaleValue within the min and max zoom values
        scaleValue=Math.min(scaleValue,maxScrollValue);
        scaleValue=Math.max(scaleValue,minScrollValue);
        //Checks if the scale value is different from the current scale
        boolean zoomAdjusted= 1!=scaleValue/zoomGroup.getScaleX();
        if(zoomAdjusted) {
            zoomGroup.setScaleX(scaleValue);
            zoomGroup.setScaleY(scaleValue);
        }
        return zoomAdjusted;
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
            parent.onMapLocationClicked(event);
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

    /**
     * Getter for zoomSlider
     * @return the zoomSlider value from the map
     */
    public JFXSlider getZoomSlider() {
        return zoomSlider;
    }

    @FXML
    public void zoomInPressed() {
        ignoreZoomSliderListener = false;
        zoomSlider.setValue(zoomSlider.getValue()*1.2);
    }

    @FXML
    public void zoomOutPressed() throws InterruptedException{
        ignoreZoomSliderListener = false;
        zoomSlider.setValue(zoomSlider.getValue()/1.2);
    }

    /**
     * sets the mouseZoom boolean as false to focus the zoom on the center of the display
     */
    @FXML
    protected void zoomWithSlider(){
        ignoreZoomSliderListener = false;
    }

    @FXML
    public void recenterPressed() {
        setFloorSelector(systemSettings.getKioskLocation().getFloor());
        onFloorSelected();
        LinkedList<Node> defaultNode = new LinkedList<Node>();
        defaultNode.add(systemSettings.getKioskLocation());
        zoomOnSelectedNodes(defaultNode);
        this.scrollPane.setHvalue(SystemSettings.getInstance().getKioskLocation().getXcoord()/mapView.getFitWidth()-0.04);
        this.scrollPane.setVvalue(SystemSettings.getInstance().getKioskLocation().getYcoord()/mapView.getFitHeight()-0.04);
    }

    /**
     * center the view at input node
     * @param node the node to recenter at
     */
    public void recenterAtNode(Node node) {
        this.scrollPane.setHvalue(node.getXcoord()/mapView.getFitWidth()-0.04);
        this.scrollPane.setVvalue(node.getYcoord()/mapView.getFitHeight()-0.04);
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

    public void nodesConnected(String nodeID1, String nodeID2) {
        parent.nodesConnected(nodeID1, nodeID2);
    }

    @FXML
    void onLanguageSelected() {
        SystemSettings systemSettings = SystemSettings.getInstance();
        systemSettings.setResourceBundle(languageSelector.getValue());
    }

    /**
     * Resets the timer in the MainWindowController
     */
    @FXML
    public void resetTimer(){
        parent.resetTimer();
    }

    @FXML
    private void btKeyClicked(){
        if(keyDialogContainer.isDisabled()) {
            keyDialogContainer.setDisable(false);
            keyDialog.show();
            keyButton.setStyle("-fx-background-color: #039f00");
        }
        else {
            keyDialog.close();
            keyDialogContainer.setDisable(true);
            keyButton.setStyle("-fx-background-color: #00589F");
        }
    }

    @FXML
    public void keyClosed() {
        keyDialog.close();
        keyDialogContainer.setDisable(true);
    }

    public MiniMapController getMiniMapController() {
        return miniMapController;
    }
}