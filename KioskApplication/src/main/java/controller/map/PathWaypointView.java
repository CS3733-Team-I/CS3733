package controller.map;

import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;
import entity.Path;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class PathWaypointView extends AnchorPane {

    private ObservableList<Edge> PathList;
    private ObservableList<MenuButton> waypointList;
    protected Path currentPath;

    private HashMap<Node, NodeView> wayPointViewsMap;
    private HashMap<Edge, PathView> pathViewsMap;

    private AnchorPane pathView;
    private AnchorPane wayPointView;

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

        waypointList.addListener(new ListChangeListener<javafx.scene.Node>() {
            @Override
            public void onChanged(Change<? extends javafx.scene.Node> c) {
                while(c.next()) {
                    if(c.wasRemoved()) {
                        for(javafx.scene.Node removedWaypoint : c.getRemoved()) {
                            getChildren().remove(removedWaypoint);
                        }
                    }
                    else if(c.wasAdded()) {
                        for(javafx.scene.Node addedWaypoint : c.getAddedSubList()) {
                            //draw the way point on Map
                            getChildren().add(addedWaypoint);
                        }
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
                        PathView pathview = new PathView(edge, new Point2D(node1.getXcoord(), node1.getYcoord()),
                                new Point2D(node2.getXcoord(), node2.getYcoord()));

                        if(map.getEdgesOnFloor(parent.getCurrentFloor()).contains(edge))
                            pathview.setOpacity(0.95);
                        else
                            pathview.setOpacity(0.2);
                        this.pathViewsMap.put(edge, pathview);
                        this.getChildren().add(pathview);
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

    public Path getPath() {
       return this.currentPath;
    }

    public void addWaypoint(Point2D location, Node node) {
        try {
            // put the pin and set it's info
            MenuButton wayPointObject = FXMLLoader.load(getClass().getResource("/view/WaypointView.fxml"));

            // TODO magic numbers
            wayPointObject.setTranslateX(location.getX() - 24);
            wayPointObject.setTranslateY(- 60 + location.getY() - 60);
            wayPointObject.setStyle("-fx-background-color: #ff1d13;");
            wayPointObject.setAccessibleText(node.getNodeID());
            wayPointObject.setAccessibleHelp("waypoint");

            TranslateTransition wayPointPutTransition = new TranslateTransition();
            wayPointPutTransition.setDuration(Duration.millis(400));
            wayPointPutTransition.setNode(wayPointObject);
            wayPointPutTransition.setToY(location.getY() - 60);

            //TODO handle waypoint option
            Tooltip nodeInfo = new Tooltip(node.getLongName());
            Tooltip.install(wayPointObject, nodeInfo);
            nodeInfo.setStyle("-fx-font-weight:bold; " +
                    "-fx-background-color: #ff1d13;" +
                    "-fx-font-size: 16pt; ");
            waypointList.add(wayPointObject);

            wayPointPutTransition.play();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeWaypoint(Node node) {
        Iterator<MenuButton> waypointIterator = waypointList.iterator();
        while(waypointIterator.hasNext()) {
            MenuButton removedWaypoint = waypointIterator.next();
            if(removedWaypoint.getAccessibleText().equals(node.getNodeID())) {
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
