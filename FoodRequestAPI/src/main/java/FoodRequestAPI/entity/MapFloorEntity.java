package FoodRequestAPI.entity;

import FoodRequestAPI.database.DatabaseController;
import FoodRequestAPI.database.connection.NotFoundException;
import FoodRequestAPI.database.objects.Node;
import FoodRequestAPI.database.utility.DatabaseException;
import javafx.scene.control.Alert;

import java.util.HashMap;
import java.util.LinkedList;

public class MapFloorEntity implements IMapEntity {

    //Key is the nodeID or edgeID
    private HashMap<String, Node> nodes;

    private DatabaseController dbController;

    MapFloorEntity() {
        nodes = new HashMap<>();

        dbController = DatabaseController.getInstance();
    }

    /**
     * Inserts a node directly into the floor, subverting the FoodRequestAPI.database
     * @param node the node to insert
     */
    public void insertNode(Node node) {
        nodes.put(node.getNodeID(), node);
    }

    @Override
    public void addNode(Node node) throws DatabaseException {
        int nodeID = dbController.addNode(node);
        node.setUniqueID(nodeID);
        nodes.put(node.getNodeID(), node);
    }

    @Override
    public void editNode(Node node) throws DatabaseException {
        dbController.updateNode(node);
        nodes.put(node.getNodeID(), node);
    }

    @Override
    public Node getNode(String s) throws NotFoundException {
        // Load node from local data

        Node node = nodes.get(s);
        if(node == null)
            throw new NotFoundException("Node not found.");
        return node;
    }

    @Override
    public LinkedList<Node> getAllNodes() {
        LinkedList<Node> allNodes = new LinkedList<>();

        allNodes.addAll(nodes.values());

        return allNodes;
    }

    @Override
    public void removeNode(Node node) {
        try {
            dbController.removeNode(node);
            nodes.remove(node.getNodeID());
        } catch (DatabaseException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("node Error");
            alert.setHeaderText("Error removing node");
            alert.setContentText(ex.toString());
            alert.showAndWait();
        }
    }

    @Override
    public void removeAll() {
        for (Node node : getAllNodes()) {
            removeNode(node);
        }
    }
}
