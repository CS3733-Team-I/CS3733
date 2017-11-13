package KioskApplication.entity;

import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;

import java.util.ArrayList;
import java.util.LinkedList;

public interface IMapEntity {
    public void addNode(Node n);
    public Node getNode(String s);
    public LinkedList<Node> getAllNodes();
    public void removeNode(String s);
    public Edge getConnectingEdge(Node node1, Node node2);
    public LinkedList<Node> getConnectedNodes(Node node);
}
