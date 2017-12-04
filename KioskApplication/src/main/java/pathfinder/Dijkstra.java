package pathfinder;

import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Dijkstra implements SearchAlgorithm {
    /**
     * Uses Dijkstra's algorithm to find a path between two nodes.
     * @param startingNode node to start navigation from
     * @param endingNode node to end navigation at
     * @return LinkedList<Edge> a linked list of edges
     * @throws PathfinderException if there are errors
     */
    @Override
    public LinkedList<Edge> findPath(Node startingNode, Node endingNode, boolean wheelchairAccessible) throws PathfinderException {
        MapEntity map = MapEntity.getInstance();

        StartNode startNode = new StartNode(startingNode);
        PathfinderNode endNode = new PathfinderNode(endingNode);

        //Create lists to store all the nodes in the area you're searching.
        HashMap<String, PathfinderNode> unexploredNodes = new HashMap<>();
        HashMap<String, PathfinderNode> exploredNodes = new HashMap<>();
        //The frontier is a list of all the nodes you want to explore next.  Once we start searching, it will be the
        //set of unexplored nodes that are adjacent to at least one explored node.
        HashMap<String, PathfinderNode> frontierNodes = new HashMap<>();

        //Next, get a list of all the nodes in the area you want to search (in this case, the whole map).
        LinkedList<Node> allNodes = map.getAllNodes();

        //At start, no nodes have been explored, so put them all in the Unexplored list.
        for(Node node : allNodes) {
            if(!unexploredNodes.containsKey(node.getNodeID()))
                unexploredNodes.put(node.getNodeID(), new PathfinderNode(node));
        }

        //Check to make sure the start and end nodes are both actually in the search area.
        if(!(unexploredNodes.containsKey(startNode.getNode().getNodeID()) &&
                unexploredNodes.containsKey(endNode.getNode().getNodeID())))
            throw new PathfinderException("Nodes are not on map");

        //The first node you want to explore is the start node, so move it to the frontier.
        unexploredNodes.remove(startNode.getNode().getNodeID());
        prepForFrontier(startNode, null, endNode);
        frontierNodes.put(startNode.getNode().getNodeID(), startNode);

        //Now, start searching.
        PathfinderNode lowestCost = null;
        while(true){

            //Check to see if there's anywhere in the frontier left to search.
            if (frontierNodes.isEmpty())
                throw new PathfinderException("No path found.");

            //If there is, check which node in the frontier has the lowest estimated cost.  Start by pick any node from
            //the frontier.  TODO: this <for-loop, break after one iteration> is a bad way to pick one item from a list;
            //                     need a better way to do this, but I can't find a better way to pick a random item
            //                     from a hashmap.
            for (PathfinderNode node : frontierNodes.values()) {
                lowestCost = node;
                break;
            }
            //Then, go through all the frontier nodes, looking for the node with the lowest tentative cost. Any
            //time you find a node with a lower cost than your selected node, change your selection to that node.
            for(Map.Entry<String, PathfinderNode> entry : frontierNodes.entrySet()){
                if(entry.getValue().getPreviousCost() < lowestCost.getPreviousCost())
                    lowestCost = entry.getValue();
            }

            //Now, we should have the lowest-cost node.  This is the next node we want to explore, so move it to the
            //explored list and start exploring.
            exploredNodes.put(lowestCost.getNode().getNodeID(), lowestCost);
            frontierNodes.remove(lowestCost.getNode().getNodeID());
            //First, check to see if this node is our ending node.  If so, we're done searching.
            if(lowestCost.getNode().getNodeID().equals(endNode.getNode().getNodeID()))
                break;

            //If not, start exploring the nodes bordering this one.
            LinkedList<Node> adjacentNodes = map.getConnectedNodes(lowestCost.getNode(), wheelchairAccessible);
            //for each node, check if it's already been explored/is in the frontier.
            for(Node node : adjacentNodes){
                //If the node has already been reached, mark it.
                PathfinderNode foundNode = null;
                if(exploredNodes.containsKey(node.getNodeID())){
                    foundNode = exploredNodes.get(node.getNodeID());
                }
                else if(frontierNodes.containsKey(node.getNodeID())){
                    foundNode = frontierNodes.get(node.getNodeID());
                }
                //If not, move it into the frontier.
                else{
                    prepForFrontier(unexploredNodes.get(node.getNodeID()), lowestCost, endNode);
                    frontierNodes.put(node.getNodeID(), unexploredNodes.get(node.getNodeID()));
                    unexploredNodes.remove(node.getNodeID());
                }
                //If we found a new way to get to a node, check to see if our new way is a faster way to get there.
                if(foundNode != null){
                    int newCost = foundNode.calculatePreviousCost(lowestCost);
                    //If we have, then set the node we just got here from to the new parent and recalculate costs.
                    if(newCost < foundNode.getPreviousCost()){
                        this.updateParent(foundNode, lowestCost, endNode, newCost);
                    }
                }
            }
        }

        PathfinderNode lastNode = lowestCost;
        LinkedList<Edge> pathEdges = lastNode.buildPath();
        // return generated path of nodes
        return pathEdges;
    }

    /**
     * Set parent node and calculate cost attributes in preparation for adding the node to the frontier.
     * @param node current node to do calculations on
     * @param parent node that you came from
     * @param endNode node after this node
     */
    public void prepForFrontier(PathfinderNode node, PathfinderNode parent, PathfinderNode endNode){
        node.setParentNode(parent);
        int previousCost = node.calculatePreviousCost(parent);
        node.setPreviousCost(previousCost);
    }

    /**
     * Calculates cost associated with a new parent for a given node and sets that parent.
     * @param node
     * @param newParent the node being set as the new parent for node.
     * @param endNode   the last node in the path.
     * @param newCost
     */
    void updateParent(PathfinderNode node, PathfinderNode newParent, PathfinderNode endNode, int newCost){
        node.setParentNode(newParent);
        node.setPreviousCost(newCost);
        node.recalculateCosts();
    }
}
