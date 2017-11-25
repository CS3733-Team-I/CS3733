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
import utility.Node.NodeFloor;

import java.io.IOException;
import java.util.ArrayList;
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
    private ArrayList<Circle> nodeObjectList;
    private ArrayList<Line> edgeObjectList;
    //protected  javafx.scene.Node heightLightedNode;

    private Group zoomGroup;

    private LinkedList<MenuButton> waypoints;

    private MainWindowController parent = null;

    protected static double DEFAULT_HVALUE = 0.52;
    protected static double DEFAULT_VVALUE = 0.3;

    public MapController() {
        waypoints = new LinkedList<>();
    }

    public void setParent(MainWindowController controller)
    {
        parent = controller;
    }

    public void HighlightNode(database.objects.Node node) {
        for(Circle nodeO : nodeObjectList) {
            if(nodeO.getAccessibleText() == node.getNodeID()) {

                nodesEdgesPane.getChildren().remove(nodeO);
                nodeO.setFill(Color.BLUE);
                nodesEdgesPane.getChildren().add(nodeO);
            }
        }
    }

    public void DehighlightNode(database.objects.Node node) {
        for(Circle nodeO : nodeObjectList) {
            if(nodeO.getAccessibleText() == node.getNodeID()) {

                nodesEdgesPane.getChildren().remove(nodeO);
                nodeO.setFill(Color.GRAY);
                nodesEdgesPane.getChildren().add(nodeO);
            }
        }
    }

    public void HightlightChangedNode(database.objects.Node node) {
        for(Circle nodeO : nodeObjectList) {
            if(nodeO.getAccessibleText() == node.getNodeID()) {

                nodesEdgesPane.getChildren().remove(nodeO);
                nodeO.setFill(Color.YELLOW);
                nodesEdgesPane.getChildren().add(nodeO);
            }
        }
    }

//    public void HighlightNewNode(database.objects.Node node) {
//        Circle newNodeView = new Circle(node.getXcoord(), node.getYcoord(), 14, Color.)
//    }

    public void reloadDisplay() {
        showNodesBox.setSelected(false);
        showEdgesBox.setSelected(false);
    }

    public void clearMap() {
        waypointPane.getChildren().clear();
        waypoints.clear();

        reloadDisplay();
    }

    public void drawNodesOnMap(List<Node> nodes) {
        for (Node n : nodes) {
            //use a scene shape so that it can be properly highlighted (more interactive)
            Circle nodeView = new Circle(n.getXcoord(), n.getYcoord(), 14, Color.GRAY);
            nodeView.setStroke(Color.BLACK);
            nodeView.setStrokeWidth(3);
            nodeView.setMouseTransparent(false);
            nodeView.setOnMouseClicked(mouseEvent -> mapNodeClicked(n));
            nodeView.setPickOnBounds(false);
            nodeView.setAccessibleText(n.getNodeID());

            nodesEdgesPane.getChildren().add(nodeView);
            nodeObjectList.add(nodeView);

        }
    }

    public void drawEdgesOnMap(List<Edge> edges) {
        MapEntity mapEntity = MapEntity.getInstance();
        nodesEdgesPane.setPickOnBounds(false);
        for (Edge e : edges) {
            Node node1 = mapEntity.getNode(e.getNode1ID());
            Node node2 = mapEntity.getNode(e.getNode2ID());

            // TODO(jerry) make this more modular, maybe into an FXML file??
            // TODO(leo) keep this as it is, or it can NOT properly interact with other map modules
            Line edgeView = new Line(node1.getXcoord(), node1.getYcoord(), node2.getXcoord(), node2.getYcoord());
            edgeView.setStroke(Color.PURPLE);
            edgeView.setStrokeWidth(10);
            edgeView.setMouseTransparent(false);
            edgeView.setOnMouseClicked(mouseEvent -> mapEdgeClicked(e));
            edgeView.setPickOnBounds(false);
            edgeView.setAccessibleText(e.getEdgeID());

            edgeObjectList.add(edgeView);
            nodesEdgesPane.getChildren().add(edgeView);
        }
        //this.nodesEdgesPane.getChildren().add(nodesEdgesPane);
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

        nodeObjectList = new ArrayList<Circle>();
        edgeObjectList = new ArrayList<Line>();

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
         * Benefit of using listener instead of calling methods to to prevent the checkbox and display state off-sync
         */
        showNodesBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue) {
                    drawNodesOnMap(MapEntity.getInstance().getNodesOnFloor(floorSelector.getValue()));
                }
                else {
                    for (javafx.scene.Node n : nodeObjectList) {
                        nodesEdgesPane.getChildren().remove(n);
                    }
                    nodeObjectList.clear();
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
                    for (javafx.scene.Node n: edgeObjectList) {
                        nodesEdgesPane.getChildren().remove(n);
                    }
                    edgeObjectList.clear();
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
         * Detect double click event to add a node
         */
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