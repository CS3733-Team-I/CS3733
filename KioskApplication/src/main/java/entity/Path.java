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

    public String getDirections() {
        String returnStr = "";
        for(String s : directions) returnStr += s;
        return returnStr;
    }

    public LinkedList<String> getDirectionsList() {
        return directions;
    }

    public boolean equals(Path comparePath){
        return(this.edges.equals(comparePath.getEdges()) && this.waypoints.equals(comparePath.getWaypoints()));
    }

    private void generateDirections() {
        directions = new LinkedList<>();
        LinkedList<Node> nodes = getListOfNodes();
        directions.add("Start at " + nodes.getFirst().getLongName() + "\n");
        for(int i = 0; i < nodes.size(); i++) {
            if(i!=0 && i!=nodes.size()-1)
                directions.add("Go to " + nodes.get(i).getLongName()+ "\n");
        }

        directions.add("End at " + nodes.getLast().getLongName());
    }

    private LinkedList<Node> getListOfNodes() {

        LinkedList<Node> nodes = new LinkedList<>();
        nodes.add(waypoints.getFirst());

        for(int i = 0; i<edges.size(); i++) {
            nodes.add(getOtherNode(edges.get(i),nodes.getLast()));
        }
        return nodes;
    }

    private Node getOtherNode(Edge e, Node n) {

        MapEntity map = MapEntity.getInstance();

        if(e.getNode1ID().equals(n.getNodeID())) return map.getNode(e.getNode2ID());
        if(e.getNode2ID().equals(n.getNodeID())) return map.getNode(e.getNode1ID());
        return null;
    }
}
