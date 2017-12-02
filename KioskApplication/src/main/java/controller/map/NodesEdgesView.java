package controller.map;

import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;
import entity.Path;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import utility.node.NodeSelectionType;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class NodesEdgesView extends AnchorPane {
    private ObservableList<Node> nodesList;
    private ObservableList<database.objects.Edge> edgesList;

    private ObservableList<database.objects.Node> observableHighlightedSelectedNodes;
    private ObservableList<database.objects.Node> observableHighlightedChangedNodes;

    private ObservableList<database.objects.Node> observableHighlightedNewNodes;
    private ObservableList<database.objects.Edge> observableHighlightedEdges;

    private AnchorPane nodesView;
    private AnchorPane edgesView;

    // Parent
    Path currentPath;
    MapController parent;

    public NodesEdgesView(MapController parent) {
        nodesView = new AnchorPane();
        edgesView = new AnchorPane();

        nodesList = FXCollections.observableArrayList();
        edgesList = FXCollections.observableArrayList();

        observableHighlightedSelectedNodes = FXCollections.observableArrayList();
        observableHighlightedChangedNodes = FXCollections.observableArrayList();
        observableHighlightedNewNodes = FXCollections.observableArrayList();
        observableHighlightedEdges = FXCollections.observableArrayList();

        this.parent = parent;
        this.currentPath = null;

        // Add various listeners
        nodesList.addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(Change<? extends Node> c) {
                while (c.next()) {
                    if (c.wasRemoved()) {
                        for (database.objects.Node removedDatabaseNode : c.getRemoved()) {
                            // TODO handle nodes being removed from list and display
                        }
                    } else {
                        for (database.objects.Node addedDatabaseNode : c.getAddedSubList()) {
                            Circle nodeView = new Circle(addedDatabaseNode.getXcoord(), addedDatabaseNode.getYcoord(), 14, Color.GRAY);
                            nodeView.setStroke(Color.BLACK);
                            nodeView.setStrokeWidth(3);
                            nodeView.setMouseTransparent(false);
                            nodeView.setOnMouseClicked(mouseEvent -> parent.nodeClicked(addedDatabaseNode));
                            nodeView.setPickOnBounds(false);


                            nodesView.getChildren().add(nodeView);
                        }
                    }
                }
            }
        });

        edgesList.addListener(new ListChangeListener<Edge>() {
            @Override
            public void onChanged(Change<? extends Edge> c) {
                while (c.next()) {
                    if (c.wasRemoved()) {
                        for (database.objects.Edge removedDatabaseEdge : c.getRemoved()) {
                            // TODO handle edges being removed from list and display
                        }
                    } else {
                        for (database.objects.Edge addedDatabaseEdge : c.getAddedSubList()) {
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

                            edgesView.getChildren().add(edgeView);

                            if (mapEntity.getEdgesOnFloor(parent.getCurrentFloor()).contains(addedDatabaseEdge))
                                edgeView.setOpacity(0.95);
                            else
                                edgeView.setOpacity(0.2);
                        }
                    }
                }
            }
        });
    }

    //TODO put this into nodeobjectlist permuted
    public void highlightNode(database.objects.Node targetNode, NodeSelectionType nodeSelectionType) {
        for (Circle nodeO : nodeObjectList) {
            if (targetNode.getXyz().equals((nodeO.getAccessibleText()))) {
                this.getChildren().remove(nodeO);
                switch (nodeSelectionType) {
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
                this.getChildren().add(nodeO);
                return;
            }
        }
        //in case of a new node
        if (nodeSelectionType == NodeSelectionType.NEW) {
            nodesList.add(targetNode);
            highlightNode(targetNode, NodeSelectionType.NEW);
        }
    }

    public void reloadDisplay() {
        setShowNodes(parent.getShowNodesBox());
        setShowEdges(parent.getShowEdgesBox());
    }

    protected void drawNodesOnMap(List<Node> nodes) {
        nodesList.addAll(nodes);
    }

    protected void undrawNodeOnMap(database.objects.Node node) {
        Iterator<Node> undrawNodeObjectIterator = nodesList.iterator();
        while (undrawNodeObjectIterator.hasNext()) {
            database.objects.Node undrawnNode = undrawNodeObjectIterator.next();
            if (undrawnNode.getXyz().equals(node.getXyz())) {
                undrawNodeObjectIterator.remove();
                break;
            }
        }
    }

    public void mapNodeClicked(Node n) {
        parent.nodeClicked(n);
    }

    public void mapEdgeClicked(Edge e) {
        parent.edgeClicked(e);
    }

    protected void drawEdgesOnMap(List<Edge> edges) {
        edgesList.addAll(edges);
    }

    public void drawPath() {
        // Change to floor of the starting node
        parent.setFloorSelector(currentPath.getWaypoints().get(0).getFloor());

        parent.clearMap();
        for (LinkedList<Edge> segment : currentPath.getEdges()) {
            drawEdgesOnMap(segment);
        }

        drawNodesOnMap(currentPath.getWaypoints());
    }

    public void setShowNodes(boolean show) {
        if (show) {
            drawNodesOnMap(MapEntity.getInstance().getNodesOnFloor(parent.getCurrentFloor()));
        } else {
            nodesList.clear();
        }
    }

    public void setShowEdges(boolean show) {
        if (show) {
            drawEdgesOnMap(MapEntity.getInstance().getEdgesOnFloor(parent.getCurrentFloor()));
        } else {
            edgesList.clear();
        }
    }
}
