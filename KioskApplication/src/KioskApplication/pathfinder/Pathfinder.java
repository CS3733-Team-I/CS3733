package KioskApplication.pathfinder;

import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;
import KioskApplication.entity.MapEntity;
import KioskApplication.entity.Path;
import KioskApplication.utility.NodeFloor;

import java.util.*;

public class Pathfinder {

    /**
     * Given a list of multiple waypoints, finds a path between them.  Returns a Path object containing the list of
     *  the waypoints and a list of edges defining a path from the first to the last waypoint that passes through all
     *  the waypoints in between, in order.
     *
     * @param waypoints A list of nodes through which the path should pass
     * @return A Path object containing the waypoints and a list of edges marking a path between them.
     */
    public static Path generatePath(LinkedList<Node> waypoints){

        LinkedList<Edge> pathEdges = new LinkedList<>();
        Node startNode = waypoints.getFirst();

        //Check to see if the start node is valid; if not, use a default starting position.
        //TODO: the default should be set to a real node at an actual location on the map.
        if (startNode == null || startNode.getNodeID() == null){
            startNode = new Node("Node1", NodeFloor.THIRD);
            waypoints.removeFirst();
            waypoints.addFirst(startNode);
            System.out.println("Invalid or no start node; using default starting position.");
        }

        NodeFloor floor = startNode.getFloor();

        //Starting with the second node, run the pathfinding algorithm using each node as the end node and the previous
        //node as the start node.  Assemble the lists of edges from each into a single path.
        boolean isFirst = true;
        for(Node endNode: waypoints){
            if(isFirst){    //Skip the first node, since it doesn't have a previous node to start from.
                isFirst = false;
                continue;
            }

            //check if the node is valid.  If not, throw an exception.
            try{
                // if end node is not defined then throw exception for not valid
                if (endNode == null || endNode.getNodeID() == null)
                    throw new PathfinderException("No defined end node, please define valid end location");
            }catch(PathfinderException e){
            }

            //Check to make sure the nodes are all on the same floor; if not, throw exception.
            try{
                if(!endNode.getFloor().equals(floor))
                    throw new PathfinderException("Pathfinding across multiple floors not supported.");
            }catch(PathfinderException e){
            }

            //Now, find the path from the previous waypoint to this one.
            try{
                pathEdges.addAll(A_star(startNode, endNode));   //Add this section to the rest of the path.
            }catch(PathfinderException e){
                System.out.println("Error: pathfinder exception" + e.getMessage());
                e.printStackTrace();
            }
            startNode = endNode;    //Set this waypoint as the start for the next waypoint and repeat.
        }
        //At this point, pathEdges should contain a full list of edges from the first to the last waypoint, passing
        //through all waypoints in between.

        //Combine the list of waypoints and the path of edges into a Path object and return.
        return(new Path(waypoints, pathEdges));
    }

    /**
     * An alternate call for generatePath for use without intermediate waypoints.  If a user has only a start and an end
     * point and no other waypoints in between, they just pass in the two nodes rather than having to assemble them into
     * a list first.
     * @param startNode
     * @param endNode
     * @return A Path object containing the waypoints and a list of edges marking a path between them.
     */
    public static Path generatePath(Node startNode, Node endNode){
        LinkedList<Node> waypoints = new LinkedList<>();
        waypoints.add(startNode);
        waypoints.add(endNode);
        return(generatePath(waypoints));
    }

    /**
     * Given two nodes, uses the A* search algorithm to find a path between them.
     * @param startingNode
     * @param endingNode
     * @return
     * @throws PathfinderException if pretty much anything goes wrong; TODO: improve this (specific exceptions for different errors).
     */
    private static LinkedList<Edge> A_star(Node startingNode, Node endingNode) throws PathfinderException {
        MapEntity map = MapEntity.getInstance();

        StartNode startNode = new StartNode(startingNode);
        PathfindingNode endNode = new PathfindingNode(endingNode);

        // create lists for explored, frontier and unexplored nodes
        HashMap<String, PathfindingNode> exploredNodes = new HashMap<>();
        HashMap<String, PathfindingNode> frontierNodes = new HashMap<>();

        //TODO: if only handling paths on single floor, only need to read in nodes for that floor.
        // list of unexplored nodes initialized as all nodes
        LinkedList<Node> allNodes = map.getAllNodes();
        HashMap<String, PathfindingNode> unexploredNodes = new HashMap<>();
        for(Node node : allNodes) {
            if(!unexploredNodes.containsKey(node.getNodeID()))
                unexploredNodes.put(node.getNodeID(), new PathfindingNode(node));
        }

        // if either node is not located on map throw exception
        if(!(unexploredNodes.containsKey(startNode.getNode().getNodeID()) &&
             unexploredNodes.containsKey(endNode.getNode().getNodeID())))
            throw new PathfinderException("Nodes are not on map");

        //move startNode to Frontier
        unexploredNodes.remove(startNode.getNode().getNodeID());
        startNode.prepForFrontier(null, endNode);
        frontierNodes.put(startNode.getNode().getNodeID(), startNode);

        // initialize lowest cost node
        PathfindingNode lowestCost = null;
        // while loop for generating path of connecting nodes
        //TODO: add actual loop logic
        while(true){

            // if list of frontier becomes empty break out of while loop
            if (frontierNodes.isEmpty())
                break;

            // initialize lowest cost node
            // TODO hack
            for (PathfindingNode node : frontierNodes.values()) {
                lowestCost = node;
                break;
            }

            // go through all nodes in list and find the one with the lowest total cost and replace that as
            // the lowestCost node
            for(Map.Entry<String, PathfindingNode> entry : frontierNodes.entrySet()){
                if(entry.getValue().getTotalCost() < lowestCost.getTotalCost())
                    lowestCost = entry.getValue();
            }

            exploredNodes.put(lowestCost.getNode().getNodeID(), lowestCost);
            frontierNodes.remove(lowestCost.getNode().getNodeID());
            // if lowest cost node = end node break out of while loop
            if(lowestCost == endNode)
                break;

            LinkedList<Node> adjacentNodes = map.getConnectedNodes(lowestCost.getNode());
            //for each node, check if it's already been explored/is in the frontier; if not, move it in.
            for(Node node : adjacentNodes){
                if(!(exploredNodes.containsKey(node.getNodeID()) || frontierNodes.containsKey(node.getNodeID()))){
                    unexploredNodes.get(node.getNodeID()).prepForFrontier(lowestCost, endNode);
                    frontierNodes.put(node.getNodeID(), unexploredNodes.get(node.getNodeID()));
                    unexploredNodes.remove(node.getNodeID());
                }
            }
        }

        PathfindingNode lastNode = lowestCost;

        LinkedList<Edge> pathEdges = lastNode.buildPath();

        // handler for no path found
        if(pathEdges == null)throw new PathfinderException("No Path was found, Please choose another path");
        // return generated path of nodes
        return pathEdges;
    }
}
