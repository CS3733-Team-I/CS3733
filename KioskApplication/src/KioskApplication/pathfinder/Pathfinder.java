package KioskApplication.pathfinder;

import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;
import KioskApplication.entity.MapEntity;

import java.nio.file.Path;
import java.util.LinkedList;

public class Pathfinder {

    public Pathfinder(){
    }

    // generate path function
    public static LinkedList<Edge> GeneratePath(Node startingNode, Node endingNode){
        MapEntity map = MapEntity.getInstance();
        StartNode startNode = new StartNode(startingNode);
        PathfindingNode endnode = new PathfindingNode(endingNode);

        // create lists for explored, frontier and unexplored nodes
        LinkedList<PathfindingNode> ListExplored = new LinkedList<>();
        LinkedList<PathfindingNode> ListofFrontier = new LinkedList<>();

        //TODO: if only handling paths on single floor, only need to read in nodes for that floor.
        // list of unexplored nodes initalized as all nodes
        LinkedList<Node> allNodes = map.getAllNodes();
        LinkedList<PathfindingNode> ListofUnexplored = new LinkedList<PathfindingNode>();
        for(Node node : allNodes)
            ListofUnexplored.add(new PathfindingNode(node));

        //TODO: add proper handling for start and end nodes on different floors; for now, just returns null.
        if(!startNode.getNode().getFloor().equals(endnode.getNode().getFloor()))
            return null;

        //TODO: make sure both nodes are in the map. Also, make sure contains() is used properly (value vs. reference)
        if(!(ListofUnexplored.contains(startNode) && ListofUnexplored.contains(endnode)))
            return null;
        // remove start node from unexplored list
        ListofUnexplored.remove(startNode);

        // add to frontier list the start node with parent node
        startNode.addToFrontier(ListofFrontier);
        // initalize lowest cost node
        PathfindingNode lowestcost = null;
        // while loop for generating path of connecting nodes
        while(true){
            //TODO add handler for Default StartNode

            // if list of frontier becomes empty break out of while loop
            if (ListofFrontier.isEmpty())
                break;

            // initalize lowest cost node
            lowestcost = ListofFrontier.get(0);

            // TODO ADD Handling for if no path is found
            // go through all nodes in list and find the one with the lowest total cost and replace that as
            // the lowestcost node
            for (PathfindingNode f: ListofFrontier) {

                if(f.getTotalCost() < lowestcost.getTotalCost())
                   lowestcost = f;
            }

            ListExplored.add(lowestcost);
            ListofFrontier.remove(lowestcost);
            // if lowest cost node = end node break out of while loop
            if(lowestcost == endnode)
                break;

            ListofFrontier.addAll(lowestcost.generateListofConnectedNodes());
            ListofUnexplored.remove(ListofFrontier);

        }

        PathfindingNode x = lowestcost;

        LinkedList<Edge> Path = x.buildPath();

        // return generated path of nodes
        return Path;
    }

}
