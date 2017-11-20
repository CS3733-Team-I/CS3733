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
        //LinkedList<Node> allNodes = map.getAllNodes();

        if(startingNode.getNode().getNodeID().equals(endNode.getNodeID())){
            endingNode.setParentNode(startingNode);
            return endingNode.buildPath();
        }

        LinkedList<PathfinderNode> queue = new LinkedList<>();
        ArrayList<PathfinderNode> explored = new ArrayList<>();
        queue.add(startingNode);
        PathfinderNode previousNode = null;

        while(!queue.isEmpty()){
            PathfinderNode current = queue.remove();
            if(current.node.getNodeID().equals(endingNode.getNode().getNodeID())) {
                endingNode.setParentNode(previousNode);
                return endingNode.buildPath();
            }
            else{
                if(getAndCheckForConnectedNodes(explored,current,map).isEmpty()){
                    queue.add(current.parentNode);//throw new PathfinderException("No path found"); check this
                }
                else{
                    queue.addAll(getAndCheckForConnectedNodes(explored,current,map));
                }
            }
            current.setParentNode(previousNode);
            explored.add(current);
            previousNode = current;
        }
        return endingNode.buildPath();
    }

    LinkedList<PathfinderNode> getAndCheckForConnectedNodes(ArrayList<PathfinderNode> listExplored, PathfinderNode pathfinderNode, MapEntity mapEntity){
        LinkedList<PathfinderNode> connectedNodes = pathfinderNode.getConnectedNodes(mapEntity);
        LinkedList<PathfinderNode> holder = new LinkedList<>();
        holder.addAll(connectedNodes);
        for(PathfinderNode pathfinderNode1: connectedNodes){
            for (PathfinderNode explored: listExplored){
                if(pathfinderNode1.node.getNodeID().equals(explored.getNode().getNodeID())){
                    holder.remove(pathfinderNode1);
                }
            }
        }
        return holder;
    }
}

