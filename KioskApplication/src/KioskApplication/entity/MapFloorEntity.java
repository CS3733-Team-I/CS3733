package KioskApplication.entity;

import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class MapFloorEntity implements IMapEntity{

    //Key is the nodeID or edgeID
    private HashMap<String, Node> nodes;

    public MapFloorEntity() {
        nodes = new HashMap<>();
    }

    @Override
    public void addNode(Node n) {
        nodes.put(n.getNodeID(), n);
    }

    @Override
    public Node getNode(String s) {
        return nodes.get(s);
    }

    @Override
    public LinkedList<Node> getAllNodes() {
        LinkedList<Node> allNodes = new LinkedList<>();

        for (String name : nodes.keySet()) {
            allNodes.add(nodes.get(name));
        }

        return allNodes;
    }

    @Override
    public void removeNode(String s) {
        nodes.remove(s);
    }

    @Override
    //TODO: Given two nodes, returns the edge connecting them, or null if they aren't connected.
    public Edge getConnectingEdge(Node node1, Node node2){
        return null;
    }
}
