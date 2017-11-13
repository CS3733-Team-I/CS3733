package KioskApplication.pathfinder;

import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;
import KioskApplication.entity.MapEntity;

import java.util.LinkedList;

public class Pathfinder {

    public Pathfinder(){
    }

    // generate path function
    public static LinkedList<Edge> GeneratePath(Node startingNode, Node endingNode){
        MapEntity map = MapEntity.getInstance();
        StartNode startNode = new StartNode(startingNode);
        PathfindingNode endNode = new PathfindingNode(endingNode);

        // create lists for explored, frontier and unexplored nodes
        LinkedList<PathfindingNode> ListOfExplored = new LinkedList<>();
        LinkedList<PathfindingNode> ListOfFrontier = new LinkedList<>();

        //TODO: if only handling paths on single floor, only need to read in nodes for that floor.
        // list of unexplored nodes initialized as all nodes
        LinkedList<Node> allNodes = map.getAllNodes();
        LinkedList<PathfindingNode> ListOfUnexplored = new LinkedList<PathfindingNode>();
        for(Node node : allNodes)
            ListOfUnexplored.add(new PathfindingNode(node));

        //TODO: add proper handling for start and end nodes on different floors; for now, just returns null.
        if(!startNode.getNode().getFloor().equals(endNode.getNode().getFloor()))
            return null;

        //TODO: make sure both nodes are in the map. Also, make sure contains() is used properly (value vs. reference)
        if(!(ListOfUnexplored.contains(startNode) && ListOfUnexplored.contains(endNode)))
            return null;
        // remove start node from unexplored list
        ListOfUnexplored.remove(startNode);

        // add to frontier list the start node with parent node
        startNode.prepForFrontier(null, endNode);
        ListOfFrontier.add(startNode);
        // initialize lowest cost node
        PathfindingNode lowestCost = null;
        // while loop for generating path of connecting nodes
        while(true){
            //TODO add handler for Default StartNode

            // if list of frontier becomes empty break out of while loop
            if (ListOfFrontier.isEmpty())
                break;

            // initialize lowest cost node
            lowestCost = ListOfFrontier.get(0);

            // TODO ADD Handling for if no path is found
            // go through all nodes in list and find the one with the lowest total cost and replace that as
            // the lowestCost node
            for (PathfindingNode f: ListOfFrontier) {

                if(f.getTotalCost() < lowestCost.getTotalCost())
                   lowestCost = f;
            }

            ListOfExplored.add(lowestCost);
            ListOfFrontier.remove(lowestCost);
            // if lowest cost node = end node break out of while loop
            if(lowestCost == endNode)
                break;

            LinkedList<Node> adjacentNodes = map.getConnectedNodes(lowestCost.getNode());
            for(Node node : adjacentNodes){

            }
            ListOfFrontier.addAll(lowestCost.generateListOfConnectedNodes());
            ListOfUnexplored.remove(ListOfFrontier);

        }

        PathfindingNode x = lowestCost;

        LinkedList<Edge> Path = x.buildPath();

        // return generated path of nodes
        return Path;
    }
}
