package controller.map;

import database.connection.NotFoundException;
import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;
import entity.SystemSettings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;
import utility.node.NodeSelectionType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class NodesEdgesView extends AnchorPane {
    private ObservableList<Node> nodesList;
    private ObservableList<Edge> edgesList;

    private HashMap<Node, NodeView> nodeViewsMap;
    private HashMap<Edge, EdgeView> edgeViewsMap;

    private AnchorPane nodesView;
    private AnchorPane edgesView;

    MapController parent;

    public NodesEdgesView(MapController parent) {
        nodesView = new AnchorPane();
        nodesView.setPickOnBounds(false);
        nodeViewsMap = new HashMap<>();

        AnchorPane.setTopAnchor(nodesView, 0.0);
        AnchorPane.setLeftAnchor(nodesView, 0.0);
        AnchorPane.setBottomAnchor(nodesView, 0.0);
        AnchorPane.setRightAnchor(nodesView, 0.0);

        edgesView = new AnchorPane();
        nodesView.setPickOnBounds(false);
        edgeViewsMap = new HashMap<>();

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
                        view.setPickOnBounds(false);
                        if(SystemSettings.getInstance().getDefaultnode().getNodeID().equals(node.getNodeID()))
                            view.setImage(node);
                        this.nodeViewsMap.put(node, view);
                        this.nodesView.getChildren().add(view);
                    }
                } else if (listener.wasRemoved()) {
                    for (Node node : listener.getRemoved()) {
                        NodeView view = this.nodeViewsMap.get(node);
                        this.nodeViewsMap.remove(node);
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
                        try {
                            Node node1 = map.getNode(edge.getNode1ID());
                            Node node2 = map.getNode(edge.getNode2ID());
                            EdgeView view = new EdgeView(edge, new Point2D(node1.getXcoord(), node1.getYcoord()),
                                    new Point2D(node2.getXcoord(), node2.getYcoord()));

                            if (map.getEdgesOnFloor(parent.getCurrentFloor()).contains(edge))
                                view.setOpacity(0.95);
                            else
                                view.setOpacity(0.2);

                            this.edgeViewsMap.put(edge, view);
                            this.edgesView.getChildren().add(view);
                        }
                        catch(NotFoundException exception){
                            exception.printStackTrace();
                            //TODO: add actual handling
                        }
                    }
                } else if (listener.wasRemoved()) {
                    for (Edge edge: listener.getRemoved()) {
                        EdgeView view = this.edgeViewsMap.get(edge);
                        this.edgeViewsMap.remove(edge);
                        this.edgesView.getChildren().remove(view);
                    }
                }
            }
        });
    }
    /**
     * Set a NodeView's selection type given a node
     * @param node the Node that corresponds to a NodeView
     * @param type the type to set the selection to
     */
    public void setNodeSelected(Node node, NodeSelectionType type) {
        NodeView view = this.nodeViewsMap.get(node);
        if (view != null) view.setSelectionType(type);
    }

    /**
     * Reload the nodes and edges views based on if nodes and edges are set to be shown
     */
    public void reloadDisplay() {
        setShowNodes(parent.areNodesVisible());
        setShowEdges(parent.areEdgesVisible());
        if(parent.getPath() != null) {
            drawPath();
        }
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
        if (parent.getPath() != null) {
            // TODO re-enable auto floor selection for path
            // parent.setFloorSelector(parent.getPath().getWaypoints().get(0).getFloor());
            parent.clearMap();

            for (LinkedList<Edge> segment : parent.getPath().getEdges()) {
                drawEdgesOnMap(segment);
            }

            drawNodesOnMap(parent.getPath().getWaypoints());
        }
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
