package entity;

import database.DatabaseController;
import database.objects.Edge;
import database.objects.Node;
import database.utility.DatabaseException;
import javafx.scene.control.Alert;
import utility.node.NodeFloor;
import utility.node.NodeType;

import java.util.*;

public class MapEntity implements IMapEntity {

    private HashMap<NodeFloor,MapFloorEntity> floors;
    private HashMap<String, Edge> edges;

    private DatabaseController dbController;

    private static class MapEntitySingleton {
        private static final MapEntity _instance = new MapEntity();

    }

    private MapEntity() {
        floors = new HashMap<>();
        edges = new HashMap<>();

        dbController = DatabaseController.getInstance();
    }

    public static MapEntity getInstance() {
        return MapEntitySingleton._instance;
    }


    /**
     * Clears the cached nodes/edges and then adds all the existing nodes and edges from the  database.
     */
    public void readAllFromDatabase() {
        // TODO do this somewhere else, and be more smart about our database access
        try {
            // Clear our current data
            this.floors.clear();
            this.edges.clear();

            ArrayList<Node> nodes = dbController.getAllNodes();

            // Sort nodes by floor
            HashMap<NodeFloor, ArrayList<Node>> nodesPerFloor = new HashMap<>();
            for (Node node : nodes) {
                if (!nodesPerFloor.containsKey(node.getFloor()))
                    nodesPerFloor.put(node.getFloor(), new ArrayList<>());

                nodesPerFloor.get(node.getFloor()).add(node);
            }

            // Insert nodes into floor entities
            for (NodeFloor floor : nodesPerFloor.keySet()) {
                if(!floorExists(floor)) addFloor(floor);

                MapFloorEntity floorEntity = floors.get(floor);
                for (Node node : nodesPerFloor.get(floor)) {
                    floorEntity.insertNode(node);
                }
            }

            ArrayList<Edge> edges = dbController.getAllEdges();
            for (Edge edge : edges)
                this.edges.put(edge.getEdgeID(), edge);

        } catch (DatabaseException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error adding from DB");
            alert.setHeaderText("Error occurred while adding nodes and edges from database.");
            alert.setContentText(ex.toString());
            alert.showAndWait();
        }
    }

    @Override
    public void addNode(Node n) {
        NodeFloor f = n.getFloor();
        if(!floorExists(f)) addFloor(f);

        floors.get(f).addNode(n);
    }

    @Override
    public void editNode(Node n) {
        NodeFloor f = n.getFloor();
        if(floorExists(f)) {
            floors.get(f).editNode(n);
        }
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
    public LinkedList<Node> getAllNodes() {
        LinkedList<Node> allNodes = new LinkedList<>();

        for (NodeFloor floor : floors.keySet()) {
            allNodes.addAll(floors.get(floor).getAllNodes());
        }

        return allNodes;
    }

    public LinkedList<Node> getNodesOnFloor(NodeFloor floor) {
        if (floors.containsKey(floor))
            return floors.get(floor).getAllNodes();
        else
            return new LinkedList<>();
    }

    public int getNodeTypeCount(NodeType nodeType, NodeFloor floor, String teamAssigned) {
        try {
            return dbController.getNodeTypeCount(nodeType, floor, teamAssigned);
        } catch (DatabaseException e) {
            e.printStackTrace(); // TODO implement handling of DB exception
            return 0;
        }
    }

    // TODO this is an expensive function, should probably rewrite
    public ArrayList<Edge> getEdgesOnFloor(NodeFloor floor) {
        ArrayList<Edge> edgesOnFloor = new ArrayList<>();

        Collection<Edge> allEdges = edges.values();
        for (Edge edge : allEdges) {
            Node node1 = getNode(edge.getNode1ID());
            Node node2 = getNode(edge.getNode2ID());

            if ((floor == node1.getFloor()) && (floor == node2.getFloor())) {
                edgesOnFloor.add(edge);
            }
        }

        return edgesOnFloor;
    }


    @Override
    public void removeNode(Node node) {
        List<Edge> edges = getEdges(node);
        for (Edge edge : edges) {
            removeEdge(edge);
        }

        for (NodeFloor floor : floors.keySet()) {
            MapFloorEntity floorEntity = floors.get(floor);
            if (floorEntity.getNode(node.getNodeID()) != null)
                floorEntity.removeNode(node);
        }
    }

    public void addEdge(Edge e) {
        try {
            dbController.addEdge(e);
            edges.put(e.getEdgeID(), e);
        } catch (DatabaseException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Edge Error");
            alert.setHeaderText("Error adding edge");
            alert.setContentText(ex.toString());
            alert.showAndWait();
        }
    }

    public void editEdge(Edge e) {
        try {
            dbController.updateEdge(e);
            edges.put(e.getEdgeID(), e);
        } catch (DatabaseException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Edge Error");
            alert.setHeaderText("Error editing edge");
            alert.setContentText(ex.toString());
            alert.showAndWait();
        }
    }

    public Edge getEdge(String s) {
        // Load edge from local data
        Edge edge = edges.get(s);

        // If edge doesn't exist, attempt to load it from the database
        if (edge == null) {
            try {
                edge = dbController.getEdge(s);
                // Add edge to local data if found
                if (edge != null) edges.put(s, edge);
            } catch (DatabaseException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Edge Error");
                alert.setHeaderText("Error getting edge: " + s);
                alert.setContentText(ex.toString());
                alert.showAndWait();
            }
        }

        return edge;
    }

    public void removeEdge(Edge edge) {
        try {
            dbController.removeEdge(edge);
            edges.remove(edge.toString());
        } catch (DatabaseException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Edge Error");
            alert.setHeaderText("Error removing edge");
            alert.setContentText(ex.toString());
            alert.showAndWait();
        }
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
            if((edge.getNode1ID().equals(node1.getNodeID()) && (edge.getNode2ID().equals(node2.getNodeID()))) ||
               (edge.getNode1ID().equals(node2.getNodeID()) && (edge.getNode2ID().equals(node1.getNodeID())))    )
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
                connectedNodes.add(getNode(edge.getNode2ID()));
            else if(edge.getNode2ID().equals(node.getNodeID()))
                connectedNodes.add(getNode(edge.getNode1ID()));
        }
        return connectedNodes;
    }
}