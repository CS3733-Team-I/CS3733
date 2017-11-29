package entity;

import database.objects.Edge;
import database.objects.Node;

import java.util.LinkedList;
import java.util.List;

public class Path {
    private LinkedList<Node> waypoints;
    private LinkedList<Edge> edges;
    private LinkedList<String> directions;

    public Path(List<Node> waypoints, List<Edge> edges) {
        this.waypoints = new LinkedList<>(waypoints);
        this.edges = new LinkedList<>(edges);
        generateDirections();
    }

    public LinkedList<Node> getWaypoints() {
        return waypoints;
    }

    public LinkedList<Edge> getEdges() {
        return edges;
    }

    public LinkedList<String> getDirections() {
        return directions;
    }

    public boolean equals(Path comparePath){
        return(this.edges.equals(comparePath.getEdges()) && this.waypoints.equals(comparePath.getWaypoints()));
    }

    private void generateDirections() {
        directions.add("Start at " + waypoints.get(0).getLongName());
    }
}
