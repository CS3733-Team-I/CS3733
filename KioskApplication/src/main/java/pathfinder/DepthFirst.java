package pathfinder;

import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;

import java.util.LinkedList;

public class DepthFirst implements SearchAlgorithm{

    @Override
    public LinkedList<Edge> findPath(Node startingNode, Node endingNode) throws PathfinderException {
        //Get map
        MapEntity map = MapEntity.getInstance();

        //Convert the start and end nodes into pathfinding nodes
        StartNode startNode = new StartNode(startingNode);
        PathfinderNode endNode = new PathfinderNode(endingNode);

        //TODO: write algorithm

        return null;
    }
}
