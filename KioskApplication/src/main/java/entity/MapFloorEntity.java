package entity;

import database.DatabaseController;
import database.objects.Node;

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
        DatabaseController.addNode(n);
    }

    @Override
    public void editNode(Node n) {
        nodes.put(n.getNodeID(), n);
        DatabaseController.updateNode(n);
    }

    @Override
    public Node getNode(String s) {
        // Load node from local data
        Node node = nodes.get(s);

        // If edge doesn't exist, attempt to load it from the database
        if (node == null) {
            node = DatabaseController.getNode(s);
            // Add edge to local data if found
            if (node != null) nodes.put(s, node);
        }

        return node;
    }

    @Override
    public LinkedList<Node> getAllNodes() {
        LinkedList<Node> allNodes = new LinkedList<>();

        for (Node node : nodes.values()) {
            allNodes.add(node);
        }

        return allNodes;
    }

    @Override
    public void removeNode(String s) {
        nodes.remove(s);
        DatabaseController.removeNode(new Node(s));
    }
}