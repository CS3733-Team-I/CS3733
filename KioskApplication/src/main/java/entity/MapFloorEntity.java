package entity;

import database.DatabaseController;
import database.connection.NotFoundException;
import database.objects.Employee;
import database.objects.Node;
import database.utility.DatabaseException;
import javafx.scene.control.Alert;

import java.util.HashMap;
import java.util.LinkedList;

public class MapFloorEntity implements IMapEntity {

    //Key is the nodeID or edgeID
    private HashMap<String, Node> nodes;

    private DatabaseController dbController;
    private ActivityLogger activityLogger;

    MapFloorEntity() {
        nodes = new HashMap<>();

        dbController = DatabaseController.getInstance();
        activityLogger = ActivityLogger.getInstance();
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
        int nodeID = dbController.addNode(node);
        node.setUniqueID(nodeID);
        nodes.put(node.getNodeID(), node);
        if (LoginEntity.getInstance().getCurrentLogin() instanceof Employee){
            activityLogger.logNodeAdd(LoginEntity.getInstance().getCurrentLogin(),node);
        }
    }

    @Override
    public void editNode(Node node) throws DatabaseException {
        dbController.updateNode(node);
        nodes.put(node.getNodeID(), node);
        if (LoginEntity.getInstance().getCurrentLogin() instanceof Employee){
            activityLogger.logNodeUpdate(LoginEntity.getInstance().getCurrentLogin(),node);
        }
    }

    @Override
    public Node getNode(String s) throws NotFoundException{
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
            if (LoginEntity.getInstance().getCurrentLogin() instanceof Employee){
                activityLogger.logNodeDelete(LoginEntity.getInstance().getCurrentLogin(),node);
            }
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
