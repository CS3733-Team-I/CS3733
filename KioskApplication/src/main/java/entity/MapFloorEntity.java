package entity;

import database.DatabaseController;
import database.objects.Node;
import database.utility.DatabaseException;
import javafx.scene.control.Alert;

import java.util.HashMap;
import java.util.LinkedList;

public class MapFloorEntity implements IMapEntity {

    //Key is the nodeID or edgeID
    private HashMap<String, Node> nodes;

    private DatabaseController dbController;

    public MapFloorEntity() {
        nodes = new HashMap<>();

        dbController = DatabaseController.getInstance();
    }

    /**
     * Inserts a node directly into the floor, subverting the database
     * @param node the node to insert
     */
    public void insertNode(Node node) {
        nodes.put(node.getNodeID(), node);
    }

    @Override
    public void addNode(Node n) {
        try {
            dbController.addNode(n);
            nodes.put(n.getNodeID(), n);
        } catch (DatabaseException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Node Error");
            alert.setHeaderText("Error adding node");
            alert.setContentText(ex.toString());
            alert.showAndWait();
        }
    }

    @Override
    public void editNode(Node n) {
        try {
            dbController.updateNode(n);
            nodes.put(n.getNodeID(), n);
        } catch (DatabaseException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Node Error");
            alert.setHeaderText("Error updating node");
            alert.setContentText(ex.toString());
            alert.showAndWait();
        }
    }

    @Override
    public Node getNode(String s) {
        // Load node from local data
        Node node = nodes.get(s);

        // If edge doesn't exist, attempt to load it from the database
        if (node == null) {
            try {
                node = dbController.getNode(s);
                // Add edge to local data if found
                if (node != null) nodes.put(s, node);
            } catch (DatabaseException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Node Error");
                alert.setHeaderText("Error getting node: " + s);
                alert.setContentText(ex.toString());
                alert.showAndWait();
            }
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
    public void removeNode(Node node) {
        try {
            dbController.removeNode(node);
            nodes.remove(node.getNodeID());
        } catch (DatabaseException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Node Error");
            alert.setHeaderText("Error removing node");
            alert.setContentText(ex.toString());
            alert.showAndWait();
        }
    }
}
