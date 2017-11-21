package pathfinder;

import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;

import java.util.LinkedList;

public class DepthFirst implements SearchAlgorithm{

    /**
     * Override method that takes two nodes and then calls the helper method
     * @param startingNode node to start navigation from
     * @param endingNode node to end navigation at
     * @return LinkedList<Edge> a linked list of edges
     * @throws PathfinderException if there are errors. Uses a DeadEndException to pass up a list of visited nodes to the method that called it
     */
    @Override
    public LinkedList<Edge> findPath(Node startingNode, Node endingNode) throws PathfinderException {

        //Make a list of visited nodes to prevent loops
        LinkedList<Node> visitedNodes = new LinkedList<>();

        return findPath(startingNode, endingNode, visitedNodes);
    }

    /**
     * helper method that takes two nodes and a list of previously visited nodes and returns a list of edges
     * connecting them or throws an exception.
     * @param startingNode node to start navigation from
     * @param endingNode node to end navigation at
     * @param visitedNodes a list of nodes that have been visited and that should not be checked to avoid infinite loops
     * @return LinkedList<Edge> a linked list of edges
     * @throws PathfinderException throws a DeadEndException that contains the visited nodes for use by the caller
     */
    private LinkedList<Edge> findPath(Node startingNode, Node endingNode, LinkedList<Node> visitedNodes) throws PathfinderException {
        //Get map
        MapEntity map = MapEntity.getInstance();

        //Create list to return
        LinkedList<Edge> returnList = new LinkedList<>();

        //return an empty list if you are at the end node
        if(startingNode.equals(endingNode)) return returnList;

        //add self to list of visited nodes
        visitedNodes.add(startingNode);

        //throw an exception if there is no where else to go
        LinkedList<Node> connected = map.getConnectedNodes(startingNode);
        if(connected.size() == 0 ||
           (connected.size() == 1 && visitedNodes.contains(connected.get(0)))
          ) {
            throw new DeadEndException(startingNode.getNodeID(), visitedNodes);
        }

        //iterate through the connected nodes and see if there is a path to the end
        LinkedList<Edge> newPath;
        for(Node n: connected) {
            if(!visitedNodes.contains(n)) {
                try {
                    newPath = findPath(n, endingNode, visitedNodes);
                    returnList.addAll(newPath);
                    return returnList;
                } catch (DeadEndException e) {
                    System.out.println("Ran into a dead end at node " + e.getMessage());
                    visitedNodes.addAll(e.getVisitedNodes());
                }
            }
        }
        throw new PathfinderException(startingNode.getNodeID() + ". Got to the end of the DF search");
    }
}
