package controller;

import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;
import entity.Path;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import utility.nodeDisplay.NodeDisplay;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class NodesEdgesView extends AnchorPane {
    private ObservableList<Node> databaseNodeObjectList;
    private ObservableList<database.objects.Edge> databaseEdgeObjectList;

    private ObservableList<Circle> nodeObjectList;
    private ObservableList<Line> edgeObjectList;

    private ObservableList<database.objects.Node> observableHighlightedSelectedNodes;
    private ObservableList<database.objects.Node> observableHighlightedChangedNodes;
    private ObservableList<database.objects.Node> observableHighlightedNewNodes;
    private ObservableList<database.objects.Edge> observableHighlightedEdges;

    // Parent
    MapController p;
    Path path;

    public NodesEdgesView(MapController parent, Path path) {
        databaseNodeObjectList = FXCollections.<database.objects.Node>observableArrayList();
        databaseEdgeObjectList = FXCollections.<database.objects.Edge>observableArrayList();

        nodeObjectList = FXCollections.<Circle>observableArrayList();
        edgeObjectList = FXCollections.<Line>observableArrayList();

        observableHighlightedSelectedNodes = FXCollections.<database.objects.Node>observableArrayList();
        observableHighlightedChangedNodes = FXCollections.<database.objects.Node>observableArrayList();
        observableHighlightedNewNodes = FXCollections.<database.objects.Node>observableArrayList();
        observableHighlightedEdges = FXCollections.<database.objects.Edge>observableArrayList();

        this.p = parent;
        this.path = path;
    }

    //TODO put this into nodeobjectlist permuted
    public void highlightNode(database.objects.Node targetNode, NodeDisplay nodeDisplay) {
        for (Circle nodeO : nodeObjectList) {
            if (targetNode.getXyz().equals((nodeO.getAccessibleText()))) {
                this.getChildren().remove(nodeO);
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
                this.getChildren().add(nodeO);
                return;
            }
        }
        //in case of a new node
        if (nodeDisplay == NodeDisplay.NEW) {
            databaseNodeObjectList.add(targetNode);
            highlightNode(targetNode, NodeDisplay.NEW);
        }
    }

    public void reloadDisplay() {
        setShowNodes(p.getShowNodesBox());
        setShowEdges(p.getShowEdgesBox());
    }

    protected void drawNodesOnMap(List<Node> nodes) {
        databaseNodeObjectList.addAll(nodes);
    }

    protected void undrawNodeOnMap(database.objects.Node node) {
        Iterator<Node> undrawNodeObjectIterator = databaseNodeObjectList.iterator();
        while (undrawNodeObjectIterator.hasNext()) {
            database.objects.Node undrawnNode = undrawNodeObjectIterator.next();
            if (undrawnNode.getXyz().equals(node.getXyz())) {
                undrawNodeObjectIterator.remove();
                break;
            }
        }
    }

    public void mapEdgeClicked(Edge e) {
        p.mapEdgeClicked(e);
    }

    protected void drawEdgesOnMap(List<Edge> edges) {
        databaseEdgeObjectList.addAll(edges);
    }

    public void drawPath() {
        // Change to floor of the starting node
        p.setFloorSelector(this.path.getWaypoints().get(0).getFloor());

        p.clearMap();
        for (LinkedList<Edge> segment : this.path.getEdges()) {
            drawEdgesOnMap(segment);
        }

        drawNodesOnMap(this.path.getWaypoints());
    }

    public void setShowNodes(boolean show) {
        if (show) {
            drawNodesOnMap(MapEntity.getInstance().getNodesOnFloor(p.getCurrentFloor()));
        } else {
            databaseNodeObjectList.clear();
        }
    }

    public void setShowEdges(boolean show) {
        if (show) {
            drawEdgesOnMap(MapEntity.getInstance().getEdgesOnFloor(p.getCurrentFloor()));
        } else {
            databaseEdgeObjectList.clear();
        }
    }
}
