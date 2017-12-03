package pathfinder;

import database.objects.Edge;
import database.objects.Node;

import java.util.LinkedList;


public interface SearchAlgorithm {
    /**
     * Given two nodes, finds a path between them.
     * @param startNode node that the path should start at
     * @param endNode node that the path should end at
     * @return LinkedList<Edge> List of edges defining a valid path between startNode and endNode.
     * @throws PathfinderException if pretty much anything goes wrong; TODO: improve this (specific exceptions for different errors).
     */
    LinkedList<Edge> findPath(Node startNode, Node endNode, boolean wheelchair) throws PathfinderException;
}
