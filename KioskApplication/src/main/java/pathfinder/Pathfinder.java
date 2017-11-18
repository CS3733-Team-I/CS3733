package pathfinder;

import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;
import entity.Path;
import utility.NodeFloor;

import java.util.*;

public class Pathfinder {

    SearchAlgorithm searchAlgorithm;

    public Pathfinder(SearchAlgorithm searchAlgorithm) {
        this.searchAlgorithm = searchAlgorithm;
    }

    /**
     * Given a list of multiple waypoints, finds a path between them.  Returns a Path object containing the list of
     *  the waypoints and a list of edges defining a path from the first to the last waypoint that passes through all
     *  the waypoints in between, in order.
     *
     * @param waypoints A list of nodes through which the path should pass
     * @return A Path object containing the waypoints and a list of edges marking a path between them.
     */
    public Path generatePath(LinkedList<Node> waypoints){

        LinkedList<Edge> pathEdges = new LinkedList<>();
        Node startNode = waypoints.getFirst();

        //Check to see if the start node is valid; if not, use a default starting position.
        //TODO: the default should be set to a real node at an actual location on the map.
        if (startNode.equals(null) || startNode.getNodeID().equals(null)){
            startNode = new Node("Node1", NodeFloor.THIRD);
            waypoints.removeFirst();
            waypoints.addFirst(startNode);
            System.out.println("Invalid or no start node; using default starting position.");
        }

        NodeFloor floor = startNode.getFloor();

        //Starting with the second node, run the pathfinder algorithm using each node as the end node and the previous
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
                if (endNode.equals(null) || endNode.getNodeID().equals(null))
                    throw new PathfinderException("No defined end node, please define valid end location");
            }catch(PathfinderException e){
            }

            //Check to make sure the nodes are all on the same floor; if not, throw exception.
            try{
                if(!endNode.getFloor().equals(floor))
                    throw new PathfinderException("Pathfinder across multiple floors not currently supported.");
            }
            catch(PathfinderException e){
            }

            //Now, find the path from the previous waypoint to this one.
            try{
                pathEdges.addAll(searchAlgorithm.findPath(startNode, endNode));   //Add this section to the rest of the path.
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
     * @param startNode Node that the algorithm should start at
     * @param endNode Node that the algorithm should end at
     * @return A Path object containing the waypoints and a list of edges marking a path between them.
     */
    public Path generatePath(Node startNode, Node endNode){
        LinkedList<Node> waypoints = new LinkedList<>();
        waypoints.add(startNode);
        waypoints.add(endNode);
        return(generatePath(waypoints));
    }

    public void setSearchAlgorithm(SearchAlgorithm searchAlgorithm) {
        this.searchAlgorithm = searchAlgorithm;
    }
}
