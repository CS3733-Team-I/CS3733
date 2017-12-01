package pathfinder;

import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;
import utility.node.NodeType;

import java.util.LinkedList;

public class PathfinderNode {

    protected Node node;
    protected int previousCost;
    protected int totalCost;
    protected PathfinderNode parentNode;
    private   LinkedList<PathfinderNode> childNodes;

    public PathfinderNode(Node node) {
        this.node = node;
        this.parentNode = null;
        this.childNodes = new LinkedList<>();

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

    //Calculates the cost to reach this node and the estimated total cost from here to the end.
    public int calculatePreviousCost(PathfinderNode potentialParent){
        //If the two nodes are both stairs or elevators and are on different floors, the edge between them represents
        //a staircase or an elevator shaft, respectively.  In either case, assign a cost to approximate the difficulty
        //of taking a staircase or an elevator up or down a single floor (same weight whether up or down).
        if(this.node.getNodeType().equals(NodeType.STAI) &&
           potentialParent.getNode().getNodeType().equals(NodeType.STAI) &&
           !this.node.getFloor().equals(potentialParent.getNode().getFloor()))
            return(100 + potentialParent.getPreviousCost()); //TODO: add some kind of scaling/constant rather than an arbitrary number
        else if(this.node.getNodeType().equals(NodeType.ELEV) &&
                potentialParent.getNode().getNodeType().equals(NodeType.ELEV) &&
                !this.node.getFloor().equals(potentialParent.getNode().getFloor()))
            return(100 + potentialParent.getPreviousCost()); //TODO: add some kind of scaling/constant rather than an arbitrary number
        //Otherwise, estimate the cost as normal.
        else {
            //Assuming all edges are straight lines, the cost of the edge from the parent node should be the
            // straight-line distance between the two.
            int xDistance = Math.abs(this.node.getXcoord() - potentialParent.getNode().getXcoord());
            int yDistance = Math.abs(this.node.getYcoord() - potentialParent.getNode().getYcoord());
            //Calculate distance with Pythagorean theorem
            int straightLineDistance = (int) Math.sqrt((Math.pow(xDistance, 2) + Math.pow(yDistance, 2)));
            //Total cost to get here is the sum of the cost to get to the previous node & the cost to get from that
            //node to here.
            return (straightLineDistance + potentialParent.getPreviousCost());
        }
    }

    public Node getNode() {
        return node;
    }

    public int getPreviousCost() {
        return previousCost;
    }

    public LinkedList<PathfinderNode> getConnectedNodes(MapEntity mapEntity){
        LinkedList<Node> nodeslist = mapEntity.getConnectedNodes(node);
        LinkedList<PathfinderNode> holder = new LinkedList<>();
        for(Node n: nodeslist){
            holder.add(new PathfinderNode(n));
        }
        return holder;
    }


    public int getTotalCost() {
        return totalCost;
    }

    public PathfinderNode getParentNode() {
        return parentNode;
    }

    public void setParentNode(PathfinderNode parentNode) {
        if(this.parentNode != null)
            this.parentNode.removeChild(this);
        this.parentNode = parentNode;
        parentNode.addChild(this);
    }

    public void setPreviousCost(int previousCost) {
        this.previousCost = previousCost;
    }

    public void setTotalCost(int totalCost) {
        this.totalCost = totalCost;
    }

    private void addChild(PathfinderNode childNode) {
        this.childNodes.add(childNode);
    }

    private void removeChild(PathfinderNode childNode){
        if(this.childNodes.contains(childNode))
            this.childNodes.remove(childNode);
    }

    public void recalculateCosts(){
        this.previousCost = calculatePreviousCost(this.parentNode);
        for(PathfinderNode node: childNodes)
            node.recalculateCosts();
    }

    public LinkedList<PathfinderNode> getChildNodes() {
        return childNodes;
    }
}
