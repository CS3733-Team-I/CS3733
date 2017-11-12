package KioskApplication.pathfinder;

import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;
import KioskApplication.entity.MapEntity;

import java.nio.file.Path;
import java.util.LinkedList;

public class Pathfinder {
    private MapEntity map;

    public Pathfinder(){
        map = MapEntity.getInstance();
    }

    public static LinkedList<Edge> GeneratePath(Node startnode, Node endnode) {
        return GeneratePath(new StartNode(startnode), new PathfindingNode(endnode));
    }

    // generate path function
    public static LinkedList<Edge> GeneratePath(StartNode startnode, PathfindingNode endnode){

        // create lists for explored, frontier and unexplored nodes
        LinkedList<PathfindingNode> ListExplored = new LinkedList<>();
        LinkedList<PathfindingNode> ListofFrontier = new LinkedList<>();

        // list of unexplored nodes initialized as all nodes
        //TODO create function that generates list of all nodes - DATABASE PEOPLE
        LinkedList<PathfindingNode> ListofUnexplored = new LinkedList<>();//getListofallNodes();

        // remove start node from unexplored list
        ListofUnexplored.remove(startnode);

        // add to frontier list the start node with parent node
        startnode.addToFrontier(ListofFrontier);
        // initialize lowest cost node
        PathfindingNode lowestcost = null;
        // while loop for generating path of connecting nodes
        while(true){
            //TODO add handler for Default StartNode

            // if list of frontier becomes empty break out of while loop
            if (ListofFrontier.isEmpty())
                break;

            // initialize lowest cost node
            lowestcost = ListofFrontier.get(0);

            // TODO ADD Handling for if no path is found
            // go through all nodes in list and find the one with the lowest total cost and replace that as
            // the lowest cost node
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
