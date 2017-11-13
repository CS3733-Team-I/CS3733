package KioskApplication.pathfinder;

import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;

import java.util.LinkedList;

public class StartNode extends PathfindingNode {

    public StartNode(Node node) {
        super(node);
    }

    @Override
    //Recursive method to build a path from this node, its parent, etc. back to the start.
    public LinkedList<Edge> buildPath(){
        //StartNode should be the final recursion, so create a new path that will form the basis of the assembled path.
        LinkedList<Edge> path = new LinkedList<Edge>();
        //StartNode has no parent, so there's no edge to add; just return the empty path.
        return path;
    }

    @Override
    //Cost to reach this node; this is the start, so cost is zero.
    public int calculatePreviousCost(){
        return 0;
    }
}
