package database.objects;

public class Edge {
    private String edgeID; //ID of the edge
    private String node1ID; //one of the nodes that is part of the edge
    private String node2ID; //one of the nodes that is part of the edge

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
}
