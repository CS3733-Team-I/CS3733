package controller.map;

import com.jfoenix.controls.JFXButton;
import controller.PathfindingSidebarController;
import database.connection.NotFoundException;
import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;
import entity.Path;
import javafx.animation.PathTransition;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.util.Duration;
import utility.ResourceManager;
import utility.node.NodeFloor;
import utility.node.NodeType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class PathWaypointView extends AnchorPane {

    private ObservableList<Edge> PathList;
    private ObservableList<Node> waypointList;
    protected Path currentPath;

    private HashMap<Node, WaypointView> wayPointViewsMap;

    private AnchorPane pathView;
    private AnchorPane wayPointView;

    private javafx.scene.image.ImageView upView;
    private javafx.scene.image.ImageView downView;

    private ArrayList<Color> segmentColorList;

    MapController parent;
    PathfindingSidebarController sidebarController;

    public PathWaypointView(MapController parent) throws NotFoundException {
        this.setPickOnBounds(false);
        wayPointView = new AnchorPane();
        wayPointViewsMap = new HashMap<>();
        wayPointView.setPickOnBounds(false);

        AnchorPane.setTopAnchor(wayPointView, 0.0);
        AnchorPane.setLeftAnchor(wayPointView, 0.0);
        AnchorPane.setBottomAnchor(wayPointView, 0.0);
        AnchorPane.setRightAnchor(wayPointView, 0.0);

        pathView = new AnchorPane();
        pathView.setPickOnBounds(false);

        AnchorPane.setTopAnchor(pathView, 0.0);
        AnchorPane.setLeftAnchor(pathView, 0.0);
        AnchorPane.setBottomAnchor(pathView, 0.0);
        AnchorPane.setRightAnchor(pathView, 0.0);

        this.getChildren().addAll(pathView, wayPointView);

        waypointList = FXCollections.observableArrayList();
        PathList = FXCollections.observableArrayList();

        waypointList = FXCollections.observableArrayList();

        this.parent = parent;

        segmentColorList = new ArrayList<>();

        Image arrowButtonIcon = ResourceManager.getInstance().getImage("/images/icons/arrow-button.png");
        upView = new javafx.scene.image.ImageView(arrowButtonIcon);
        upView.setFitHeight(48);
        upView.setFitWidth(48);

        downView = new javafx.scene.image.ImageView(arrowButtonIcon);
        downView.setRotate(180);
        downView.setFitHeight(48);
        downView.setFitWidth(48);

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
        JFXButton switchFloor = null;
        segmentColorList.clear();

        this.currentPath = path;

        for (LinkedList<Edge> segment : currentPath.getEdges()) {
            PathList.addAll(segment);
        }

        for(int i = 0; i < waypointList.size()-1; i ++) {
            NodeFloor a = parent.getCurrentFloor();
            LinkedList<Node> segmentNodes = currentPath.getListOfNodesSegmentOnFloor(currentPath.getEdges().get(i), waypointList.get(i), parent.getCurrentFloor());

            javafx.scene.shape.Path jfxPath = new javafx.scene.shape.Path();
            jfxPath.setFill(Color.TRANSPARENT);
            MoveTo moveTo = new MoveTo(segmentNodes.get(0).getXcoord(), segmentNodes.get(0).getYcoord());
            jfxPath.getElements().add(moveTo);

            for(Node traversedNode : segmentNodes) {
                LineTo lineTo = new LineTo(traversedNode.getXcoord(), traversedNode.getYcoord());
                jfxPath.getElements().add(lineTo);

                NodeFloor targetFloor = waypointList.get(i+1).getFloor();
                if(traversedNode.getNodeType() == NodeType.ELEV || traversedNode.getNodeType() == NodeType.STAI) {
                    switchFloor = new JFXButton();
                    switchFloor.setOnAction(event -> parent.setFloorSelector(targetFloor));
                    switchFloor.setLayoutX(traversedNode.getXcoord()-48);
                    switchFloor.setLayoutY(traversedNode.getYcoord());
                    switchFloor.setPrefHeight(48);
                    switchFloor.setPrefWidth(48);

                    if (traversedNode.getFloor().toInt() > targetFloor.toInt()) {
                        switchFloor.setGraphic(upView);
                    } else {
                        switchFloor.setGraphic(downView);
                    }
                }
            }

            this.pathView.getChildren().add(jfxPath);

            Color colorForPointers = Color.color(Math.random() * 0.75, Math.random() * 0.75, 0.8);
            segmentColorList.add(colorForPointers);
            for(int k = 0; k < currentPath.getPathCost()/20; k++) {
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

                navigationTransition.playFrom(Duration.seconds(k));
            }
        }

        if (switchFloor != null) this.pathView.getChildren().add(switchFloor);
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

        waypointList.set(index1, temp2);
        waypointList.set(index2, temp1);

        wayPointViewsMap.put(temp2, tempWaypointView);

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

    /**
     * return the text direction for targeted index
     */
    public LinkedList<String> getDirectionForWaypointIndex(int i) {
        if(currentPath != null && i < currentPath.getDirectionsList().size()) {
            return currentPath.getDirectionsList().get(i);
        }
        else return null;
    }

    public ArrayList<Color> getsSegmentColorList() {
        return segmentColorList;
    }

    public void setSidebarController(PathfindingSidebarController sidebarController) {
        this.sidebarController = sidebarController;
    }
}
