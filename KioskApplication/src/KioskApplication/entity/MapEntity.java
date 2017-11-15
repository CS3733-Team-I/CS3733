package KioskApplication.entity;

import KioskApplication.database.DatabaseController;
import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;
import KioskApplication.utility.NodeFloor;
import KioskApplication.utility.NodeType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

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

    // TODO do this somewhere else, and be more smart about our database access
    public void readAllFromDatabase() {
        ArrayList<Node> nodes = DatabaseController.getAllNodes();
        for (Node node : nodes)
            addNode(node);

        ArrayList<Edge> edges = DatabaseController.getAllEdges();
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
    public ArrayList<Node> getAllNodes() {
        ArrayList<Node> allNodes = new ArrayList<>();

        for (NodeFloor floor : floors.keySet()) {
            allNodes.addAll(floors.get(floor).getAllNodes());
        }

        return allNodes;
    }

    public ArrayList<Node> getNodesOnFloor(NodeFloor floor) {
        if (floors.containsKey(floor))
            return floors.get(floor).getAllNodes();
        else
            return new ArrayList<>();
    }

    public int getNodeTypeCount(NodeType nodeType, NodeFloor floor, String teamAssigned){
        return DatabaseController.getNodeTypeCount(nodeType, floor, teamAssigned);
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
        DatabaseController.addEdge(e);
    }

    public void editEdge(Edge e) {
        edges.put(e.getEdgeID(), e);
        DatabaseController.updateEdge(e);
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