package controller;

import com.jfoenix.controls.JFXRippler;
import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;
import entity.Path;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import utility.NodeFloor;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class MapController implements Initializable{
    @FXML private AnchorPane container;
    @FXML private ImageView mapView;
    @FXML private StackPane stackPane;
    @FXML private ScrollPane scrollPane;
    @FXML private ComboBox<NodeFloor> floorSelector;
    @FXML private Slider zoomSlider;
    @FXML private ArrayList<MenuButton> wayPoints;

    Group zoomGroup;

    private MainWindowController parent = null;

    private static double DEFAULT_HVALUE = 0.52;
    private static double DEFAULT_VVALUE = 0.3;

    private NodeFloor currentFloor = NodeFloor.THIRD;

    private boolean showNodes = false;
    private boolean showEdges = false;

    public MapController() {
        this.wayPoints = new ArrayList<MenuButton>();
    }

    public void setParent(MainWindowController controller)
    {
        parent = controller;
    }

    //this method should be called when ever the instance of this class is created to enable zooming,
    //must be called after setController() and load()
    @Override
    public void initialize(URL url, ResourceBundle rb) {
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

        //add Rippler effect
        JFXRippler mapRippler = new JFXRippler(mapView);
        mapRippler.setMaskType(JFXRippler.RipplerMask.RECT);
        mapRippler.setRipplerFill(Paint.valueOf("#0019ff"));
        mapRippler.setRipplerRadius(100);
        //mapRippler.setOverlayVisible(true,true);

        container.getChildren().add(mapRippler);
    }

    public void setShowNodes(boolean showNodes) {
        this.showNodes = showNodes;

        this.clearMap();
        if (this.showEdges) drawEdgesOnMap(MapEntity.getInstance().getEdgesOnFloor(currentFloor));
        if (this.showNodes) drawNodesOnMap(MapEntity.getInstance().getNodesOnFloor(currentFloor));
    }

    public void setShowEdges(boolean showEdges) {
        this.showEdges = showEdges;

        // TODO remove copypasta
        this.clearMap();
        if (this.showEdges) drawEdgesOnMap(MapEntity.getInstance().getEdgesOnFloor(currentFloor));
        if (this.showNodes) drawNodesOnMap(MapEntity.getInstance().getNodesOnFloor(currentFloor));
    }

    public void clearMap() {
        stackPane.getChildren().clear();
        stackPane.getChildren().add(mapView);
    }

    public void drawNodesOnMap(List<Node> nodes) {
        for (Node n : nodes) {
            try {
                javafx.scene.Node nodeObject = FXMLLoader.load(getClass().getResource("/view/NodeView.fxml"));
                nodeObject.setTranslateX(n.getXcoord() - 14); // TODO magic numbers
                nodeObject.setTranslateY(n.getYcoord() - 14); // TODO magic numbers
                nodeObject.setOnMouseClicked(mouseEvent -> mapNodeClicked(n));
                nodeObject.setMouseTransparent(false);
                stackPane.getChildren().add(nodeObject);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void drawEdgesOnMap(List<Edge> edges) {
        MapEntity mapEntity = MapEntity.getInstance();
        AnchorPane edgesPane = new AnchorPane();
        edgesPane.setPickOnBounds(false);
        for (Edge e : edges) {
            Node node1 = mapEntity.getNode(e.getNode1ID());
            Node node2 = mapEntity.getNode(e.getNode2ID());

            // TODO(jerry) make this more modular, maybe into an FXML file??
            Line edgeView = new Line(node1.getXcoord(), node1.getYcoord(), node2.getXcoord(), node2.getYcoord());
            edgeView.setStroke(Color.PURPLE);
            edgeView.setStrokeWidth(10);
            edgeView.setOnMouseClicked(mouseEvent -> mapEdgeClicked(e));
            edgeView.setPickOnBounds(false);
            edgesPane.getChildren().add(edgeView);
        }
        stackPane.getChildren().add(edgesPane);
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
        loadFloor(path.getWaypoints().get(0).getFloor());

        clearMap();
        drawEdgesOnMap(path.getEdges());
        drawNodesOnMap(path.getWaypoints());
    }

    public void clearPath() {
        clearMap();
    }

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

        currentFloor = floor;
    }

    public NodeFloor getCurrentFloor() {
        return currentFloor;
    }

    public void setAnchor(double top, double left, double bottom, double  right) {
        if (container != null) {
            AnchorPane.setTopAnchor(container, top);
            AnchorPane.setLeftAnchor(container, left);
            AnchorPane.setRightAnchor(container, bottom);
            AnchorPane.setBottomAnchor(container, right);
        }
    }

    @FXML
    protected void initialize() {
        floorSelector.getItems().addAll(NodeFloor.values());

        loadFloor(currentFloor);
    }

    @FXML
    protected void onMapClicked(MouseEvent event) throws IOException{
        if (parent != null) {
            // Check if clicked location is a node
            LinkedList<Node> floorNodes = MapEntity.getInstance().getNodesOnFloor(currentFloor);
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
            parent.onMapLocationClicked(new Point2D(event.getX(), event.getY()));
        }


        // put the pin and set it's info
        javafx.scene.control.MenuButton wayPointObject = FXMLLoader.load(getClass().getResource("/view/wayPointView.fxml"));
        /*Offsets, don't remove*/
//        double pinW = wayPointObject.getBoundsInLocal().getWidth();
//        double pinH = wayPointObject.getBoundsInLocal().getHeight();
        wayPointObject.setTranslateX(event.getX()-24);
        wayPointObject.setTranslateY(event.getY()-60);
        //TODO hanld waypoint option
        wayPointObject.setOnAction(ActionEvent -> WaypointOptions());
        stackPane.getChildren().add(wayPointObject);
        this.wayPoints.add(wayPointObject);

//        System.out.println("should be at "+ (event.getX() - (pinW / 2)) + " " + (event.getY() - (pinH / 2)));
//        System.out.println("actually at "+ wayPointObject.getLayoutX() + " " + wayPointObject.getLayoutX());

    }
    //TODO hanld waypoint option
    @FXML
    private void WaypointOptions() {
        System.out.println("Here");
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

    private void zoom(double scaleValue) {
        double scrollH = scrollPane.getHvalue();
        double scrollV = scrollPane.getVvalue();
        zoomGroup.setScaleX(scaleValue);
        zoomGroup.setScaleY(scaleValue);
        scrollPane.setHvalue(scrollH);
        scrollPane.setVvalue(scrollV);
    }


    @FXML
    protected void recenterPressed() {
        this.scrollPane.setHvalue(DEFAULT_HVALUE);
        this.scrollPane.setVvalue(DEFAULT_VVALUE);
    }

    @FXML
    protected void floorSelected() {
        System.out.println("Here");
        NodeFloor selectedFloor = floorSelector.getSelectionModel().getSelectedItem();
        loadFloor(selectedFloor);

        setShowNodes(showNodes);
        setShowEdges(showEdges);

        parent.onMapFloorChanged(selectedFloor);
    }
}
