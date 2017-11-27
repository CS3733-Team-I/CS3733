package entity;

import database.objects.Node;
import java.util.LinkedList;

public interface IMapEntity {
    public void addNode(Node n);

    public void editNode(Node n);

    public Node getNode(String s);

    public LinkedList<Node> getAllNodes();

    public void removeNode(Node node);
}
