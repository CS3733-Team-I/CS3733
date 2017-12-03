package controller.map;

import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;
import entity.Path;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import utility.node.NodeSelectionType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class NodesEdgesView extends AnchorPane {
    private ObservableList<Node> nodesList;
    private ObservableList<Edge> edgesList;

    private ArrayList<NodeView> nodesViewList;
    private ArrayList<EdgeView> edgesViewList;

    private AnchorPane nodesView;
    private AnchorPane edgesView;

    // Parent
    Path currentPath;
    MapController parent;

    public NodesEdgesView(MapController parent) {
        nodesView = new AnchorPane();
        nodesViewList = new ArrayList<>();

        edgesView = new AnchorPane();
        edgesViewList = new ArrayList<>();

        this.getChildren().addAll(nodesView, edgesView);

        nodesList = FXCollections.observableArrayList();
        edgesList = FXCollections.observableArrayList();

        this.parent = parent;
        this.currentPath = null;

        // TODO handle nodes view and edges view add/remove with listeners
        nodesList.addListener((ListChangeListener<Node>) listener -> {
            while (listener.next()) {
                if (listener.wasAdded()) {
                    for (Node node : listener.getAddedSubList()) {
                        NodeView view = new NodeView(this, node, parent.isEditMode());
                        this.nodesViewList.add(view);
                        this.nodesView.getChildren().add(view);
                    }
                } else if (listener.wasRemoved()) {
                    for (Node node : listener.getRemoved()) {
                        NodeView view = getNodeView(node);
                        this.nodesViewList.remove(view);
                        this.nodesView.getChildren().remove(view);
                    }
                }
            }
        });

        edgesList.addListener((ListChangeListener<Edge>) listener -> {
            MapEntity map = MapEntity.getInstance();

            while (listener.next()) {
                if (listener.wasAdded()) {
                    for (Edge edge : listener.getAddedSubList()) {
                        Node node1 = map.getNode(edge.getNode1ID());
                        Node node2 = map.getNode(edge.getNode2ID());
                        EdgeView view = new EdgeView(edge, new Point2D(node1.getXcoord(), node1.getYcoord()),
                                                           new Point2D(node2.getXcoord(), node2.getYcoord()));
                        this.edgesViewList.add(view);
                        this.edgesView.getChildren().add(view);
                    }
                } else if (listener.wasRemoved()) {
                    for (Edge edge: listener.getRemoved()) {
                        EdgeView view = getEdgeView(edge);
                        this.edgesViewList.remove(view);
                        this.edgesView.getChildren().remove(view);
                    }
                }
            }
        });
    }

    /**
     * Get an existing NodeView based on a given Node
     * @param node the Node to search for
     * @return an NodeView corresponding to node, or null
     */
    private NodeView getNodeView(Node node) {
        for (NodeView view : nodesViewList) {
            if (view.node.equals(node)) return view;
        }
        return null;
    }

    /**
     * Get an existing EdgeView based on a given Edge
     * @param edge the Edge to search for
     * @return an EdgeView corresponding to edge, or null
     */
    private EdgeView getEdgeView(Edge edge) {
        for (EdgeView view : edgesViewList) {
            if (view.edge.equals(edge)) return view;
        }
        return null;
    }

    /**
     * Reload the nodes and edges views based on if nodes and edges are set to be shown
     */
    public void reloadDisplay() {
        setShowNodes(parent.areNodesVisible());
        setShowEdges(parent.areEdgesVisible());
    }

    /**
     * Remove all nodes and edges
     */
    public void clear() {
        nodesList.clear();
        edgesList.clear();
    }
    
    public void mapNodeClicked(Node n) {
        parent.nodeClicked(n);
    }

    public void mapEdgeClicked(Edge e) {
        parent.edgeClicked(e);
    }

    protected void drawNodesOnMap(List<Node> nodes) {
        nodesList.addAll(nodes);
    }

    protected void drawEdgesOnMap(List<Edge> edges) {
        edgesList.addAll(edges);
    }

    public void drawPath(Path path) {
        currentPath = path;

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
