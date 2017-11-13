package KioskApplication.entity;

import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;

import java.util.ArrayList;
import java.util.LinkedList;

public interface IMapEntity {
    public void addNode(Node n);
    public Node getNode(String s);
    public ArrayList<Node> getAllNodes();
    public void removeNode(String s);
}
