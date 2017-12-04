package pathfinder;

public class BestFirst extends A_star{
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
       // node.setTotalCost(node.getPreviousCost() + heuristic(node, endNode));
    }

    /**
     * @param node
     * @param newParent the node being set as the new parent for node.
     * @param endNode   the last node in the path.
     * @param newCost
     */
    @Override
    void updateParent(PathfinderNode node, PathfinderNode newParent, PathfinderNode endNode, int newCost){
        node.setParentNode(newParent);
        node.setPreviousCost(newCost);
       // node.setTotalCost(newCost + heuristic(node, endNode));
        node.recalculateCosts();
    }
}
