package database.objects;

import database.connection.NotFoundException;
import entity.MapEntity;
import utility.node.NodeType;

public class Edge {
    public static final int STAIR_COST = 100;
    public static final int ELEVATOR_COST = 100;
    private String edgeID; //ID of the edge
    private String node1ID; //one of the nodes that is part of the edge
    private String node2ID; //one of the nodes that is part of the edge
    private Integer cost = null;
    private Boolean wheelchairAccessible = null;

    //initialize the edge with an id, and two nodes
    //checks to see if each node exists
    public Edge(String edgeID, String node1, String node2) {
        this.edgeID = edgeID;
        this.node1ID = node1;
        this.node2ID = node2;
    }

    /**
     * Override of .equals()
     * @param obj other edge object
     * @return true if nodes are the same; otherwise false
     */
    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if (obj.getClass() == this.getClass()) {
            Edge other = (Edge)obj;
            return (edgeID.equals(other.getEdgeID()) &&
                    node1ID.equals(other.getNode1ID()) &&
                    node2ID.equals(other.getNode2ID()));
        } else {
            return false;
        }
    }

    /**
     * Gets the ID of this edge
     * @return String of ID
     */
    public String getEdgeID() {
        return edgeID;
    }

    /**
     * Gets the ID of node1
     * @return String of ID
     */
    public String getNode1ID() {
        return node1ID;
    }

    /**
     * Gets the ID of node2
     * @return String of ID
     */
    public String getNode2ID() {
        return node2ID;
    }

    /**
     * Sets the ID for node1
     * @param node1 String of node ID
     */
    public void setNode1ID(String node1) {
        this.node1ID = node1;
    }

    /**
     * Sets the ID for node2
     * @param node2 String of node ID
     */
    public void setNode2ID(String node2) {
        this.node2ID = node2;
    }

    /**
     * Returns true if two the given edge is connected by having a common node
     * @param e Another edge to compare nodes with
     * @return True if there is a common node; otherwise false
     */
    public boolean isConnectedTo(Edge e) {
        return this.node1ID.equals(e.getNode1ID())  ||
                this.node1ID.equals(e.getNode2ID()) ||
                this.node2ID.equals(e.getNode1ID()) ||
                this.node2ID.equals(e.getNode2ID());
    }

    /**
     * Given one node ID return the other node ID or null if the given node isn't a node in the edge
     * @param id ID of the given node
     * @return the ID of the other connected node or null
     */
    public String getOtherNodeID(String id) {

        if(this.node1ID.equals(id))
            return this.node2ID;
        else if(this.node2ID.equals(id))
            return this.node1ID;
        else
            return null;
    }

    /**
     * Calculates the cost to travel across this edge and determines whether the edge is wheelchair-accessible.
     */
    private void calculateCost(){
        MapEntity map = MapEntity.getInstance();
        Node node1, node2;
        try {
            node1 = map.getNode(this.node1ID);
            node2 = map.getNode(this.node2ID);
            //If the two nodes are both stairs or elevators and are on different floors, the edge between them represents
            //a staircase or an elevator shaft, respectively.  In either case, assign a cost to approximate the difficulty
            //of taking a staircase or an elevator up or down a single floor (same weight whether up or down).
            if(node1.getNodeType().equals(NodeType.STAI) &&
               node2.getNodeType().equals(NodeType.STAI) &&
               !node1.getFloor().equals(node2.getFloor())){
                this.cost =  STAIR_COST;
                this.wheelchairAccessible = false;
            }
            else if(node1.getNodeType().equals(NodeType.ELEV) &&
                    node2.getNodeType().equals(NodeType.ELEV) &&
                    !node1.getFloor().equals(node2.getFloor())) {
                this.cost = ELEVATOR_COST;
                this.wheelchairAccessible = true;
            }
                //Otherwise, estimate the cost as normal.
            else {
                //Assuming all edges are straight lines, the cost of the edge from the parent node should be the
                // straight-line distance between the two.
                int xDistance = Math.abs(node1.getXcoord() - node2.getXcoord());
                int yDistance = Math.abs(node1.getYcoord() - node2.getYcoord());
                //Calculate distance with Pythagorean theorem
                int straightLineDistance = (int) Math.sqrt((Math.pow(xDistance, 2) + Math.pow(yDistance, 2)));
                //Total cost to get here is the sum of the cost to get to the previous node & the cost to get from that
                //node to here.
                this.cost =  straightLineDistance;
                this.wheelchairAccessible = true;
            }
        }
        catch (NotFoundException exception){
            exception.printStackTrace();
            //TODO: add actual handling
        }
    }

    /**
     * Getter for this.cost.  Checks to see if this edge's cost has been calculated yet; if not, calculates it and
     * returns the result; if so, returns the value.
     * @return
     */
    public int getCost() {
        if(this.cost == null)
            this.calculateCost();
        return cost;
    }

    /**
     * Getter for this.wheelchairAccessible.  Checks to see if this edge's wheelchair accessibility has been determined
     * yet; if not, evaluates it and returns the result; if so, returns the value.
     * @return
     */
    public boolean isWheelchairAccessible() {
        if(this.wheelchairAccessible == null)
            this.calculateCost();
        return wheelchairAccessible;
    }
}
