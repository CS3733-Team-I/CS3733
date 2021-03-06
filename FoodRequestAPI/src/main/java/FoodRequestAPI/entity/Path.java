package FoodRequestAPI.entity;

import FoodRequestAPI.database.connection.NotFoundException;
import FoodRequestAPI.database.objects.Edge;
import FoodRequestAPI.database.objects.Node;
import FoodRequestAPI.utility.node.NodeFloor;
import FoodRequestAPI.utility.node.NodeType;

import java.util.LinkedList;
import java.util.List;

public class Path {
    private LinkedList<Node> waypoints;
    private LinkedList<LinkedList<Edge>> edges;
    private LinkedList<LinkedList<String>> directions;
    private int currentCost = 0;
    private int distance;

    public Path(List<Node> waypoints, LinkedList<LinkedList<Edge>> edges) {
        this.waypoints = new LinkedList<>(waypoints);
        this.edges = new LinkedList<>(edges);
        generateDirections();
        setDistance();
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
            directionSegment.add(findStartDirectionInstructions(nodes));
            for (int i = 0; i < nodes.size(); i++) {
                if (i != 0 && i != nodes.size() - 1)
                    directionSegment.add(findDirectionInstructions(nodes.get(i), nodes.get(i - 1), nodes.get(i + 1)));
            }
            if(!nodes.getLast().getNodeType().equals(NodeType.HALL))
                directionSegment.add(SystemSettings.getInstance().getResourceBundle().getString("my.end") + nodes.getLast().getLongName() + ".");
            else
                directionSegment.add(SystemSettings.getInstance().getResourceBundle().getString("my.endHall") + ".");
            directions.add(directionSegment);
        }
    }

    private String findStartDirectionInstructions(LinkedList<Node> nodes) {
        MapEntity map = MapEntity.getInstance();
        String returnStr = "";
        if(!nodes.getFirst().getNodeType().equals(NodeType.HALL))
            returnStr += SystemSettings.getInstance().getResourceBundle().getString("my.start") + nodes.getFirst().getLongName();
        else
            returnStr += SystemSettings.getInstance().getResourceBundle().getString("my.startHall");

        double angle = nodes.getFirst().getAngleBetweenNodes(nodes.get(1));

        if(angle<=Math.PI/6 && angle>=-Math.PI/6) returnStr += SystemSettings.getInstance().getResourceBundle().getString("my.east");
        else if(angle<=Math.PI/3 && angle>Math.PI/6) returnStr += SystemSettings.getInstance().getResourceBundle().getString("my.southeast");
        else if(angle<=2*Math.PI/3 && angle>Math.PI/3) returnStr += SystemSettings.getInstance().getResourceBundle().getString("my.south");
        else if(angle<=5*Math.PI/6 && angle>2*Math.PI/3) returnStr += SystemSettings.getInstance().getResourceBundle().getString("my.southwest");

        else if(angle>=-Math.PI/3 && angle<-Math.PI/6) returnStr += SystemSettings.getInstance().getResourceBundle().getString("my.northeast");
        else if(angle>=-2*Math.PI/3 && angle<-Math.PI/3) returnStr += SystemSettings.getInstance().getResourceBundle().getString("my.north");
        else if(angle>=-5*Math.PI/6 && angle<-2*Math.PI/3) returnStr += SystemSettings.getInstance().getResourceBundle().getString("my.northwest");
        else returnStr += SystemSettings.getInstance().getResourceBundle().getString("my.west");

        return returnStr + (int)map.getConnectingEdge(nodes.getFirst(),nodes.get(1)).getCostFeet() + " " + SystemSettings.getInstance().getResourceBundle().getString("my.feet");
    }

    private String findDirectionInstructions(Node thisNode, Node prevNode, Node nextNode) {

        MapEntity map = MapEntity.getInstance();

        //Elevator
        if(nextNode.getNodeType().equals(NodeType.ELEV) && thisNode.getNodeType().equals(NodeType.ELEV)) {
            return SystemSettings.getInstance().getResourceBundle().getString("my.elevator") + nextNode.getFloor().toString() + " ";
        }
        //Stairs
        else if(nextNode.getNodeType().equals(NodeType.STAI) && thisNode.getNodeType().equals(NodeType.STAI)) {
            return SystemSettings.getInstance().getResourceBundle().getString("my.stairs") + nextNode.getFloor().toString() + " ";
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
            if(Math.abs(angleDif) < straightAngle) returnStr += SystemSettings.getInstance().getResourceBundle().getString("my.straight") + thisNode.getLongName();
            else if(angleDif > rightAngle) returnStr += SystemSettings.getInstance().getResourceBundle().getString("my.sharpright") + thisNode.getLongName() + SystemSettings.getInstance().getResourceBundle().getString("my.gofor");
            else if(angleDif >= straightAngle) returnStr += SystemSettings.getInstance().getResourceBundle().getString("my.right") + thisNode.getLongName() + SystemSettings.getInstance().getResourceBundle().getString("my.gofor");
            else if(angleDif < rightAngle) returnStr += SystemSettings.getInstance().getResourceBundle().getString("my.sharpleft") + thisNode.getLongName() + SystemSettings.getInstance().getResourceBundle().getString("my.gofor");
            else if(angleDif <= straightAngle) returnStr += SystemSettings.getInstance().getResourceBundle().getString("my.left") + thisNode.getLongName() + SystemSettings.getInstance().getResourceBundle().getString("my.gofor");
            else returnStr += "Go to " + thisNode.getLongName() + SystemSettings.getInstance().getResourceBundle().getString("my.gofor"); //".\n\tGo straight for "
        }
        else {
            if(Math.abs(angleDif) < straightAngle) returnStr += SystemSettings.getInstance().getResourceBundle().getString("my.straightHall"); //"Go straight at hallway intersection\n\tfor "
            else if(angleDif > rightAngle) returnStr += SystemSettings.getInstance().getResourceBundle().getString("my.sharprightHall"); //"Make a sharp right at hallway intersection.\n\tGo straight for "
            else if(angleDif >= straightAngle) returnStr += SystemSettings.getInstance().getResourceBundle().getString("my.rightHall"); //"Turn right at hallway intersection.\n\tGo straight for "
            else if(angleDif < rightAngle) returnStr += SystemSettings.getInstance().getResourceBundle().getString("my.sharpleftHall"); //"Make a sharp left at hallway intersection.\n\tGo straight for "
            else if(angleDif <= straightAngle) returnStr += SystemSettings.getInstance().getResourceBundle().getString("my.leftHall"); //"Turn left at hallway intersection.\n\tGo straight for "
            else returnStr += SystemSettings.getInstance().getResourceBundle().getString("my.go") + thisNode.getLongName() + SystemSettings.getInstance().getResourceBundle().getString("my.gofor");
        }
        return returnStr + (int)map.getConnectingEdge(thisNode,nextNode).getCostFeet() + " "+ SystemSettings.getInstance().getResourceBundle().getString("my.feet"); //" feet."
    }

     public LinkedList<Node> getListOfNodes(LinkedList<Edge> segment, Node segmentStart) {

        MapEntity map = MapEntity.getInstance();

        LinkedList<Node> nodes = new LinkedList<>();
        nodes.add(segmentStart);

        for (Edge e : segment) {
            try {
                nodes.add(map.getNode(e.getOtherNodeID(nodes.getLast().getNodeID())));
            }
            catch (NotFoundException exception){}
        }
        return nodes;
    }

    /**
     * get the nodes On target floor
     */
    public LinkedList<Node> getListOfNodesSegmentOnFloor(LinkedList<Edge> segment, Node segmentStart, NodeFloor floor) {
        currentCost = 0;

        MapEntity map = MapEntity.getInstance();

        LinkedList<Node> nodes = new LinkedList<>();

        try {
            if(segmentStart.getFloor() != floor) {
                for(Edge e : segment) {
                    if(e.getEdgeType() == "elevator shaft" || e.getEdgeType() == "staircase") {
                        if(map.getNode(e.getNode1ID()).getFloor() == floor) {
                            segmentStart = map.getNode(e.getNode1ID());
                        }
                        if(map.getNode(e.getNode2ID()).getFloor() == floor) {
                            segmentStart = map.getNode(e.getNode2ID());
                        }
                    }
                }
            }
        } catch (NotFoundException exception){}

        nodes.add(segmentStart);

        for (Edge e : segment) {
            try {
                if(map.getNode(e.getOtherNodeID(nodes.getLast().getNodeID())).getFloor() == floor) {
                    currentCost += e.getCost();
                    nodes.add(map.getNode(e.getOtherNodeID(nodes.getLast().getNodeID())));
                }
            }
            catch (NotFoundException exception){}
        }
        return nodes;
    }

    /**
     * Returns an ordered list of all nodes along the path.
     * @return a list of nodes containing in a path segment
     */
    public LinkedList<Node> getNodesInSegment(Node Start) {
        LinkedList<Node> retVal= new LinkedList<>();
        for(LinkedList<Edge> edges : this.edges) {
            for(Edge edge : edges) {
                if(edge.hasNode(Start)){
                    retVal.addAll(getListOfNodes(edges, Start));
                    break;
                }
            }
        }
        return retVal;
    }

    /**
     * Returns an ordered list of all nodes along the path.
     * @return
     */
    public LinkedList<Node> getListOfAllNodes(){
        LinkedList<Node> allNodes = new LinkedList<>();
        Node startNode = this.waypoints.getFirst();
        allNodes.add(startNode);
        for(LinkedList<Edge> segment: this.edges){
            LinkedList<Node> segmentNodes = this.getListOfNodes(segment, startNode);
            segmentNodes.removeFirst();
            allNodes.addAll(segmentNodes);
        }
        return(allNodes);
    }

    public int getPathCost(){
        return currentCost;
    }

    /**
     * helper method to set the distance of the path when created
     */
    private void setDistance(){
        LinkedList<Edge> edgeList = getEdgesAsOneList();
        distance = 0;
        for(Edge e : edgeList) {
            distance += e.getCost();
        }
    }

    /**
     * return the length of the path in pixels
     * @return length of path in pixels
     */
    public int getDistance() {
        return distance;
    }

    /**
     * Returns the edges as a single linked list, rather than a nested linked list
     * @return linked list of all edges
     */
    private LinkedList<Edge> getEdgesAsOneList() {
        LinkedList<Edge> returnList = new LinkedList<>();
        for(LinkedList<Edge> edgeList : edges) {
            returnList.addAll(edgeList);
        }
        return returnList;
    }
}
