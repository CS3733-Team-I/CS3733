package pathfinder;

import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class A_star extends Dijkstra {
    /**
     * Set parent node and calculate cost attributes in preparation for adding the node to the frontier.
     * @param node current node to do calculations on
     * @param parent node that you came from
     * @param endNode node after this node
     */
    @Override
    public void prepForFrontier(PathfinderNode node, PathfinderNode parent, PathfinderNode endNode){
        node.setParentNode(parent);
        int previousCost = node.calculatePreviousCost(parent);
        node.setPreviousCost(previousCost);
        node.setTotalCost(node.getPreviousCost() + heuristic(node, endNode));
    }

    @Override
    void calculateCosts(PathfinderNode node, PathfinderNode newParent, PathfinderNode endNode, int newCost){
        node.setParentNode(newParent);
        node.setPreviousCost(newCost);
        node.setTotalCost(newCost + heuristic(node, endNode));
        node.recalculateCosts();
    }

    /**
     * Estimates the distance between two nodes.
     * @param node1 a pathfinding node
     * @param node2 another pathfinding node
     * @return  An estimate of the distance between node1 and node2.
     */
    private int heuristic(PathfinderNode node1, PathfinderNode node2){
        double xDistance = node1.getNode().getXcoord() - node2.getNode().getXcoord();
        double yDistance = node1.getNode().getYcoord() - node2.getNode().getYcoord();
        return (int) Math.sqrt((Math.pow(xDistance, 2) + Math.pow(yDistance, 2)));
    }
}
