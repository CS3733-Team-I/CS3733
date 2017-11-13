package KioskApplication.pathfinder;

import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;

import java.util.LinkedList;

public class StartNode extends PathfindingNode {

    public StartNode(Node node) {
        super(node);
    }

    @Override
    //TODO: recursive method to build a path from this node, its parent, etc. back to the start.
    public LinkedList<Edge> buildPath(){
        return new LinkedList<Edge>();
    }

    @Override
    //Cost to reach this node; this is the start, so cost is zero.
    public int calculatePreviousCost(){
        return 0;
    }
}
