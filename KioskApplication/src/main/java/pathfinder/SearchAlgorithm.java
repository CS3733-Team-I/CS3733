package pathfinder;

import database.objects.Edge;
import database.objects.Node;

import java.util.LinkedList;

public interface SearchAlgorithm {
    LinkedList<Edge> findPath(Node startNode, Node endNode) throws PathfinderException;
}
