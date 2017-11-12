package KioskApplication.entity;

import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class MapEntity implements IMapEntity {

    private HashMap<String, MapFloorEntity> floors;
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
            if(edges.get(key).getNode1().getNodeID().equals(n.getNodeID()) ||
               edges.get(key).getNode2().getNodeID().equals(n.getNodeID())) {
                returnList.add(edges.get(key));
            }
        }
        return returnList;
    }

    @Override
    public void addNode(Node n) {
        String f = n.getFloor();
        if(!floorExists(f)) addFloor(f);

        floors.get(f).addNode(n);
    }

    @Override
    public Node getNode(String s) {
        for (String floor : floors.keySet()) {
            if (floors.get(floor).getNode(s) != null) return floors.get(floor).getNode(s);
        }
        return null;
    }

    @Override
    public void removeNode(String s) {
        for (String floor : floors.keySet()) {
            floors.get(floor).removeNode(s);
        }
    }

    @Override
    public LinkedList<Node> getAllNodes() {
        //TODO: return all nodes in all floors in map
        return null;
    }

    private boolean floorExists(String floor) {
        return floors.keySet().contains(floor);
    }

    private void addFloor(String floor) {
        floors.put(floor, new MapFloorEntity());
    }
}