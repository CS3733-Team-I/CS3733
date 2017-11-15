package KioskApplication.entity;

import KioskApplication.database.objects.Node;
import java.util.LinkedList;

public interface IMapEntity {
    public void addNode(Node n);
    public void editNode(Node n);
    public Node getNode(String s);
    public LinkedList<Node> getAllNodes();
    public void removeNode(String s); //TODO: make this take in a node object
}
