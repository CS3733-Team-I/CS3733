package entity;

import database.objects.Node;
import database.utility.DatabaseException;

import java.util.LinkedList;

public interface IMapEntity {
    public void addNode(Node n) throws DatabaseException;

    public void editNode(Node n) throws DatabaseException;

    public Node getNode(String s);

    public LinkedList<Node> getAllNodes();

    public void removeNode(Node node) throws DatabaseException;

    public void removeAll() throws DatabaseException;
}
