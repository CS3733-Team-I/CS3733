package entity;

import database.DatabaseController;
import database.objects.Node;
import database.utility.DatabaseException;
import javafx.scene.control.Alert;
import utility.node.NodeFloor;

import java.util.HashMap;
import java.util.LinkedList;

public class MapFloorEntity implements IMapEntity {

    //Key is the nodeID or edgeID
    private HashMap<String, Node> nodes;
    private NodeFloor floor;

    private DatabaseController dbController;

    public MapFloorEntity(NodeFloor floor) {
        this.floor = floor;
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
    public void addNode(Node node) throws DatabaseException {
        dbController.addNode(node);
        nodes.put(node.getNodeID(), node);
    }

    @Override
    public void editNode(Node node) throws DatabaseException {
        dbController.updateNode(node);
        nodes.put(node.getNodeID(), node);
    }

    @Override
    public Node getNode(String s) {
        // Load node from local data
        Node node = nodes.get(s);

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

    @Override
    public void removeAll() throws DatabaseException {
        for (Node node : getAllNodes()) {
            removeNode(node);
        }
    }
}
