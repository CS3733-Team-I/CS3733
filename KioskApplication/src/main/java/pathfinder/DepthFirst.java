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

        try {
            return findPath(startingNode, endingNode, visitedNodes);
        }
        catch (DeadEndException e) {
            throw new PathfinderException(e.getMessage() + " " + e.getVisitedNodes());
        }
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
        if(!isOpenConnectedNode(startingNode,visitedNodes)) {
            throw new DeadEndException(startingNode.getNodeID(), visitedNodes);
        }

        //iterate through the connected nodes and see if there is a path to the end
        LinkedList<Edge> newPath;

        LinkedList<Node> connected = map.getConnectedNodes(startingNode);
        for(Node n: connected) {
            if(!visitedNodes.contains(n)) {
                try {
                    newPath = findPath(n, endingNode, visitedNodes);
                    returnList.add(map.getConnectingEdge(startingNode,n));
                    //System.out.println(returnList + " " + n.getNodeID());
                    returnList.addAll(newPath);
                    return returnList;
                } catch (DeadEndException e) {
                    //System.out.println("Ran into a dead end at node " + e.getMessage());
                    visitedNodes.addAll(e.getVisitedNodes());
                }
            }
        }
        throw new DeadEndException(startingNode.getNodeID(), visitedNodes);
    }

    private boolean isOpenConnectedNode(Node node, LinkedList<Node> visitedNodes) {
        MapEntity map = MapEntity.getInstance();

        LinkedList<Node> connectedNodes = map.getConnectedNodes(node);

        if(connectedNodes.size()==0) return false;

        for(Node c : connectedNodes) {
            if(!visitedNodes.contains(c)) return true;
        }

        return false;
    }
}
