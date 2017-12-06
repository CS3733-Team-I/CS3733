package entity;

import database.connection.NotFoundException;
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

        MapEntity map = MapEntity.getInstance();

        //        //Elevator
        if(nextNode.getNodeType().equals(NodeType.ELEV) && thisNode.getNodeType().equals(NodeType.ELEV)) {
            return "Take the elevator to " + nextNode.getFloor().toString() + " ";
        }
        //Stairs
        else if(nextNode.getNodeType().equals(NodeType.STAI) && thisNode.getNodeType().equals(NodeType.STAI)) {
            return "Take the stairs to " + nextNode.getFloor().toString() + " ";
        }

        //Calculate angles if turning
        double prevAngle = prevNode.getAngleBetweenNodes(thisNode);
        double nextAngle = thisNode.getAngleBetweenNodes(nextNode);

        double angleDif = nextAngle - prevAngle;
        double straightAngle = Math.PI/6;
        double rightAngle = Math.PI/2;

        //Turning directions

        String returnStr = "";

        if(!thisNode.getNodeType().equals(NodeType.HALL)){
            if(Math.abs(angleDif) < straightAngle) returnStr += "Go straight at " + thisNode.getLongName();
            else if(angleDif > rightAngle) returnStr += "Make a sharp right at " + thisNode.getLongName() + ". Go straight for ";
            else if(angleDif >= straightAngle) returnStr += "Turn right at " + thisNode.getLongName() + ". Go straight for ";
            else if(angleDif < rightAngle) returnStr += "Make a sharp left at " + thisNode.getLongName() + ". Go straight for ";
            else if(angleDif <= straightAngle) returnStr += "Turn left at " + thisNode.getLongName() + ". Go straight for ";
            else returnStr += "Go to " + thisNode.getLongName() + ". Go straight for ";
        }
        else {
            if(Math.abs(angleDif) < straightAngle) returnStr += "Go straight at hallway intersection for ";
            else if(angleDif > rightAngle) returnStr += "Make a sharp right at hallway intersection. Go straight for ";
            else if(angleDif >= straightAngle) returnStr += "Turn right at hallway intersection. Go straight for ";
            else if(angleDif < rightAngle) returnStr += "Make a sharp left at hallway intersection. Go straight for ";
            else if(angleDif <= straightAngle) returnStr += "Turn left at hallway intersection. Go straight for ";
            else returnStr += "Go to " + thisNode.getLongName() + ". Go straight for ";
        }
        return returnStr + map.getConnectingEdge(thisNode,nextNode).getCost() + " feet.";

    }

    private LinkedList<Node> getListOfNodes(LinkedList<Edge> segment, Node segmentStart) {

        MapEntity map = MapEntity.getInstance();

        LinkedList<Node> nodes = new LinkedList<>();
        nodes.add(segmentStart);

        for (Edge e : segment) {
            try {
                nodes.add(map.getNode(e.getOtherNodeID(nodes.getLast().getNodeID())));
            }
            catch (NotFoundException exception){
                exception.printStackTrace();
                //TODO: add actual handling
            }
        }
        return nodes;
    }
}
