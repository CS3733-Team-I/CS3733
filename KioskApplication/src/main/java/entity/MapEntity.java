package entity;

import database.DatabaseController;
import database.connection.NotFoundException;
import database.objects.Edge;
import database.objects.Node;
import database.utility.DatabaseException;
import javafx.scene.layout.Pane;
import utility.node.NodeFloor;
import utility.node.NodeType;
import utility.node.TeamAssigned;

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
    public void readAllFromDatabase() throws DatabaseException {
        // TODO do this somewhere else, and be more smart about our database access
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
    }

    @Override
    public void addNode(Node n) throws DatabaseException {
        NodeFloor f = n.getFloor();
        if(!floorExists(f)) addFloor(f);

        floors.get(f).addNode(n);
    }

    @Override
    public void editNode(Node n) throws DatabaseException {
        NodeFloor f = n.getFloor();
        if(floorExists(f)) {
            floors.get(f).editNode(n);
        }
    }


    @Override
    public Node getNode(String s) throws NotFoundException{
        Node thisNode = null;
        //First, check to see if the node is in the map.  Check every floor.
        for (NodeFloor floor : floors.keySet()) {
            try {
                thisNode = floors.get(floor).getNode(s);
            }
            catch(NotFoundException exception){
            }
            //If we found the node, return it.
            if(thisNode != null)
                return thisNode;
        }

        //If the node isn't in the map, check the database to see if it even exists.
        try {
            thisNode = dbController.getNode(s);
            //If it does, go ahead and add it to the map.
            NodeFloor f = thisNode.getFloor();
            if (!floorExists(f))
                addFloor(f);
            floors.get(f).insertNode(thisNode);
        }
        catch (DatabaseException ex) {
            ex.printStackTrace();
        }
        //If you reach this point, something's gone wrong.
        return thisNode;
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

    public String getNodeTypeCount(NodeType nodeType, NodeFloor floor, TeamAssigned teamAssigned, String temp) {
        try {
            if(nodeType != NodeType.ELEV) {
                return String.valueOf(dbController.getNodeTypeCount(nodeType, floor, teamAssigned));
            }else{
                return generateElevName(floor, teamAssigned, temp);
            }
        } catch (DatabaseException e) {
            e.printStackTrace(); // TODO implement handling of DB exception
        }
        return "";
    }

    public String generateElevName(NodeFloor floor, TeamAssigned teamAssigned, String changedListElevName){
        String alphList = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String result = "EXC";
        try{
            String temp = dbController.getAllElevName(floor, teamAssigned) + changedListElevName;
            int count = temp.length();
            int number = 0;
            if(count > 26){return "EXC";}
            else {
                for(int i=0; i<count; i++){
                    if(alphList.indexOf(temp.charAt(i)) != -1){
                        number = alphList.indexOf(temp.charAt(i));
                        alphList = alphList.substring(0, number) + alphList.substring(number+1, alphList.length());
                    }
                }
                result = alphList.charAt(0) + "";
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public String selectNodeID(int xcoord, int ycoord, NodeFloor floor, NodeType nodeType){
        String result = "";
        try {
            result = dbController.selectNodeName(xcoord, ycoord, floor, nodeType);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        return result;
    }



    // TODO this is an expensive function, should probably rewrite
    public ArrayList<Edge> getEdgesOnFloor(NodeFloor floor) {
        ArrayList<Edge> edgesOnFloor = new ArrayList<>();

        Collection<Edge> allEdges = edges.values();
        for (Edge edge : allEdges) {
            try {
                Node node1 = getNode(edge.getNode1ID());
                Node node2 = getNode(edge.getNode2ID());

                if ((floor == node1.getFloor()) && (floor == node2.getFloor())) {
                    edgesOnFloor.add(edge);
                }
            }
            catch(NotFoundException exception){
                exception.printStackTrace();
                //TODO: add actual handling
            }
        }

        return edgesOnFloor;
    }


    @Override
    public void removeNode(Node node) throws DatabaseException {
        List<Edge> edges = getEdges(node);
        for (Edge edge : edges) {
            removeEdge(edge);
        }

        for (NodeFloor floor : floors.keySet()) {
            MapFloorEntity floorEntity = floors.get(floor);
            try{
                //Check to see if the node is on the floor. If so, remove it.
                floorEntity.getNode(node.getNodeID());
                floorEntity.removeNode(node);
            }
            catch(NotFoundException exception){
            }
        }
    }

    @Override
    public void removeAll() throws DatabaseException {
        List<Node> nodes = getAllNodes();
        for (Node node : nodes) {
            ArrayList<Edge> edges = MapEntity.getInstance().getEdges(node);
            for (Edge edge : edges)
                MapEntity.getInstance().removeEdge(edge);
            removeNode(node);
        }
    }

    public void addEdge(Edge e) throws DatabaseException {
        dbController.addEdge(e);
        edges.put(e.getEdgeID(), e);
    }

    public void editEdge(Edge e) throws DatabaseException {
        dbController.updateEdge(e);
        edges.put(e.getEdgeID(), e);
    }

    public Edge getEdge(String s) throws DatabaseException {
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

    public void removeEdge(Edge edge) throws DatabaseException {
        dbController.removeEdge(edge);
        edges.remove(edge.getEdgeID());
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
        floors.put(floor, new MapFloorEntity(floor));
    }

    //Given two nodes, returns the edge connecting them, or null if they aren't connected.
    public Edge getConnectingEdge(Node node1, Node node2){
        ArrayList<Edge> node1Edges = getEdges(node1);
        for(Edge edge: node1Edges){
            if((edge.getNode1ID().equals(node1.getNodeID()) && (edge.getNode2ID().equals(node2.getNodeID()))) ||
               (edge.getNode1ID().equals(node2.getNodeID()) && (edge.getNode2ID().equals(node1.getNodeID())))    )
                return edge;
        }
        return null;
    }

    /**
     * Given a node, return a list of all adjacent nodes.
     * @param node
     * @param wheelchairAccessible if true, exclude nodes connected by edges that a wheelchair can't access
     * @return LinkedList of nodes accessible from this node
     */
    public LinkedList<Node> getConnectedNodes(Node node, boolean wheelchairAccessible){
        ArrayList<Edge> edges = this.getEdges(node);
        if(wheelchairAccessible){
            ArrayList<Edge> removeList = new ArrayList<>();
            for(Edge edge: edges){
                if(!edge.isWheelchairAccessible())
                    removeList.add(edge);
            }
            edges.removeAll(removeList);
        }
        LinkedList<Node> connectedNodes = new LinkedList<>();
        for(Edge edge: edges){
            if(edge.getNode1ID().equals(node.getNodeID())){
                try {
                    connectedNodes.add(getNode(edge.getNode2ID()));
                }
                catch (NotFoundException exception){
                    exception.printStackTrace();
                    //TODO: add actual handling
                }
            }
            else if(edge.getNode2ID().equals(node.getNodeID())){
                try {
                    connectedNodes.add(getNode(edge.getNode1ID()));
                }
                catch(NotFoundException exception){
                    exception.printStackTrace();
                    //TODO: add actual handling
                }
            }
        }
        return connectedNodes;
    }

    /**
     * Alternate call for getConnectedNodes.  If no wheelchair accessibility level is specified, default to false.
     * @param node
     * @return
     */
    public LinkedList<Node> getConnectedNodes(Node node){
        return this.getConnectedNodes(node, false);
    }
}