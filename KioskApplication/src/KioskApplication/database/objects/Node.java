package KioskApplication.database.objects;

public class Node {
    private String nodeID;
    private int xcoord;
    private int ycoord;
    private int floor;
    private int building;
    private int nodeType;
    private String longName;
    private String shortName;
    private String teamAssigned;

    public Node(String nodeID, int xcoord, int ycoord, int floor, int building, int nodeType, String longName, String shortName, String teamAssigned) {
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
    }

    public Node(String nodeID, String floor) {
        this.nodeID = nodeID;
        this.floor = floor;
    }

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

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getBuilding() {
        return building;
    }

    public void setBuilding(int building) {
        this.building = building;
    }

    public int getNodeType() {
        return nodeType;
    }

    public void setNodeType(int nodeType) {
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

    public String getNodeID() {
        return nodeID;
    }
}
