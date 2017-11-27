package controller;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;
import entity.Path;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import utility.node.NodeFloor;
import utility.Display.Node.NodeDisplay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MapController {
    @FXML protected AnchorPane container;

    @FXML protected ScrollPane scrollPane;

    @FXML private ImageView mapView;
    @FXML private AnchorPane nodesEdgesPane;
    @FXML private AnchorPane waypointPane;

    @FXML protected JFXComboBox<NodeFloor> floorSelector;
    @FXML private JFXSlider zoomSlider;

    @FXML private VBox optionsBox;
    @FXML protected JFXCheckBox showNodesBox;
    @FXML protected JFXCheckBox showEdgesBox;

    @FXML public AnchorPane miniMapPane;
    MiniMapController miniMapController;


    //list of showing nodes or edges
    //protected  javafx.scene.node heightLightedNode;
    private ObservableList<database.objects.Node> DatabaseNodeObjectList;
    private ObservableList<database.objects.Edge> DatabaseEdgeObjectList;
    private ObservableList<Circle> nodeObjectList;
    private ObservableList<Line> edgeObjectList;
    protected ObservableList<database.objects.Node> observableHighlightededSelectedNodes;
    protected ObservableList<database.objects.Node> observableHighlightededChangedNodes;
    protected ObservableList<database.objects.Node> observableHighlightededNewNodes;
    protected ObservableList<database.objects.Edge> observableHighlightedEdges;

    private Group zoomGroup;

    private LinkedList<MenuButton> waypoints;

    private MainWindowController parent = null;

    protected static double DEFAULT_HVALUE = 0.52;
    protected static double DEFAULT_VVALUE = 0.3;

    public MapController() {
        waypoints = new LinkedList<>();

        DatabaseNodeObjectList = FXCollections.<database.objects.Node>observableArrayList();
        DatabaseEdgeObjectList = FXCollections.<database.objects.Edge>observableArrayList();

        nodeObjectList = FXCollections.<Circle>observableArrayList();
        edgeObjectList = FXCollections.<Line>observableArrayList();

        observableHighlightededSelectedNodes = FXCollections.<database.objects.Node>observableArrayList();
        observableHighlightededChangedNodes = FXCollections.<database.objects.Node>observableArrayList();
        observableHighlightededNewNodes = FXCollections.<database.objects.Node>observableArrayList();
        observableHighlightedEdges = FXCollections.<database.objects.Edge>observableArrayList();
    }

    public void setParent(MainWindowController controller)
    {
        parent = controller;
    }

    //TODO put this into nodeobjectlist permuted
    public void HighlightNode(database.objects.Node targetNode, NodeDisplay nodeDisplay) {
        for(Circle nodeO : nodeObjectList) {
            if(nodeO.getAccessibleText() == targetNode.getNodeID()) {
                nodesEdgesPane.getChildren().remove(nodeO);
                switch (nodeDisplay) {
                    case SELECTED:
                        nodeO.setFill(Color.BLUE);
                        break;
                    case CHANGED:
                        nodeO.setFill(Color.RED);
                        break;
                    case NORMAL:
                        nodeO.setFill(Color.GRAY);
                        break;
                    case SELECTEDANDCHANGED:
                        nodeO.setFill(Color.PURPLE);
                        break;
                    case NEW:
                        nodeO.setFill(Color.YELLOW);
                        break;
                }
                nodesEdgesPane.getChildren().add(nodeO);
                return;
            }
        }
        //in case of a new node
        if(nodeDisplay == NodeDisplay.NEW) {
            DatabaseNodeObjectList.add(targetNode);
            HighlightNode(targetNode, NodeDisplay.NEW);
        }
    }

    public void reloadDisplay() {
        showNodesBox.setSelected(false);
        showEdgesBox.setSelected(false);
    }

    public void clearMap() {
        waypointPane.getChildren().clear();
        waypoints.clear();

        reloadDisplay();
    }

    protected void drawNodesOnMap(List<database.objects.Node> nodes) {
        DatabaseNodeObjectList.addAll(nodes);
    }

    protected void undrawNodeOnMap(database.objects.Node node) {
        DatabaseNodeObjectList.remove(node);
    }

    protected void drawEdgesOnMap(List<Edge> edges) {
        DatabaseEdgeObjectList.addAll(edges);
    }

    public void mapEdgeClicked(Edge e) {
        if (!parent.equals(null)) {
            parent.onMapEdgeClicked(e);
        }
    }

    public void mapNodeClicked(Node n) {
        if (!parent.equals(null)) {
            parent.onMapNodeClicked(n);
        }
    }

    public void drawPath(Path path) {
        MapEntity mapEntity = MapEntity.getInstance();

        // Change to floor of the starting node
        floorSelector.setValue(path.getWaypoints().get(0).getFloor());

        clearMap();
        drawEdgesOnMap(path.getEdges());
        drawNodesOnMap(path.getWaypoints());
    }

    public void addWaypoint(Point2D location) {
        try {
            // put the pin and set it's info
            MenuButton wayPointObject = FXMLLoader.load(getClass().getResource("/view/WaypointView.fxml"));

            /*Offsets, don't remove*/
            // double pinW = wayPointObject.getBoundsInLocal().getWidth();
            // double pinH = wayPointObject.getBoundsInLocal().getHeight();

            // TODO magic numbers
            wayPointObject.setTranslateX(location.getX() - 24);
            wayPointObject.setTranslateY(location.getY() - 60);

            //TODO handle waypoint option
            //wayPointObject.setOnAction(ActionEvent -> WaypointOptions());

            waypoints.add(wayPointObject);
            waypointPane.getChildren().add(wayPointObject);

            // System.out.println("should be at "+ (event.getX() - (pinW / 2)) + " " + (event.getY() - (pinH / 2)));
            // System.out.println("actually at "+ wayPointObject.getLayoutX() + " " + wayPointObject.getLayoutX());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * This method's visibility should be keep private and only used in MapController's listener
     * For switching bwtween floors, either in this class or sidebar controller classes,
     * call "mapController.floorSelector.setValue(your floor)"
     */
    private void loadFloor(NodeFloor floor) {
        String floorImageURL = "";
        switch (floor) {
            case LOWERLEVEL_2:
                floorImageURL = getClass().getResource("/images/00_thelowerlevel2.png").toString();
                break;
            case LOWERLEVEL_1:
                floorImageURL = getClass().getResource("/images/00_thelowerlevel1.png").toString();
                break;
            case GROUND:
                floorImageURL = getClass().getResource("/images/00_thegroundfloor.png").toString();
                break;
            case FIRST:
                floorImageURL = getClass().getResource("/images/01_thefirstfloor.png").toString();
                break;
            case SECOND:
                floorImageURL = getClass().getResource("/images/02_thesecondfloor.png").toString();
                break;
            case THIRD:
                floorImageURL = getClass().getResource("/images/03_thethirdfloor.png").toString();
                break;
        }

        Image floorImage = new Image(floorImageURL);
        mapView.setImage(floorImage);
        mapView.setFitWidth(floorImage.getWidth());
        mapView.setFitHeight(floorImage.getHeight());
        //System.out.println("Image Width: " + floorImage.getWidth());
        //System.out.println("Image Height: " + floorImage.getHeight());

        miniMapController.switchFloor(floorImage);
    }

    public NodeFloor getCurrentFloor() {
        return floorSelector.getValue();
    }

    public void setAnchor(double top, double left, double bottom, double  right) {
        if (container != null) {
            AnchorPane.setTopAnchor(container, top);
            AnchorPane.setLeftAnchor(container, left);
            AnchorPane.setRightAnchor(container, bottom);
            AnchorPane.setBottomAnchor(container, right);
        }
    }

    private void zoom(double scaleValue) {
        double scrollH = scrollPane.getHvalue();
        double scrollV = scrollPane.getVvalue();
        zoomGroup.setScaleX(scaleValue);
        zoomGroup.setScaleY(scaleValue);
        scrollPane.setHvalue(scrollH);
        scrollPane.setVvalue(scrollV);
        miniMapController.NavigationRecZoom(scaleValue);
        //System.out.println(scaleValue);
    }

    public void setFloorSelectorPosition(Point2D position) {
        AnchorPane.setTopAnchor(floorSelector, position.getY());
        AnchorPane.setLeftAnchor(floorSelector, position.getX());
    }

    @FXML
    protected void initialize() {

        nodesEdgesPane.setPickOnBounds(false);
        waypointPane.setPickOnBounds(false);

        floorSelector.getItems().addAll(NodeFloor.values());

        miniMapController = new MiniMapController(parent, this);
        miniMapPane.getChildren().clear();
        miniMapPane.getChildren().add(miniMapController.getContentView());

        zoomSlider.setMin(0.2);
        zoomSlider.setMax(2.2);
        zoomSlider.setValue(1.2);
        zoomSlider.valueProperty().addListener((o, oldVal, newVal) -> zoom((Double) newVal));

        // Wrap scroll content in a Group so ScrollPane re-computes scroll bars
        Group contentGroup = new Group();
        zoomGroup = new Group();
        contentGroup.getChildren().add(zoomGroup);
        zoomGroup.getChildren().add(scrollPane.getContent());
        scrollPane.setContent(contentGroup);

        //floor changing listeners
        //TODO Solve the Null pointer issue with another way
        final boolean offsetBool = false;
        floorSelector.valueProperty().addListener(new ChangeListener<NodeFloor>() {
            @Override
            public void changed(ObservableValue<? extends NodeFloor> observable, NodeFloor oldValue, NodeFloor newValue) {
                loadFloor(newValue);
                //at application start up, map controller is created first thus no sidebar controller is available at the time

                parent.onMapFloorChanged(newValue);

                reloadDisplay();
            }
        });
        //checkboxes for showing nodes and edges
        /**
         * sync UI
         */
        showNodesBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue) {
                    drawNodesOnMap(MapEntity.getInstance().getNodesOnFloor(floorSelector.getValue()));
                }
                else {
                    DatabaseNodeObjectList.clear();
                }
            }
        });
        showEdgesBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue) {
                    drawEdgesOnMap(MapEntity.getInstance().getEdgesOnFloor(floorSelector.getValue()));
                }
                else {
                    DatabaseEdgeObjectList.clear();
                }
            }
        });

        DatabaseNodeObjectList.addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(Change<? extends Node> c) {
                while(c.next()) {
                    if(c.wasRemoved()) {
                        for (database.objects.Node removedDatabaseNode : c.getRemoved()) {
                            Iterator<Circle> nodeObjectIterator = nodeObjectList.iterator();
                            while (nodeObjectIterator.hasNext()) {
                                Circle circle = nodeObjectIterator.next();
                                if (removedDatabaseNode.getNodeID().equals(circle.getAccessibleText())) {
                                    nodeObjectIterator.remove();
                                    break;
                                }
                            }
                        }
                    }
                    else if(c.wasAdded()) {
                        for(database.objects.Node addedDatabaseNode : c.getAddedSubList()) {
                            Circle nodeView = new Circle(addedDatabaseNode.getXcoord(), addedDatabaseNode.getYcoord(), 14, Color.GRAY);
                            nodeView.setStroke(Color.BLACK);
                            nodeView.setStrokeWidth(3);
                            nodeView.setMouseTransparent(false);
                            nodeView.setOnMouseClicked(mouseEvent -> mapNodeClicked(addedDatabaseNode));
                            nodeView.setPickOnBounds(false);
                            nodeView.setAccessibleText(addedDatabaseNode.getNodeID());
                            nodeObjectList.add(nodeView);
                        }
                    }
                }
            }
        });
        DatabaseEdgeObjectList.addListener(new ListChangeListener<Edge>() {
            @Override
            public void onChanged(Change<? extends Edge> c) {
                while(c.next()) {
                    if(c.wasRemoved()) {
                        for(database.objects.Edge removedDatabaseEdge : c.getRemoved()) {
                            Iterator<Line> edgeObjectIterator = edgeObjectList.iterator();
                            while (edgeObjectIterator.hasNext()) {
                                Line line = edgeObjectIterator.next();
                                if (removedDatabaseEdge.getEdgeID().equals(line.getAccessibleText())) {
                                    edgeObjectIterator.remove();
                                    break;
                                }
                            }
                        }
                    }
                    else if(c.wasAdded()) {
                        for(database.objects.Edge addedDatabaseEdge : c.getAddedSubList()) {
                            MapEntity mapEntity = MapEntity.getInstance();
                            Node node1 = mapEntity.getNode(addedDatabaseEdge.getNode1ID());
                            Node node2 = mapEntity.getNode(addedDatabaseEdge.getNode2ID());
                            Line edgeView = new Line(node1.getXcoord(), node1.getYcoord(), node2.getXcoord(), node2.getYcoord());
                            edgeView.setStroke(Color.GREY);
                            edgeView.setStrokeWidth(10);
                            edgeView.setMouseTransparent(false);
                            edgeView.setOnMouseClicked(mouseEvent -> mapEdgeClicked(addedDatabaseEdge));
                            edgeView.setPickOnBounds(false);
                            edgeView.setAccessibleText(addedDatabaseEdge.getEdgeID());
                            edgeObjectList.add(edgeView);
                        }
                    }
                }
            }
        });
        //TODO INTEGRATE THIS LISTENER WITH nodesEdgesPane LISTENER
        nodeObjectList.addListener(new ListChangeListener<Circle>() {
            @Override
            public void onChanged(Change<? extends Circle> c) {
                while(c.next()) {
                    if(c.wasRemoved()) {
                        for(Circle removedCircle : c.getRemoved()) {
                            nodesEdgesPane.getChildren().remove(removedCircle);
                        }
                    }
                    else if(c.wasAdded()) {
                        for(Circle addedCircle : c.getAddedSubList()) {
                            nodesEdgesPane.getChildren().add(addedCircle);
                        }
                    }
                }
            }
        });

        edgeObjectList.addListener(new ListChangeListener<Line>() {
            @Override
            public void onChanged(Change<? extends Line> c) {
                while(c.next()) {
                    if(c.wasRemoved()) {
                        for(Line removedLine : c.getRemoved()) {
                            nodesEdgesPane.getChildren().remove(removedLine);
                        }
                    }
                    else if(c.wasAdded()) {
                        for(Line addedLine : c.getAddedSubList()) {
                            nodesEdgesPane.getChildren().add(addedLine);
                        }
                    }
                }
            }
        });

        //update minimap navigationRec's position
        scrollPane.hvalueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                miniMapController.setNavigationRecX((double)newValue/scrollPane.getHmax());
            }
        });

        scrollPane.vvalueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                miniMapController.setNavigationRecY((double)newValue/scrollPane.getVmax());
            }
        });
        //adjust minimap navigationRec's width:height
        container.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                miniMapController.setNavigationRecWidth((double)newValue);
            }
        });

        container.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                miniMapController.setNavigationRecHeight((double)newValue);
            }
        });
        /**
         * highlight nodes and edges
         */
        observableHighlightededSelectedNodes.addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(Change<? extends Node> c) {
                //revert deselected nodes to normal color
                while(c.next()) {
                    if(c.wasRemoved()) {
                        for(database.objects.Node deseletedNode : c.getRemoved()) {
                            if(!observableHighlightededChangedNodes.contains(deseletedNode)) {
                                HighlightNode(deseletedNode, NodeDisplay.NORMAL);
                            }
                            else {
                                HighlightNode(deseletedNode, NodeDisplay.CHANGED);
                            }
                        }
                    }
                    else if(c.wasAdded()) {
                        for(database.objects.Node selectedNode : c.getAddedSubList()) {
                            if(observableHighlightededChangedNodes.contains(selectedNode)){
                                HighlightNode(selectedNode, NodeDisplay.SELECTEDANDCHANGED);
                            }
                            else {
                                HighlightNode(selectedNode, NodeDisplay.SELECTED);
                            }
                        }
                    }
                }
            }
        });
        //TODO
        observableHighlightededChangedNodes.addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(Change<? extends Node> c) {
                while(c.next()) {
                    if(c.wasAdded()){
                        for(database.objects.Node addedChangedNode : c.getAddedSubList()) {
                            if(observableHighlightededSelectedNodes.contains(addedChangedNode)) {
                                HighlightNode(addedChangedNode, NodeDisplay.SELECTEDANDCHANGED);
                            }
                            else {
                                HighlightNode(addedChangedNode, NodeDisplay.CHANGED);
                            }
                        }
                    }
                    else if(c.wasRemoved()) {
                        for(database.objects.Node removedChangedNode : c.getRemoved()) {
                            if(observableHighlightededSelectedNodes.contains(removedChangedNode)) {
                                HighlightNode(removedChangedNode, NodeDisplay.SELECTED);
                            }
                            else {
                                HighlightNode(removedChangedNode, NodeDisplay.NORMAL);
                            }
                        }
                    }
                }
            }
        });
        observableHighlightededNewNodes.addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(Change<? extends Node> c) {
                //remove unsaved new nodes
                while(c.next()) {
                    if(c.wasRemoved()) {
                        for(database.objects.Node deseletedNewNode : c.getRemoved()) {
                            if(MapEntity.getInstance().getNode(deseletedNewNode.getNodeID()) != null) {//the node was saved to database
                                HighlightNode(deseletedNewNode, NodeDisplay.NORMAL);
                            }
                            else {
                                undrawNodeOnMap(deseletedNewNode);
                            }
                        }
                    }
                    else if(c.wasAdded()) {
                        for(database.objects.Node newNode : c.getAddedSubList()) {
                            HighlightNode(newNode, NodeDisplay.NEW);
                        }
                    }
                }
            }
        });
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
    protected void zoomInPressed() {
        //System.out.println("Zoom in clicked");
        double sliderVal = zoomSlider.getValue();
        zoomSlider.setValue(sliderVal + 0.1);
    }

    @FXML
    protected void zoomOutPressed() {
        //System.out.println("Zoom out clicked");
        double sliderVal = zoomSlider.getValue();
        zoomSlider.setValue(sliderVal + -0.1);
    }

    @FXML
    protected void recenterPressed() {
        this.scrollPane.setHvalue(DEFAULT_HVALUE);
        this.scrollPane.setVvalue(DEFAULT_VVALUE);
    }
}