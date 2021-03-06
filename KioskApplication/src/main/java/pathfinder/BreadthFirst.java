package pathfinder;

import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;
import utility.node.NodeType;

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
    @Override
    public LinkedList<Edge> findPath(Node startNode, Node endNode, boolean wheelchairAccessible)throws PathfinderException{
            MapEntity map = MapEntity.getInstance();

            StartNode startingNode = new StartNode(startNode);
            PathfinderNode endingNode = new PathfinderNode(endNode);

        if(startingNode.getNode().getNodeID().equals(endNode.getNodeID()))
            return startingNode.buildPath();

        // make linked list for queue and explored
        LinkedList<PathfinderNode> queue = new LinkedList<>();
        ArrayList<PathfinderNode> explored = new ArrayList<>();
        queue.add(startingNode);

        while(!queue.isEmpty()){
            // remove first in index
            PathfinderNode current = queue.remove(0);
            if(current.node.getNodeID().equals(endingNode.getNode().getNodeID())) {
                // we found our path now generate it
                return current.buildPath();
            }
            else{
                if(getAndCheckForConnectedNodes(explored, current, wheelchairAccessible).isEmpty()){
                    // no path is found throw excepetion
                }
                else{
                    // add connected nodes not explored in queue
                    queue.addAll(getAndCheckForConnectedNodes(explored, current, wheelchairAccessible));
                }
            }
            // add to explored list
            explored.add(current);
        }

        throw new PathfinderException("No path found");
    }

    /**
     * check for the connected nodes in a list and add to connected nodes list
     * and the parent node if it there is none
     * @param listExplored
     * @param pathfinderNode
     * @param mapEntity
     * @return
     */
    private LinkedList<PathfinderNode> getAndCheckForConnectedNodes(ArrayList<PathfinderNode> listExplored, PathfinderNode pathfinderNode, boolean wheelchairAccessible){
        // initalize connected nodes list from current node
        LinkedList<PathfinderNode> connectedNodes = pathfinderNode.getConnectedNodes(wheelchairAccessible);
        LinkedList<PathfinderNode> holder = new LinkedList<>();
        holder.addAll(connectedNodes);
        for(PathfinderNode pathfinderNode1: connectedNodes){
            // add parent node for edge generation if partent node is not created
            if(pathfinderNode1.parentNode == null && pathfinderNode !=null)
                pathfinderNode1.parentNode = pathfinderNode;
            for (PathfinderNode explored: listExplored){
                // remove from queue list if in explored list
                if(pathfinderNode1.node.getNodeID().equals(explored.getNode().getNodeID())){
                    holder.remove(pathfinderNode1);
                }
            }
        }
        return holder;
    }

    /**
     * Uses the Breadth First search algorithm to find a nearest node of required type
     * @param startNode node to start navigation from
     * @param nodeType  required nodeType
     * @return nearest node of required type
     * @throws PathfinderException if there are errors
     */
    public Node findPathToNearestType(Node startNode, NodeType nodeType, boolean wheelchairAccessible) throws PathfinderException {
        MapEntity map = MapEntity.getInstance();

        StartNode startingNode = new StartNode(startNode);

        if(startingNode.getNode().getNodeType() == nodeType) {
            return startingNode.getNode();
        }

        // make linked list for queue and explored
        LinkedList<PathfinderNode> queue = new LinkedList<>();
        ArrayList<PathfinderNode> explored = new ArrayList<>();
        queue.add(startingNode);

        while(!queue.isEmpty()){
            // remove first in index
            PathfinderNode current = queue.remove(0);
            if(current.node.getNodeType() == nodeType) {
                // we found our path now generate it
                return current.node;
            }
            else{
                if(getAndCheckForConnectedNodes(explored, current, wheelchairAccessible).isEmpty()){
                    // no path is found throw excepetion
                }
                else{
                    // add connected nodes not explored in queue
                    queue.addAll(getAndCheckForConnectedNodes(explored, current, wheelchairAccessible));
                }
            }
            // add to explored list
            explored.add(current);
        }
        return null;
    }
}

