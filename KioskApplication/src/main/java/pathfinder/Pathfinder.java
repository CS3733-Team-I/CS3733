package pathfinder;

import database.objects.Edge;
import database.objects.Node;
import entity.Path;
import entity.SystemSettings;
import utility.node.NodeFloor;

import java.util.*;

public class Pathfinder {

    private SearchAlgorithm searchAlgorithm;

    public Pathfinder(SearchAlgorithm searchAlgorithm) {
        this.searchAlgorithm = searchAlgorithm;
    }

    //Alternate constructor; if no search algorithm is selected, default to A*
    public Pathfinder() {
        this.searchAlgorithm = new A_star();
    }

    /**
     * Given a list of multiple waypoints, finds a path between them.  Returns a Path object containing the list of
     *  the waypoints and a list of edges defining a path from the first to the last waypoint that passes through all
     *  the waypoints in between, in order.
     *
     * @param waypoints A list of nodes through which the path should pass
     * @return A Path object containing the waypoints and a list of edges marking a path between them.
     */
    public Path generatePath(LinkedList<Node> waypoints) throws PathfinderException{

        LinkedList<LinkedList<Edge>> pathEdges = new LinkedList<>();
        Node startNode = waypoints.getFirst();

        //Check to see if the start node is valid; if not, use a default starting position.
        if (waypoints.size() == 1){
            startNode = SystemSettings.getInstance().getDefaultnode();  //TODO: behavior for if default start node is deleted
            if (startNode == null)
                throw new PathfinderException("No default start location. \n Please see an administrator.");
            waypoints.addLast(waypoints.removeFirst());
            waypoints.addFirst(startNode);
            //System.out.println("Invalid or no start node; using default starting position.");
        }
        //check if the node is valid.  If not, throw an exception.
        // if end node is not defined then throw exception for not valid

         //   throw new PathfinderException("No defined end node, \n please define valid end location");

        //Starting with the second node, run the pathfinder algorithm using each node as the end node and the previous
        //node as the start node.  Assemble the lists of edges from each into a single path.
        boolean isFirst = true;
        for(Node endNode: waypoints){
            if(isFirst){    //Skip the first node, since it doesn't have a previous node to start from.
                isFirst = false;
                continue;
            }


            //Now, find the path from the previous waypoint to this one.
            pathEdges.add(searchAlgorithm.findPath(startNode, endNode));  //Add this section to the rest of the path.
            startNode = endNode;    //Set this waypoint as the start for the next waypoint and repeat.
        }
        //At this point, pathEdges should contain a full list of edges from the first to the last waypoint, passing
        //through all waypoints in between.

        //Combine the list of waypoints and the path of edges into a Path object and return.
        return(new Path(waypoints, pathEdges));
    }

    public SearchAlgorithm getSearchAlgorithm() {
        return searchAlgorithm;
    }

    public void setSearchAlgorithm(SearchAlgorithm searchAlgorithm) {
        this.searchAlgorithm = searchAlgorithm;
    }
}
