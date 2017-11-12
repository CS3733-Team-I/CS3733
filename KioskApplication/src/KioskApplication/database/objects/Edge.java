package KioskApplication.database.objects;

import java.sql.SQLException;

public class Edge {
    private String edgeID; //ID of the edge
    private String node1ID; //one of the nodes that is part of the edge
    private String node2ID; //one of the nodes that is part of the edge

    //initialize the edge with an id, and two nodes
    //checks to see if each node exists
    public Edge(String edgeID, String node1, String node2) throws SQLException {
        this.edgeID = edgeID;
        this.node1ID = node1;
        this.node2ID = node2;
    }


    public String getEdgeID() {
        return edgeID;
    }

    public String getNode1ID() {
        return node1ID;
    }

    public String getNode2ID() {
        return node2ID;
    }

    public void setNode1ID(String node1) {
        this.node1ID = node1;
    }

    public void setNode2ID(String node2) {
        this.node2ID = node2;
    }

}
