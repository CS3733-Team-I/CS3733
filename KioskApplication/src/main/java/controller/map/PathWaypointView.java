package controller.map;

import com.jfoenix.controls.JFXButton;
import database.connection.NotFoundException;
import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;
import entity.Path;
import javafx.animation.PathTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;
import sun.awt.image.ImageWatched;
import utility.ResourceManager;
import utility.node.NodeFloor;
import utility.node.NodeType;

import javax.swing.text.html.ImageView;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class PathWaypointView extends AnchorPane {

    private ObservableList<Edge> PathList;
    private ObservableList<Node> waypointList;
    protected Path currentPath;

    private HashMap<Node, WaypointView> wayPointViewsMap;
    private HashMap<Edge, PathView> pathViewsMap;

    private AnchorPane pathView;
    private AnchorPane wayPointView;

    private javafx.scene.image.ImageView upView;
    private javafx.scene.image.ImageView downView;

    MapController parent;

    public PathWaypointView(MapController parent) throws NotFoundException{
        wayPointView = new AnchorPane();
        wayPointViewsMap = new HashMap<>();
        wayPointView.setPickOnBounds(false);

        AnchorPane.setTopAnchor(wayPointView, 0.0);
        AnchorPane.setLeftAnchor(wayPointView, 0.0);
        AnchorPane.setBottomAnchor(wayPointView, 0.0);
        AnchorPane.setRightAnchor(wayPointView, 0.0);

        pathView = new AnchorPane();
        pathViewsMap = new HashMap<>();
        pathView.setPickOnBounds(false);

        AnchorPane.setTopAnchor(pathView, 0.0);
        AnchorPane.setLeftAnchor(pathView, 0.0);
        AnchorPane.setBottomAnchor(pathView, 0.0);
        AnchorPane.setRightAnchor(pathView, 0.0);

//        wayPointView.prefWidthProperty().bind(this.widthProperty());
//        wayPointView.prefHeightProperty().bind(this.heightProperty());
//
//        pathView.prefWidthProperty().bind(this.widthProperty());
//        pathView.prefHeightProperty().bind(this.heightProperty());

        this.getChildren().addAll(wayPointView, pathView);

        wayPointView.setMouseTransparent(true);
        pathView.setMouseTransparent(false);

        waypointList = FXCollections.observableArrayList();
        PathList = FXCollections.observableArrayList();

        waypointList = FXCollections.observableArrayList();

        this.parent = parent;

        Image upIcon = ResourceManager.getInstance().getImage("/images/icons/arrow-up.png");
        upView = new javafx.scene.image.ImageView(upIcon);
        upView.setFitHeight(48);
        upView.setFitWidth(48);
        upView.setStyle("-fx-background-color: #00589F;");

        Image downIcon = ResourceManager.getInstance().getImage("/images/icons/arrow-down.png");
        downView = new javafx.scene.image.ImageView(downIcon);
        downView.setFitHeight(48);
        downView.setFitWidth(48);
        downView.setStyle("-fx-background-color: #00589F;");

        waypointList.addListener((ListChangeListener<Node>) listener -> {
            while (listener.next()) {
                if(listener.wasRemoved()) {
                    for (Node node : listener.getRemoved()) {
                        WaypointView view = this.wayPointViewsMap.get(node);
                        this.wayPointViewsMap.remove(node);
                        this.wayPointView.getChildren().remove(view);
                    }
                }
                if(listener.wasAdded()) {
                    for(Node addedNode : listener.getAddedSubList()) {
                        WaypointView waypointView = new WaypointView(this, addedNode, waypointList.indexOf(addedNode));
                        this.wayPointViewsMap.put(addedNode, waypointView);

                        this.wayPointView.getChildren().add(waypointView);
                        waypointView.playWaypointPutTransition();
                    }
                }
            }
        });

        PathList.addListener((ListChangeListener<Edge>) listener -> {
            MapEntity map = MapEntity.getInstance();

            while (listener.next()) {
                if (listener.wasAdded()) {
                    for (Edge edge : listener.getAddedSubList()) {
                        try{
                            Node node1 = map.getNode(edge.getNode1ID());
                            Node node2 = map.getNode(edge.getNode2ID());
                            PathView pathview = new PathView(edge, new Point2D(node1.getXcoord(), node1.getYcoord()),
                                    new Point2D(node2.getXcoord(), node2.getYcoord()));
                            if(map.getEdgesOnFloor(parent.getCurrentFloor()).contains(edge))
                                pathview.setOpacity(0.95);
                            else
                                pathview.setOpacity(0.2);
                            this.pathViewsMap.put(edge, pathview);

                            this.pathView.getChildren().add(pathview);
                        }catch (NotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (listener.wasRemoved()) {
                    for (Edge edge: listener.getRemoved()) {
                        PathView view = this.pathViewsMap.get(edge);
                        this.pathViewsMap.remove(edge);
                        this.pathView.getChildren().remove(view);
                    }
                }
            }
        });
    }

    /**
     * draw a path
     */
    //TODO rename this
    public void setPath(Path path) {
        this.currentPath = path;
    }

    /**
     * Clear drawn path
     */
    public void clearPath() {
        this.currentPath = null;
        pathView.getChildren().clear();
    }
    /**
     * Clear drawn waypoints
     */
    public void clearWaypoint() {
        this.waypointList.clear();
        wayPointView.getChildren().clear();
    }
    /**
     * Clear both waypoints and path
     */
    public void clearAll() {
        clearWaypoint();
        clearPath();
    }

    public void drawPath(Path path) {
        JFXButton switchFloor = new JFXButton();

        this.currentPath = path;

        for (LinkedList<Edge> segment : currentPath.getEdges()) {
            PathList.addAll(segment);
        }

        for(int i = 0; i < waypointList.size()-1; i ++) {
            NodeFloor a = parent.getCurrentFloor();
            LinkedList<Node> segmentNodes = currentPath.getListOfNodesSegmentOnFloor(currentPath.getEdges().get(i), waypointList.get(i), parent.getCurrentFloor());
            System.out.println(segmentNodes);

            javafx.scene.shape.Path jfxPath = new javafx.scene.shape.Path();
            jfxPath.setFill(Color.TRANSPARENT);
            MoveTo moveTo = new MoveTo(segmentNodes.get(0).getXcoord(), segmentNodes.get(0).getYcoord());
            jfxPath.getElements().add(moveTo);

            for(Node traversedNode : segmentNodes) {
                LineTo lineTo = new LineTo(traversedNode.getXcoord(), traversedNode.getYcoord());
                jfxPath.getElements().add(lineTo);

                if(traversedNode.getNodeType() == NodeType.ELEV || traversedNode.getNodeType() == NodeType.STAI) {
                    switchFloor = new JFXButton();
                    //going up
                    final NodeFloor targetFloor = waypointList.get(i+1).getFloor();
                    final NodeFloor sourceFloor = waypointList.get(i).getFloor();
                    if(targetFloor.toInt() > sourceFloor.toInt()) {
                        switchFloor.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                parent.setFloorSelector(targetFloor);
                            }
                        });
                        switchFloor.setLayoutX(traversedNode.getXcoord()-48);
                        switchFloor.setLayoutY(traversedNode.getYcoord());
                        switchFloor.setPrefHeight(48);
                        switchFloor.setPrefWidth(48);
                        switchFloor.setStyle("-fx-background-color: #01499f;");
                        switchFloor.setGraphic(upView);
                    }
                    //going down
                    else if(targetFloor.toInt() < sourceFloor.toInt()) {
                        switchFloor.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                parent.setFloorSelector(targetFloor);
                            }
                        });
                        switchFloor.setLayoutX(traversedNode.getXcoord()-48);
                        switchFloor.setLayoutY(traversedNode.getYcoord());
                        switchFloor.setPrefHeight(48);
                        switchFloor.setPrefWidth(48);
                        switchFloor.setStyle("-fx-background-color: #00589F;");
                        switchFloor.setGraphic(downView);
                    }
                }
            }
            this.pathView.getChildren().add(jfxPath);

            Color colorForPointers = Color.color(Math.random(), Math.random(), Math.random());
            for(int j = 0; j < currentPath.getPathCost()/20; j++) {
                Circle circle = new Circle(10);
                circle.setFill(colorForPointers);
                circle.setAccessibleHelp("path pointer");
                this.pathView.getChildren().add(circle);

                PathTransition navigationTransition = new PathTransition();
                navigationTransition.setNode(circle);
                navigationTransition.setDuration(Duration.seconds(currentPath.getPathCost()/20));
                navigationTransition.setPath(jfxPath);
                navigationTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
                navigationTransition.setAutoReverse(false);
                navigationTransition.setCycleCount(PathTransition.INDEFINITE);

                navigationTransition.playFrom(Duration.seconds(j));
            }
        }
        this.pathView.getChildren().add(switchFloor);
    }

    public Path getPath() {
       return this.currentPath;
    }

    public void addWaypoint(Node node) {
        waypointList.add(node);
    }

    public void removeWaypoint(Node node) {
        Iterator<Node> waypointIterator = waypointList.iterator();
        while(waypointIterator.hasNext()) {
            Node removedWaypoint = waypointIterator.next();
            if(removedWaypoint.getNodeID().equals(node.getNodeID())) {
                this.wayPointView.getChildren().remove(removedWaypoint);
                waypointIterator.remove();
                break;
            }
        }
    }

    public void reloadDisplay() {
        pathView.getChildren().clear();
        if(currentPath != null) {
            drawPath(currentPath);
        }
        for(Node node : waypointList){
            if(!MapEntity.getInstance().isNodeOnFloor(node, parent.getCurrentFloor())) {
                this.wayPointViewsMap.get(node).setVisible(false);
            }
            else {
                this.wayPointViewsMap.get(node).setVisible(true);
            }
        }
    }

    /**
     * swap the waypoints at targeted indexes
     */
    public void swapWaypoint(int index1, int index2) {
        Node temp1 = waypointList.get(index1);
        Node temp2 = waypointList.get(index2);
        WaypointView tempWaypointView = wayPointViewsMap.get(temp2);

        wayPointViewsMap.get(waypointList.get(index1)).setWaypointCount(index2);
        wayPointViewsMap.get(waypointList.get(index2)).setWaypointCount(index1);

//        waypointList.remove(index1);
//        waypointList.remove(index2);

        waypointList.set(index1, temp2);
        waypointList.set(index2, temp1);

        wayPointViewsMap.put(temp2, tempWaypointView);
    }

    /**
     * give the starting waypoint in path(first one)
     */
    public Node getStartWaypoint() {
        if(this.waypointList.size() != 0) {
            return waypointList.get(0);
        }
        else return null;
    }

    /**
     * return true if there's path displaying
     */
    public boolean isPathShowing() {
        return this.currentPath != null;
    }
}
