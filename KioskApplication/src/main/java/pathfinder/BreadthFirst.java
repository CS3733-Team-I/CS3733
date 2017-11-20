package pathfinder;

import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;

import java.util.ArrayList;
import java.util.LinkedList;

public class BreadthFirst implements SearchAlgorithm{

    /**
     * Uses the Breath First search algorithm to find a path between two nodes.
     * @param startingNode node to start navigation from
     * @param endingNode node to end navigation at
     * @return LinkedList<Edge> a linked list of edges
     * @throws PathfinderException if there are errors
     */
    @Override
    public LinkedList<Edge> findPath(Node startNode, Node endNode)throws PathfinderException{
            MapEntity map = MapEntity.getInstance();

            StartNode startingnode = new StartNode(startNode);
            PathfinderNode endingnode = new PathfinderNode(endNode);

        //Next, get a list of all the nodes in the area you want to search (in this case, the whole map).
        //TODO: if only handling paths on single floor, only need to read in nodes for that floor.
        LinkedList<Node> allNodes = map.getAllNodes();

        if(startingnode.equals(endNode)){
         return endingnode.buildPath();
        }

        LinkedList<PathfinderNode> queue = new LinkedList<>();
        ArrayList<PathfinderNode> explored = new ArrayList<>();
        queue.add(startingnode);
        explored.add(startingnode);

        while(!queue.isEmpty()){
            PathfinderNode current = queue.remove();
            if(current.equals(endingnode)) {
                return endingnode.buildPath();
            }
            else{
                if(current.childNodes.isEmpty())
                    return endingnode.buildPath();
                else
                    queue.addAll(current.childNodes);
            }
            explored.add(current);
        }

        return endingnode.buildPath();

    }



    }

