package controller.map;

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
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class PathWaypointView extends AnchorPane {

    private ObservableList<Edge> PathList;
    private ObservableList<Node> waypointList;
    protected Path currentPath;

    private HashMap<Node, WaypointView> wayPointViewsMap;
    private HashMap<Edge, PathView> pathViewsMap;

    private AnchorPane pathView;
    private AnchorPane wayPointView;

    MapController parent;

    public PathWaypointView(MapController parent) throws NotFoundException{
        this.parent = parent;

        wayPointView = new AnchorPane();
        wayPointViewsMap = new HashMap<>();

        pathView = new AnchorPane();
        pathViewsMap = new HashMap<>();

        getChildren().addAll(wayPointView, pathView);

        waypointList = FXCollections.observableArrayList();
        PathList = FXCollections.observableArrayList();

        waypointList = FXCollections.observableArrayList();

        waypointList.addListener((ListChangeListener<Node>) listener -> {
            while (listener.next()) {
                if(listener.wasRemoved()) {
                    for (Node node : listener.getRemoved()) {
                        WaypointView view = this.wayPointViewsMap.get(node);
                        this.wayPointViewsMap.remove(node);
                        this.getChildren().remove(view);
                    }
                }
                if(listener.wasAdded()) {
                    for(Node addedNode : listener.getAddedSubList()) {
                        WaypointView waypointView = new WaypointView(this, addedNode);

                        this.wayPointViewsMap.put(addedNode, waypointView);

                        this.getChildren().add(waypointView);
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

                            getChildren().add(pathview);
                        }catch (NotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (listener.wasRemoved()) {
                    for (Edge edge: listener.getRemoved()) {
                        PathView view = this.pathViewsMap.get(edge);
                        this.pathViewsMap.remove(edge);
                        this.getChildren().remove(view);
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
        Iterator<javafx.scene.Node> pathPointerIterator = getChildren().iterator();
        //TODO make this faster by putting these in pathView
        while(pathPointerIterator.hasNext()) {
            javafx.scene.Node removedPathPointer = pathPointerIterator.next();
            if(removedPathPointer.getAccessibleHelp() != null) {
                if(removedPathPointer.getAccessibleHelp().equals("path pointer")) {
                    pathPointerIterator.remove();
                }
            }
        }
        pathView.getChildren().clear();
    }
    /**
     * Clear drawn waypoints
     */
    public void clearWaypoint() {
        this.waypointList.clear();
    }
    /**
     * Clear both waypoints and path
     */
    public void clearAll() {
        clearWaypoint();
        clearPath();
        this.getChildren().clear(); //TODO REPLACE THIS WITH CLEAR PATH
    }

    public void drawPath(Path path) {
        this.currentPath = path;

        for (LinkedList<Edge> segment : currentPath.getEdges()) {
            PathList.addAll(segment);
        }

        //TODO DATABASE ERROR HERE
        for(Node nodes : waypointList.subList(0, waypointList.size()-1)) {
            System.out.println("1. " + currentPath.getNodesInSegment(nodes));
        }


        javafx.scene.shape.Path jfxPath = new javafx.scene.shape.Path();

        jfxPath.setFill(Color.TRANSPARENT);
        MoveTo moveTo = new MoveTo(currentPath.getListOfAllNodes().get(0).getXcoord(), currentPath.getListOfAllNodes().get(0).getYcoord());
        jfxPath.getElements().add(moveTo);

        for(Node traversedNode : currentPath.getListOfAllNodes()) {
            LineTo lineTo = new LineTo(traversedNode.getXcoord(), traversedNode.getYcoord());
            jfxPath.getElements().add(lineTo);
        }
        getChildren().add(jfxPath);

        for(int i = 0; i < currentPath.getPathCost()/30; i++) {
            Circle circle = new Circle(10);
            circle.setFill(Color.BLUE);
            circle.setAccessibleHelp("path pointer");
            getChildren().add(circle);

            PathTransition navigationTransition = new PathTransition();
            navigationTransition.setNode(circle);
            navigationTransition.setDuration(Duration.seconds(currentPath.getPathCost()/30));
            navigationTransition.setPath(jfxPath);
            navigationTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
            navigationTransition.setAutoReverse(false);
            navigationTransition.setCycleCount(PathTransition.INDEFINITE);

            navigationTransition.playFrom(Duration.seconds(i));
        }
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
                this.getChildren().remove(removedWaypoint);
                waypointIterator.remove();
                break;
            }
        }
    }

    public void reloadDisplay() {
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
}
