package controller.map;

import com.jfoenix.controls.*;
import controller.MainWindowController;
import database.connection.NotFoundException;
import database.objects.Edge;
import database.objects.Node;
import database.utility.DatabaseException;
import entity.MapEntity;
import entity.Path;
import entity.SystemSettings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import utility.ApplicationScreen;
import utility.ResourceManager;
import utility.node.NodeFloor;
import utility.node.NodeSelectionType;

import java.io.IOException;
import java.util.*;

import static javafx.scene.layout.Region.USE_PREF_SIZE;

public class MapController {
    @FXML private AnchorPane container;
    @FXML private AnchorPane contentPane;
    @FXML private BorderPane mapBorder;
    @FXML private AnchorPane trayContainer;
    private FloorPreviewTray previewTray;

    private Group zoomGroup;
    @FXML private ScrollPane scrollPane;

    public static final double DEFAULT_HVALUE = 0.52;
    public static final double DEFAULT_VVALUE = 0.3;
    public static final double DEFAULT_ZOOM = 0.75;

    @FXML private StackPane mapStackPane;
    @FXML private ImageView mapView;
    @FXML private AnchorPane nodesEdgesContainer;
    @FXML private AnchorPane pathWaypointContainer;
    @FXML private Label copyrightLabel;

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

    private NodesEdgesView nodesEdgesView;
    private boolean editMode = false;

    private PathWaypointView pathWaypointView;
    @FXML private JFXPopup popup;

    private MiniMapController miniMapController;
    @FXML private AnchorPane miniMapPane;
    private SystemSettings systemSettings;

    @FXML private JFXDialogLayout aboutLayout;

    private MainWindowController parent = null;

    public boolean isWheelchairSet;
    @FXML
    private JFXCheckBox wheelchairCheckbox;

    public MapController() {
        visibleWaypoints = FXCollections.<javafx.scene.Node>observableArrayList();
        systemSettings = SystemSettings.getInstance();
    }

