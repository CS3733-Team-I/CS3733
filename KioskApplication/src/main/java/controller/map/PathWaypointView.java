package controller.map;

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
import javafx.scene.shape.Circle;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Polyline;
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

    private javafx.scene.shape.Path jfxPath;

    MapController parent;

    public PathWaypointView(MapController parent){
        this.parent = parent;

        wayPointView = new AnchorPane();
        wayPointViewsMap = new HashMap<>();

        pathView = new AnchorPane();
        pathViewsMap = new HashMap<>();

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
                        Node node1 = map.getNode(edge.getNode1ID());
                        Node node2 = map.getNode(edge.getNode2ID());
//                        MoveTo moveTo1 = new MoveTo(node1.getXcoord(), node1.getYcoord());
//                        MoveTo moveTo2 = new MoveTo(node2.getXcoord(), node2.getYcoord());
//
//                        if(jfxPath == null) {
//                            this.jfxPath = new javafx.scene.shape.Path(moveTo1, moveTo2);
//                        }
//                        else {
//                            jfxPath.getElements().addAll(moveTo1, moveTo2);
//                        }
//                        jfxPath.setStyle("-fx-background-color: #0026ff;");
//                        jfxPath.setStrokeWidth(20);

                        PathView pathview = new PathView(edge, new Point2D(node1.getXcoord(), node1.getYcoord()),
                                new Point2D(node2.getXcoord(), node2.getYcoord()));

                        if(map.getEdgesOnFloor(parent.getCurrentFloor()).contains(edge))
                            pathview.setOpacity(0.95);
                        else
                            pathview.setOpacity(0.2);
                        this.pathViewsMap.put(edge, pathview);
//                        if(!getChildren().contains(jfxPath)) {
//                            this.getChildren().add(jfxPath);
//                        }
                        getChildren().add(pathview);
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
    }

    public Polyline getPathPolyline() {
        Polyline polyline = new Polyline();
        for(PathView pathView : pathViewsMap.values()) {
            if(!polyline.contains(pathView.getStart())) {
                polyline.getPoints().addAll(new Double[] {
                        pathView.getStart().getX(), pathView.getStart().getY(),
                });
            }
            if(!polyline.contains(pathView.getEnd())) {
                polyline.getPoints().addAll(new Double[] {
                        pathView.getEnd().getX(), pathView.getEnd().getY(),
                });
            }

        }
        polyline.setStyle("-fx-background-color: #ffffff;"+
        "-fx-stroke-width: 10px;");
        return polyline;
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
    }
}
