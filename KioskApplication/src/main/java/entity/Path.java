package entity;

import database.objects.Edge;
import database.objects.Node;

import java.util.LinkedList;
import java.util.List;

public class Path {
    private LinkedList<Node> waypoints;
    private LinkedList<Edge> edges;

    public Path(List<Node> waypoints, List<Edge> edges) {
        this.waypoints = new LinkedList<>(waypoints);
        this.edges = new LinkedList<>(edges);
    }

    public LinkedList<Node> getWaypoints() {
        return waypoints;
    }

    public LinkedList<Edge> getEdges() {
        return edges;
    }

    public boolean equals(Path comparePath){
        return(this.edges.equals(comparePath.getEdges()) && this.waypoints.equals(comparePath.getWaypoints()));
    }
}
