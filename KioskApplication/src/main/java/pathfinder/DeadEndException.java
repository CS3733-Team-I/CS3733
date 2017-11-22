package pathfinder;

import database.objects.Node;

import java.util.LinkedList;

public class DeadEndException extends PathfinderException {

    private LinkedList<Node> visitedNodes;

    public DeadEndException(String message, LinkedList<Node> visitedNodes) {
        super(message);
        this.visitedNodes = visitedNodes;
    }

    public LinkedList<Node> getVisitedNodes() {
        return visitedNodes;
    }
}
