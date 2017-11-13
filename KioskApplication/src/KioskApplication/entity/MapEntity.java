package KioskApplication.entity;

import KioskApplication.database.DatabaseController;
import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;
import KioskApplication.utility.NodeFloor;

import java.util.ArrayList;
import java.util.HashMap;

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
            Node thisNode = floors.get(floor).getNode(s);
            if (thisNode != null) return thisNode;
        }
        return null;
    }

    @Override
    public ArrayList<Node> getAllNodes() {
        ArrayList<Node> allNodes = new ArrayList<>();

        for (NodeFloor floor : floors.keySet()) {
            allNodes.addAll(floors.get(floor).getAllNodes());
        }

        return allNodes;
    }

    @Override
    public void removeNode(String s) {
        for (NodeFloor floor : floors.keySet()) {
            MapFloorEntity floorEntity = floors.get(floor);
            if (floorEntity.getNode(s) != null)
                floorEntity.removeNode(s);
        }
    }

    public void addEdge(Edge e) {
        edges.put(e.getEdgeID(),e);
        DatabaseController.addEdge(e);
    }

    public Edge getEdge(String s) {
        // Load edge from local data
        Edge edge = edges.get(s);

        // If edge doesn't exist, attempt to load it from the database
        if (edge == null) {
            edge = DatabaseController.getEdge(s);
            // Add edge to local data if found
            if (edge != null) edges.put(s, edge);
        }

        return edge;
    }

    // TODO pass edge as param instead of string
    public void removeEdge(String s) {
        edges.remove(s);
        DatabaseController.removeEdge(new Edge(s, "", ""));
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
}