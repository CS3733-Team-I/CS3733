package KioskApplication.entity;

import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;
import KioskApplication.utility.NodeFloor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class MapEntity implements IMapEntity {

    private HashMap<NodeFloor,MapFloorEntity> floors;
    private HashMap<String, Edge> edges;

    private static MapEntity instance = null;

    protected MapEntity() {
        floors = new HashMap<>();
        edges = new HashMap<>();
    }

    public static MapEntity getInstance() {
        if (instance == null) instance = new MapEntity();
        return instance;
    }

    @Override
    public void addNode(Node n) {
        NodeFloor f = n.getFloor();
        if(!floorExists(f)) addFloor(f);

        floors.get(f).addNode(n);
    }

    @Override
    public Node getNode(String s) {
        for (NodeFloor floor : floors.keySet()) {
            if (floors.get(floor).getNode(s) != null) return floors.get(floor).getNode(s);
        }
        return null;
    }

    @Override
    public LinkedList<Node> getAllNodes() {
        LinkedList<Node> allNodes = new LinkedList<>();

        for (NodeFloor floor : floors.keySet()) {
            allNodes.addAll(floors.get(floor).getAllNodes());
        }

        return allNodes;
    }

    @Override
    public void removeNode(String s) {
        for (NodeFloor floor : floors.keySet()) {
            floors.get(floor).removeNode(s);
        }
    }

    public void addEdge(Edge e) {
        edges.put(e.getEdgeID(),e);
    }

    public Edge getEdge(String s) {
        return edges.get(s);
    }

    public void removeEdge(String s) {
        edges.remove(s);
    }

    public ArrayList<Edge> getEdges(Node n) {
        ArrayList<Edge> returnList = new ArrayList<>();

        for(String key : edges.keySet()) {
            if(edges.get(key).getNode1ID().equals(n.getNodeID()) ||
                    edges.get(key).getNode2ID().equals(n.getNodeID())) {
                returnList.add(edges.get(key));
            }
        }
        return returnList;
    }

    private boolean floorExists(NodeFloor floor) {
        return floors.keySet().contains(floor);
    }

    private void addFloor(NodeFloor floor) {
        floors.put(floor, new MapFloorEntity());
    }

    //TODO: Given two nodes, returns the edge connecting them, or null if they aren't connected.
    public Edge getConnectingEdge(Node node1, Node node2){
        ArrayList<Edge> node1Edges = getEdges(node1);
        for(Edge edge: node1Edges){
            if(edge.getNode1ID().equals(node2.getNodeID()) || edge.getNode2ID().equals(node2.getNodeID()))
                return edge;
        }
        return null;
    }

    //Given a node, return a list of all adjacent nodes.
    public LinkedList<Node> getConnectedNodes(Node node){
        ArrayList<Edge> edges = this.getEdges(node);
        LinkedList<Node> connectedNodes = new LinkedList<>();
        for(Edge edge: edges){
            if(edge.getNode1ID().equals(node.getNodeID()))
                connectedNodes.add(getNode(edge.getNode1ID()));
            else if(edge.getNode2ID().equals(node.getNodeID()))
                connectedNodes.add(getNode(edge.getNode2ID()));
        }
        return connectedNodes;
    }
}