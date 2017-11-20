package pathfinder;

import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;

import java.util.LinkedList;

public class DepthFirst implements SearchAlgorithm{

    @Override
    public LinkedList<Edge> findPath(Node startingNode, Node endingNode) throws PathfinderException {

        //Make a list of visited nodes to prevent loops
        LinkedList<Node> visitedNodes = new LinkedList<>();

        return findPath(startingNode, endingNode, visitedNodes);
    }

    //helper method that takes a list of visited nodes
    private LinkedList<Edge> findPath(Node startingNode, Node endingNode, LinkedList<Node> visitedNodes) throws PathfinderException {
        //Get map
        MapEntity map = MapEntity.getInstance();

        //return an empty list if you are at the end node
        if(startingNode.equals(endingNode)) return new LinkedList<>();

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
        LinkedList<Edge> newPath = null;
        for(Node n: connected) {
            if(!visitedNodes.contains(n)) {
                try {
                    newPath = findPath(n, endingNode, visitedNodes);
                } catch (DeadEndException e) {
                    //System.out.println("Ran into a dead end at node " + e.getMessage());
                    visitedNodes.addAll(e.getVisitedNodes());
                }
            }
        }
        if(newPath == null) throw new DeadEndException(startingNode.getNodeID(), visitedNodes);
        return newPath;
    }
}
