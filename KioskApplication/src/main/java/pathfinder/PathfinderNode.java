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
        MapEntity map = MapEntity.getInstance();
        return (map.getConnectingEdge(this.node, potentialParent.getNode()).getCost() + potentialParent.getPreviousCost());
    }

    public Node getNode() {
        return node;
    }

    public int getPreviousCost() {
        return previousCost;
    }

    /**
     * Gets a list of all connected nodes from the map, converts them into PathfindingNodes, and returns them.
     * @param wheelchairAccessible if  this is true, only gets nodes reachable by wheelchair-accessible edges.
     * @return
     */
    public LinkedList<PathfinderNode> getConnectedNodes(boolean wheelchairAccessible){
        MapEntity map = MapEntity.getInstance();
        LinkedList<Node> nodesList = map.getConnectedNodes(node, wheelchairAccessible);
        LinkedList<PathfinderNode> pathfinderNodeList = new LinkedList<>();
        for(Node n: nodesList){
            pathfinderNodeList.add(new PathfinderNode(n));
        }
        return pathfinderNodeList;
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
