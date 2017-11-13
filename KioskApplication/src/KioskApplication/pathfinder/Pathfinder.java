package KioskApplication.pathfinder;

import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;
import KioskApplication.entity.MapEntity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Pathfinder {

    public Pathfinder(){
    }

    // generate path function
    public static LinkedList<Edge> GeneratePath(Node startingNode, Node endingNode){
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

        //TODO: add proper handling for start and end nodes on different floors; for now, just returns null.
        if(!startNode.getNode().getFloor().equals(endNode.getNode().getFloor()))
            return null;

        //TODO: add handling for if either node is not in the map.
        if(!(unexploredNodes.containsKey(startNode.getNode().getNodeID()) &&
             unexploredNodes.containsKey(endNode.getNode().getNodeID())))
            return null;
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
            //TODO add handler for Default StartNode

            // if list of frontier becomes empty break out of while loop
            if (frontierNodes.isEmpty())
                break;

            // initialize lowest cost node
            lowestCost = frontierNodes.get(0);

            // TODO ADD Handling for if no path is found
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

        // return generated path of nodes
        return Path;
    }
}
