package KioskApplication.entity;

import KioskApplication.database.DatabaseController;
import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;
import KioskApplication.utility.NodeFloor;
import KioskApplication.utility.NodeType;

import java.util.*;

public class MapEntity implements IMapEntity {

    private HashMap<NodeFloor,MapFloorEntity> floors;
    private HashMap<String, Edge> edges;

    private DatabaseController dbController;

    private static MapEntity instance = null;

    protected MapEntity() {
        floors = new HashMap<>();
        edges = new HashMap<>();

        dbController = DatabaseController.getInstance();
    }

    public static MapEntity getInstance() {
        if (instance == null) instance = new MapEntity();
        return instance;
    }

    // TODO do this somewhere else, and be more smart about our database access
    public void readAllFromDatabase() {
        ArrayList<Node> nodes = dbController.getAllNodes();
        for (Node node : nodes)
            addNode(node);

        ArrayList<Edge> edges = dbController.getAllEdges();
        for (Edge edge : edges)
            addEdge(edge);
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

    public int getNodeTypeCount(NodeType nodeType, NodeFloor floor, String teamAssigned){
        return dbController.getNodeTypeCount(nodeType, floor, teamAssigned);
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
    public void removeNode(String s) {
        Node n = getNode(s);
        List<Edge> edges = getEdges(n);
        for (Edge edge : edges) {
            removeEdge(edge.getEdgeID());
        }

        for (NodeFloor floor : floors.keySet()) {
            MapFloorEntity floorEntity = floors.get(floor);
            if (floorEntity.getNode(s) != null)
                floorEntity.removeNode(s);
        }
    }

    public void addEdge(Edge e) {
        edges.put(e.getEdgeID(),e);
        dbController.addEdge(e);
    }

    public void editEdge(Edge e) {
        edges.put(e.getEdgeID(), e);
        dbController.updateEdge(e);
    }

    public Edge getEdge(String s) {
        // Load edge from local data
        Edge edge = edges.get(s);

        // If edge doesn't exist, attempt to load it from the database
        if (edge == null) {
            edge = dbController.getEdge(s);
            // Add edge to local data if found
            if (edge != null) edges.put(s, edge);
        }

        return edge;
    }

    // TODO pass edge as param instead of string
    public void removeEdge(String s) {
        edges.remove(s);
        dbController.removeEdge(new Edge(s, "", ""));
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