    /**
     * Initialize the MapController. Called when the FXML file for this is loaded
     */
    @FXML
    protected void initialize() throws NotFoundException{
        Image wheelChairIcon = ResourceManager.getInstance().getImage("/images/icons/wheelchair.png");
        ImageView wheelChairView = new ImageView(wheelChairIcon);
        this.wheelchairCheckbox.setGraphic(wheelChairView);

        floorSelector.getItems().addAll(NodeFloor.values());
        aboutButton.setVisible(true);
        languageSelector.getItems().addAll("English","French");
        languageSelector.setValue("English");

        miniMapController = new MiniMapController(this);

        //initialize paths and waypoints view
        pathWaypointView = new PathWaypointView(this);
        pathWaypointView.setPickOnBounds(false);
        //initialize nodes and egdes view
        nodesEdgesView = new NodesEdgesView(this);
        nodesEdgesView.setPickOnBounds(false);

        recenterButton.setText(SystemSettings.getInstance().getResourceBundle().getString("my.recenter"));
        keyButton.setText(SystemSettings.getInstance().getResourceBundle().getString("mapKey"));
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

        aboutLayout.setDisable(true);
        this.isWheelchairSet = false;
        wheelchairCheckbox.setSelected(false);
        wheelchairCheckbox.setOnAction(e -> {
            this.isWheelchairSet = wheelchairCheckbox.isSelected();
        });

        // Controller-wide localization observer
        systemSettings.addObserver((o, arg) -> {
            ResourceBundle rB = systemSettings.getResourceBundle();
            recenterButton.setText(rB.getString("my.recenter"));
        });
        systemSettings.addObserver((o,arg) -> {
            keyButton.setText(systemSettings.getInstance().getResourceBundle().getString("mapKey"));
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

        this.previewTray = new FloorPreviewTray();
        this.trayContainer.getChildren().add(previewTray);
        this.trayContainer.setPrefHeight(220);
        AnchorPane.setTopAnchor(previewTray, 0.0);
        AnchorPane.setBottomAnchor(previewTray, 0.0);
        AnchorPane.setLeftAnchor(previewTray, 0.0);
        AnchorPane.setRightAnchor(previewTray, 0.0);
//        this.previewTray.addPreviewMap(NodeFloor.GROUND);  //TODO: remove after debugging
//        this.previewTray.addPreviewMap(NodeFloor.FIRST);  //TODO: remove after debugging
//        this.previewTray.addPreviewMap(NodeFloor.THIRD);  //TODO: remove after debugging
//        this.previewTray.addPreviewMap(NodeFloor.SECOND);  //TODO: remove after debugging
        this.hideTray(); //TODO: after debugging, start tray hidden
    }

    public void hideTray(){
        this.mapBorder.setBottom(null);
    }

    public void showTray(){
        this.mapBorder.setBottom(this.trayContainer);
    }

    //TODO: not sure how good it is to be using the control to just pass through commands
    public void clearTray(){
        this.previewTray.clearPreviews();



        mapView.setOnDragOver(e -> {
            /*String message = e.getDragboard().getString();
            String condition = String.valueOf(message.charAt(message.length()-1));
            if(condition.equals("T")){e.consume();}*/

            if(e.getDragboard().hasString()) {
                System.out.println("Drag node over map.");
                String content = e.getDragboard().getString();
                int length = content.length();

                int uniqueID = Integer.parseInt(e.getDragboard().getString().substring(0, length-1));
                String condition = String.valueOf(content.charAt(length-1));
                if(condition.equals("S")){
                    return;
                }

                Node selectNode = MapEntity.getInstance().getNodeByID(uniqueID);

                Point2D point2D = new Point2D(e.getX(), e.getY());
                if(condition.equals("C")){
                    point2D = new Point2D(roundToFive((int)e.getX()), roundToFive((int)e.getY()));
                }

                nodesEdgesView.setNodePosition(selectNode, point2D);
                e.acceptTransferModes(TransferMode.MOVE);
                e.consume();
            }
        });


        mapView.setOnDragDropped(e ->{
            System.out.println("Node Dropped");

            String content = e.getDragboard().getString();
            int length = content.length();
            int uniqueID = Integer.parseInt(e.getDragboard().getString().substring(0, length-1));
            String condition = String.valueOf(content.charAt(length-1));
            if(condition.equals("T")){
                return;
            }

            double xcoord = e.getX();
            double ycoord = e.getY();
            if(condition.equals("C")){
                xcoord = roundToFive((int)xcoord);
                ycoord = roundToFive((int)ycoord);
            }
            Node selectNode = MapEntity.getInstance().getNodeByID(uniqueID);
            selectNode.setXcoord((int)xcoord);
            selectNode.setYcoord((int)ycoord);

            try {
                MapEntity.getInstance().editNodeByUK(selectNode);
            } catch (DatabaseException e1) {
                e1.printStackTrace();
            }

            reloadDisplay();
            e.setDropCompleted(true);
            e.consume();
        });
    }

    /**
     * round to 5
     * @param num
     * @return
     */
    public static int roundToFive(int num){
        int a = num % 5;
        int b = num / 5;

        int result = b*5;
        return result;
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

            this.previewTray.clearPreviews();   //remove old previews
            this.previewTray.generatePreviews(path, this);
            pathWaypointView.drawPath(path);
            miniMapController.showPath(path);
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
     * Sets the selected value for the language combobox
     * @param lang value to set
     */
    public void setLanguageSelector(String lang){
        languageSelector.setValue(lang);
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
        pathWaypointView.clearAll();
        clearNodes();
        this.miniMapController.clearWaypoints();
        this.miniMapController.clearPath();
    }

    public void clearNodes() {
        this.nodesEdgesView.clear();
    }

    /**
     * Add a waypoint indicator to the map and minimap
     * @param node the waypoint's node
     */
    public void addWaypoint(Node node) {
        this.pathWaypointView.addWaypoint(node);
        this.miniMapController.addWaypoint(node);
    }

    /**
     * Remove a waypoint indicator from the map and minimap
     * @param node the waypoint's node
     */
    public void removeWaypoint(Node node) {
        this.pathWaypointView.removeWaypoint(node);
        this.miniMapController.removeWaypoint(node);
    }

    /**
     * Load a new floor image and display it. Additionally re-renders the current path based on the floor being viewed
     * @param floor the floor to load
     */
    private void loadFloor(NodeFloor floor) {
        Image floorImage = ResourceManager.getInstance().getImage(floor.toImagePath());
        mapView.setImage(floorImage);
        mapView.setFitWidth(floorImage.getWidth());
        mapView.setFitHeight(floorImage.getHeight());
        pathWaypointView.reloadDisplay();
        miniMapController.switchFloor(floor.toImagePath());
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

    public void setContentLeftAnchor(double left) {
        if (container != null) {
            AnchorPane.setLeftAnchor(contentPane, left);
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
    public void zoomOnSelectedNodes(List<Node> viewedNodes){
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
        if (parent != null && event.isStillSincePress()) {
            MapEntity mapEntity = MapEntity.getInstance();
            LinkedList<Node> nearestNodes = new LinkedList<>();
            double radius = 150.0;
            // Check if clicked location is a node
            LinkedList<Node> floorNodes = mapEntity.getNodesOnFloor(floorSelector.getValue());
            Circle clickArea = new Circle(event.getX(),event.getY(),radius);
            for (Node node : floorNodes) {
                Point2D nodePosition = new Point2D(node.getXcoord(), node.getYcoord());

                if (clickArea.contains(nodePosition)) {
                    nearestNodes.add(node);
                }
            }
            Point2D clickPosition = new Point2D(event.getX(),event.getY());
            // Nearest neighbor calculation
            if(parent.currentScreen != ApplicationScreen.MAP_BUILDER) {
                double shortestDistance = radius+1;
                Node nearestNode = null;
                for (Node node : nearestNodes){
                    double distance = clickPosition.distance(node.getXcoord(),node.getYcoord());
                    if(distance < shortestDistance){
                        shortestDistance=distance;
                        nearestNode = node;
                    }
                }
                if(nearestNode!=null){
                    //System.out.println("Shortest Distance: "+shortestDistance);
                    parent.onMapNodeClicked(nearestNode);
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

    @FXML
    private void onAboutAction(){

        JFXDialogLayout aboutDialogLayout = new JFXDialogLayout();
        aboutDialogLayout.setHeading(new Text("About"));
        aboutDialogLayout.setBody(new Text("Team Members:\n\n" +
                "Benjamin Gillette - Project Manager/ Scrum Master \n" +
                "Jerish Brown - Lead Software Engineer         \n" +
                "John Parrick  - Assistant Lead Software Engineer\n" +
                "Ziheng (Leo) Li - Assistant Lead Software Engineer\n" +
                "James Taylor - Product Owner\n" +
                "Michael Sidler - Test Engineer\n" +
                "Mary Hatfalvi - Documentation Analyst\n" +
                "Henry Dunphy - Software Engineer\n" +
                "Ryan Racine  - Software Engineer\n" +
                "Da Xu - Software Engineer\n" +
                "Team Coach - Akshit (Axe) Soota \n" +
                "\n" +
                "WPI Computer Science Department, \n" +
                "CS3733 Software Engineering, Prof. Wilson Wong, \n\n" +
                "We would like to thank Brigham and Women’s Faulkner Hospital and the representative, Andrew Shinn, for their time and input.\n"+
        "\nThe Brigham & Women’s Hospital maps and data used in this application are copyrighted and provided for the sole use of educational purposes."));
        JFXDialog dialog = new JFXDialog(aboutLayout, aboutDialogLayout, JFXDialog.DialogTransition.CENTER);

        JFXButton btnodeDialog= new JFXButton("OK");
        btnodeDialog.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                aboutLayout.setDisable(true);
                dialog.close();
                aboutButton.setStyle("-fx-background-color: #00589F");
            }
        });

        JFXButton btnodeDialog2 = new JFXButton("CONT");
        btnodeDialog2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                aboutDialogLayout.setHeading(new Text("API Credit"));
                aboutDialogLayout.setBody(new Text("Transportation API courtesy of Team Bronze Basilisk\n\n" +
                        "Team Members:\n" +
                        "Josiah Boucher, Kenneth Colpritt,\n" +
                        "Antonio Costa Ferreira, Emir Emir, \n" +
                        "Sydney Fisher, Jacob Henry, \n" +
                        "Demi Karavoussianis, Matthew Kornitsky, \n" +
                        "Yosuke Nakamura, Benjamin Pasculano, Grace Seiche"));
                btnodeDialog2.setVisible(false);
            }
        });
        aboutDialogLayout.setActions(btnodeDialog2,btnodeDialog);

        if(aboutLayout.isDisable()) {
            aboutLayout.setDisable(false);
            dialog.show();
            aboutButton.setStyle("-fx-background-color: #039f00");
        }
        else {
            aboutLayout.setDisable(true);
            dialog.close();
            aboutButton.setStyle("-fx-background-color: #00589F");
        }
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

}