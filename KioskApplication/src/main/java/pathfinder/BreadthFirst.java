package pathfinder;

import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;

import java.util.ArrayList;
import java.util.LinkedList;

public class BreadthFirst implements SearchAlgorithm{

    /**
     * Uses the Breadth First search algorithm to find a path between two nodes.
     * @param startNode node to start navigation from
     * @param endNode node to end navigation at
     * @return LinkedList<Edge> a linked list of edges
     * @throws PathfinderException if there are errors
     */
    //TODO this does not work yet will work on soon
    @Override
    public LinkedList<Edge> findPath(Node startNode, Node endNode)throws PathfinderException{
            MapEntity map = MapEntity.getInstance();


            StartNode startingNode = new StartNode(startNode);
            PathfinderNode endingNode = new PathfinderNode(endNode);

        //Next, get a list of all the nodes in the area you want to search (in this case, the whole map).
        //TODO: if only handling paths on single floor, only need to read in nodes for that floor.
        LinkedList<Node> allNodes = map.getAllNodes();

        if(startingNode.equals(endNode)){
            endingNode.setParentNode(startingNode);
            return endingNode.buildPath();
        }

        LinkedList<PathfinderNode> queue = new LinkedList<>();
        ArrayList<PathfinderNode> explored = new ArrayList<>();
        queue.add(startingNode);
        explored.add(startingNode);
        PathfinderNode previousNode = startingNode;

        while(!queue.isEmpty()){
            PathfinderNode current = queue.remove();
            if(current.equals(endingNode)) {
                endingNode.setParentNode(current);
                return endingNode.buildPath();
            }
            else{
                if(getAndCheckForConnectedNodes(explored,current,map).isEmpty())
                    throw new PathfinderException("No path found");
                else{

                    queue.addAll(getAndCheckForConnectedNodes(explored,current,map));
                }
            }
           // current.setParentNode(previousNode);
            explored.add(current);
        }
        return endingNode.buildPath();
    }

    LinkedList<PathfinderNode> getAndCheckForConnectedNodes(ArrayList<PathfinderNode> listExplored, PathfinderNode pathfinderNode, MapEntity mapEntity){
        LinkedList<PathfinderNode> holder = pathfinderNode.getConnectedNodes(mapEntity);
        for(PathfinderNode pathfinderNode1: holder){
            for (PathfinderNode explored: listExplored){
                if(pathfinderNode1.equals(explored)){
                    holder.remove(pathfinderNode1);
                }
            }
        }
        return holder;
    }
}

