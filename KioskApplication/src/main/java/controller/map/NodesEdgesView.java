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
import utility.node.NodeSelectionType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class NodesEdgesView extends AnchorPane {
    private ObservableList<Node> nodesList;
    private ObservableList<Edge> edgesList;

    private ArrayList<NodeView> nodesViewList;
    private ArrayList<EdgeView> edgesViewList;

    private AnchorPane nodesView;
    private AnchorPane edgesView;

    MapController parent;

    public NodesEdgesView(MapController parent) {
        nodesView = new AnchorPane();
        nodesViewList = new ArrayList<>();

        AnchorPane.setTopAnchor(nodesView, 0.0);
        AnchorPane.setLeftAnchor(nodesView, 0.0);
        AnchorPane.setBottomAnchor(nodesView, 0.0);
        AnchorPane.setRightAnchor(nodesView, 0.0);

        edgesView = new AnchorPane();
        edgesViewList = new ArrayList<>();

        AnchorPane.setTopAnchor(edgesView, 0.0);
        AnchorPane.setLeftAnchor(edgesView, 0.0);
        AnchorPane.setBottomAnchor(edgesView, 0.0);
        AnchorPane.setRightAnchor(edgesView, 0.0);

        this.getChildren().addAll(edgesView, nodesView);

        nodesList = FXCollections.observableArrayList();
        edgesList = FXCollections.observableArrayList();

        this.parent = parent;

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
     * Set a NodeView's selection type given a node
     * @param node the Node that corresponds to a NodeView
     * @param type the type to set the selection to
     */
    public void setNodeSelected(Node node, NodeSelectionType type) {
        NodeView view = getNodeView(node);
        if (view != null) view.setSelectionType(type);
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

    protected void drawNodesOnMap(List<Node> nodes) {
        nodesList.addAll(nodes);
    }

    protected void drawEdgesOnMap(List<Edge> edges) {
        edgesList.addAll(edges);
    }

    public void drawPath() {
        // TODO re-enable auto floor selection for path
        // parent.setFloorSelector(parent.getPath().getWaypoints().get(0).getFloor());
        parent.clearMap();

        for (LinkedList<Edge> segment : parent.getPath().getEdges()) {
            drawEdgesOnMap(segment);
        }

        drawNodesOnMap(parent.getPath().getWaypoints());
    }

    public void setShowNodes(boolean show) {
        nodesList.clear();

        if (show) {
            drawNodesOnMap(MapEntity.getInstance().getNodesOnFloor(parent.getCurrentFloor()));
        }
    }

    public void setShowEdges(boolean show) {
        edgesList.clear();

        if (show) {
            drawEdgesOnMap(MapEntity.getInstance().getEdgesOnFloor(parent.getCurrentFloor()));
        }
    }
}
