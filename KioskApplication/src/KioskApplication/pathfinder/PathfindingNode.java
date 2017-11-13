package KioskApplication.pathfinder;

import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;
import KioskApplication.entity.MapEntity;

import java.util.LinkedList;

public class PathfindingNode {

    private Node node;
    private int previousCost;
    private int remainingCost;
    private int totalCost;
    private PathfindingNode parentNode;

    public PathfindingNode(Node node) {
        this.node = node;
    }

    //TODO: Calculate cost attributes and then add the node to the frontier.
    public void addToFrontier( LinkedList<PathfindingNode> ListofFrontier){

    }

    //Recursive method to build a path from this node, its parent, etc. back to the start.
    public LinkedList<Edge> buildPath(){
        //Get the path up to this point.
        LinkedList<Edge> path = this.parentNode.buildPath();
        MapEntity map = MapEntity.getInstance();
        //Add the edge connecting this node to its parent
        path.add(map.getConnectingEdge(this.node, this.parentNode.getNode()));
        return path;
    }

    //estimate the remaining cost to reach the end node from this node.  Currently uses straight-line distance.
    public int heuristic(PathfindingNode node){
        double xDistance = node.getNode().getXcoord() - this.node.getXcoord();
        double yDistance = node.getNode().getYcoord() - this.node.getYcoord();
        int straightLine = (int) Math.pow((Math.pow(xDistance, 2) + Math.pow(yDistance, 2)), -2);
        return straightLine;
    }

    //Calculates the cost to reach this node.
    public int calculatePreviousCost(){
        //Assuming all edges are straight lines, the cost of the edge from the parent node should be the straight-line
        //distance between the two.
        int xDistance = Math.abs(this.node.getXcoord() - this.parentNode.getNode().getXcoord());
        int yDistance = Math.abs(this.node.getYcoord() - this.parentNode.getNode().getYcoord());
        //Calculate distance with Pythagorean theorem
        int straightLineDistance = (int) Math.pow((Math.pow(xDistance, 2) + Math.pow(yDistance, 2)), -2);
        //Total cost to get here is the sum of the cost to get to the previous node & the cost to get from that
        //node to here.
        this.previousCost = straightLineDistance + this.parentNode.getTotalCost();
        return this.previousCost;
    }

    public Node getNode() {
        return node;
    }

    public int getPreviousCost() {
        return previousCost;
    }

    public int getRemainingCost() {
        return remainingCost;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public PathfindingNode getParentNode() {
        return parentNode;
    }

    //TODO generate list of connected nodes
    public LinkedList<PathfindingNode> generateListofConnectedNodes(){
        return new LinkedList<>();
    }
}
