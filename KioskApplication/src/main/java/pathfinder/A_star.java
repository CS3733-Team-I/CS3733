package pathfinder;

import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class A_star implements SearchAlgorithm {

    /**
     * Given two nodes, uses the A* search algorithm to find a path between them.
     * @param startingNode Node that the algorithm should start at
     * @param endingNode Node that the algorithm should end at
     * @return LinkedList<Edge>
     * @throws PathfinderException if pretty much anything goes wrong; TODO: improve this (specific exceptions for different errors).
     */
    public LinkedList<Edge> findPath(Node startingNode, Node endingNode) throws PathfinderException {
        MapEntity map = MapEntity.getInstance();

        StartNode startNode = new StartNode(startingNode);
        PathfinderNode endNode = new PathfinderNode(endingNode);

        // create lists for explored, frontier and unexplored nodes
        HashMap<String, PathfinderNode> exploredNodes = new HashMap<>();
        HashMap<String, PathfinderNode> frontierNodes = new HashMap<>();

        //TODO: if only handling paths on single floor, only need to read in nodes for that floor.
        // list of unexplored nodes initialized as all nodes
        LinkedList<Node> allNodes = map.getAllNodes();
        HashMap<String, PathfinderNode> unexploredNodes = new HashMap<>();
        for(Node node : allNodes) {
            if(!unexploredNodes.containsKey(node.getNodeID()))
                unexploredNodes.put(node.getNodeID(), new PathfinderNode(node));
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
        PathfinderNode lowestCost = null;
        // while loop for generating path of connecting nodes
        //TODO: add actual loop logic
        while(true){

            // if list of frontier becomes empty break out of while loop
            if (frontierNodes.isEmpty())
                break;

            // initialize lowest cost node
            // TODO hack
            for (PathfinderNode node : frontierNodes.values()) {
                lowestCost = node;
                break;
            }

            // go through all nodes in list and find the one with the lowest total cost and replace that as
            // the lowestCost node
            for(Map.Entry<String, PathfinderNode> entry : frontierNodes.entrySet()){
                if(entry.getValue().getTotalCost() < lowestCost.getTotalCost())
                    lowestCost = entry.getValue();
            }

            exploredNodes.put(lowestCost.getNode().getNodeID(), lowestCost);
            frontierNodes.remove(lowestCost.getNode().getNodeID());
            // if lowest cost node = end node break out of while loop
            if(lowestCost.getNode().getNodeID().equals(endNode.getNode().getNodeID()))
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

        PathfinderNode lastNode = lowestCost;

        LinkedList<Edge> pathEdges = lastNode.buildPath();

        // handler for no path found
        if(pathEdges.equals(null))throw new PathfinderException("No Path was found, Please choose another path");
        // return generated path of nodes
        return pathEdges;
    }
}
