package database.objects;

import javafx.beans.Observable;
import utility.node.NodeBuilding;
import utility.node.NodeFloor;
import utility.node.NodeType;

public class Node {

    private int uniqueID;
    private String nodeID;
    private int xcoord;
    private int ycoord;
    private NodeFloor floor;
    private NodeBuilding building;
    private NodeType nodeType;
    private String longName;
    private String shortName;
    private String teamAssigned;

    public Node(Node node) {
        this(node.getNodeID(), node.getXcoord(), node.getYcoord(), node.getFloor(), node.getBuilding(), node.getNodeType(),
                node.getLongName(), node.getShortName(), node.getTeamAssigned());
        this.setUniqueID(node.uniqueID);
    }

    public Node(String nodeID, int xcoord, int ycoord, NodeFloor floor, NodeBuilding building, NodeType nodeType,
                String longName, String shortName, String teamAssigned) {
        this.nodeID = nodeID;
        this.xcoord = xcoord;
        this.ycoord = ycoord;
        this.floor = floor;
        this.building = building;
        this.nodeType = nodeType;
        this.longName = longName;
        this.shortName = shortName;
        this.teamAssigned = teamAssigned;
    }

    public Node(String nodeID) {
        this.nodeID = nodeID;
        this.xcoord = 0;
        this.ycoord = 0;
        this.floor = NodeFloor.THIRD;
        this.building = NodeBuilding.FRANCIS45;
        this.nodeType = NodeType.HALL;
        this.longName = "";
        this.shortName = "";
        this.teamAssigned = "";
    }

    public Node(String nodeID, NodeFloor floor) {
        this.nodeID = nodeID;
        this.xcoord = 0;
        this.ycoord = 0;
        this.floor = floor;
        this.building = NodeBuilding.FRANCIS45;
        this.nodeType = NodeType.HALL;
        this.longName = "";
        this.shortName = "";
        this.teamAssigned = "";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;

        if (obj.getClass() == this.getClass()) {
            Node other = (Node)obj;
            return (nodeID.equals(((Node) obj).getNodeID()) &&
                    xcoord == other.getXcoord() &&
                    ycoord == other.getYcoord() &&
                    floor == other.getFloor() &&
                    building == other.getBuilding() &&
                    nodeType.equals(other.getNodeType()) &&
                    longName.equals(other.getLongName()) &&
                    shortName.equals(other.getShortName()) &&
                    teamAssigned.equals(other.getTeamAssigned()));
        } else {
            return false;
        }
    }

    public int getUniqueID() { return uniqueID; }

    public void setUniqueID(int uniqueID) { this.uniqueID = uniqueID; }

    public int getXcoord() {
        return xcoord;
    }

    public void setXcoord(int xcoord) {
        this.xcoord = xcoord;
    }

    public int getYcoord() {
        return ycoord;
    }

    public void setYcoord(int ycoord) {
        this.ycoord = ycoord;
    }

    public NodeFloor getFloor() {
        return floor;
    }

    public void setFloor(NodeFloor floor) {
        this.floor = floor;
    }

    public NodeBuilding getBuilding() {
        return building;
    }

    public void setBuilding(NodeBuilding building) {
        this.building = building;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getTeamAssigned() {
        return teamAssigned;
    }

    public void setTeamAssigned(String teamAssigned) {
        this.teamAssigned = teamAssigned;
    }

    public String getNodeID()
    {
        return nodeID;
    }

    public void setNodeID(String nodeID) {
        this.nodeID = nodeID;
    }

    public String getXyz() {
        return Integer.toString(xcoord)+Integer.toString(ycoord)+floor.toString();
    }

    /**
     * Return angle between this and Node n
     * @param n node to get angle to
     * @return angle in radians
     */
    public double getAngleBetweenNodes(Node n) {
        double dx = n.getXcoord() - xcoord;
        double dy = n.getYcoord() -ycoord;

        return Math.atan2(dy,dx);
    }
}
