package KioskApplication.pathfinder;

import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;
import KioskApplication.entity.MapEntity;
import KioskApplication.utility.NodeFloor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Pathfinder {

    public Pathfinder(){
    }

    // generate path function
    public static LinkedList<Edge> GeneratePath(Node startingNode, Node endingNode) throws PathfindingException{
        MapEntity map = MapEntity.getInstance();


        // TODO: this node needs a location UPDATE with actual coordinates
        // if start node is not declared, then default to start position on floor 3 Node1.
        if (startingNode == null || startingNode.getNodeID() == null){
            startingNode = new Node("Node1", NodeFloor.THIRD);
        }
        // if end node is not defined then throw exception for not valid
        if (endingNode == null || endingNode.getNodeID() == null){
            throw new PathfindingException("No defined end node, please define valid end location");
        }
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

        //TODO: add proper handling for start and end nodes on different floors; for now, just returns exception
        if(!startNode.getNode().getFloor().equals(endNode.getNode().getFloor()))
            throw new PathfindingException("Multiple floors not supported");

        // if either node is not located on map throw exception
        if(!(unexploredNodes.containsKey(startNode.getNode().getNodeID()) &&
             unexploredNodes.containsKey(endNode.getNode().getNodeID())))
            throw new PathfindingException("Nodes are not on map");
        // remove start node from unexplored list
        unexploredNodes.remove(startNode.getNode().getNodeID());

        // add to frontier list the start node with parent node
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
            lowestCost = frontierNodes.get(0);

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
                if(!(exploredNodes.containsKey(node.getNodeID()) && frontierNodes.containsKey(node.getNodeID()))){
                    unexploredNodes.get(node.getNodeID()).prepForFrontier(lowestCost, endNode);
                    frontierNodes.put(node.getNodeID(), unexploredNodes.get(node.getNodeID()));
                    unexploredNodes.remove(node.getNodeID());
                }
            }
        }

        PathfindingNode lastNode = lowestCost;

        LinkedList<Edge> Path = lastNode.buildPath();

        // handeler for no path found
        if(Path == null)throw new PathfindingException("No Path was found, Please choose another path");
        // return generated path of nodes
        return Path;
    }
}
