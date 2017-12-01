package entity;

import database.objects.Edge;
import database.objects.Node;
import utility.node.NodeFloor;
import utility.node.NodeType;

import java.util.LinkedList;
import java.util.List;

public class Path {
    private LinkedList<Node> waypoints;
    private LinkedList<LinkedList<Edge>> edges;
    private LinkedList<LinkedList<String>> directions;

    public Path(List<Node> waypoints, LinkedList<LinkedList<Edge>> edges) {
        this.waypoints = new LinkedList<>(waypoints);
        this.edges = new LinkedList<>(edges);
        generateDirections();
    }

    public LinkedList<Node> getWaypoints() {
        return waypoints;
    }

    public LinkedList<LinkedList<Edge>> getEdges() {
        return edges;
    }

    public String getDirections() {
        String returnStr = "";
        for(LinkedList<String> segment: directions) {
            for (String s : segment)
                returnStr += s + "\n";
        }
        return returnStr;
    }

    public LinkedList<LinkedList<String>> getDirectionsList() {
        return directions;
    }

    public boolean equals(Path comparePath){
        return(this.edges.equals(comparePath.getEdges()) && this.waypoints.equals(comparePath.getWaypoints()));
    }

    private void generateDirections() {
        directions = new LinkedList<>();
        int segmentIndex = 0;
        for(LinkedList<Edge> edgeSegment: edges) {
            LinkedList<String> directionSegment = new LinkedList<>();
            LinkedList<Node> nodes = getListOfNodes(edgeSegment, this.waypoints.get(segmentIndex++));
            directionSegment.add("Start at " + nodes.getFirst().getLongName());
            for (int i = 0; i < nodes.size(); i++) {
                if (i != 0 && i != nodes.size() - 1)
                    directionSegment.add(findDirectionInstructions(nodes.get(i), nodes.get(i - 1), nodes.get(i + 1)));
            }

            directionSegment.add("End at " + nodes.getLast().getLongName());
            directions.add(directionSegment);
        }
    }

    private String findDirectionInstructions(Node thisNode, Node prevNode, Node nextNode) {

        if(nextNode.getNodeType().equals(NodeType.ELEV) && thisNode.getNodeType().equals(NodeType.ELEV)) {
            return "Take the elevator to " + nextNode.getFloor().toString() + " ";
        }
        else if(nextNode.getNodeType().equals(NodeType.STAI) && thisNode.getNodeType().equals(NodeType.STAI)) {
            return "Take the stairs to " + nextNode.getFloor().toString() + " ";
        }

        double prevAngle = getAngleBetweenNodes(prevNode, thisNode);
        double nextAngle = getAngleBetweenNodes(thisNode, nextNode);

        double angleDif = nextAngle - prevAngle;
        double straightAngle = Math.PI/6;

        String longName = "";

        if(Math.abs(angleDif) < straightAngle) return "Go straight at " + thisNode.getLongName();
        else if(angleDif >= straightAngle) return "Turn right at " + thisNode.getLongName();
        else if(angleDif <= straightAngle) return "Turn left at " + thisNode.getLongName();

        return "Go to ";
    }

    private double getAngleBetweenNodes(Node n1, Node n2) {
        double dx = n2.getXcoord() - n1.getXcoord();
        double dy = n2.getYcoord() - n1.getYcoord();

        return Math.atan2(dy,dx);
    }

    private LinkedList<Node> getListOfNodes(LinkedList<Edge> segment, Node segmentStart) {

        LinkedList<Node> nodes = new LinkedList<>();
        nodes.add(segmentStart);

        for(Edge e : segment) {
            nodes.add(getOtherNode(e,nodes.getLast()));
        }
        return nodes;
    }

    private Node getOtherNode(Edge e, Node n) {

        MapEntity map = MapEntity.getInstance();

        if(e.getNode1ID().equals(n.getNodeID()))
            return map.getNode(e.getNode2ID());
        else if(e.getNode2ID().equals(n.getNodeID()))
            return map.getNode(e.getNode1ID());
        else
            return null;
    }
}
