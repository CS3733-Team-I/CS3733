package KioskApplication.entity;

import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;

import java.util.ArrayList;

public interface IMapEntity {

    void addNode(Node n);
    Node getNode(String s);
    void removeNode(String s);
}