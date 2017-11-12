package KioskApplication.pathfinder;

import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;

import java.util.LinkedList;

public class PathfindingNode {

    private Node node;
    private int previousCost;
    private int remainingCost;
    private int totalCost;
    private Node parentNode;

    public PathfindingNode(Node node) {
        this.node = node;
    }

    //TODO: Calculate cost attributes and then add the node to the frontier.
    public void addToFrontier(){

    }

    //TODO: recursive method to build a path from this node, its parent, etc. back to the start.
    public LinkedList<Edge> buildPath(){
        return new LinkedList<Edge>();
    }

    //TODO: estimate the remaining cost to reach the end node from this node.
    public int heuristic(){
        return 0;
    }

    //TODO: calculate the cost to reach this node based on cost to reach parent and cost from parent to here.
    public int calculatePreviousCost(){
        return 0;
    }
}
