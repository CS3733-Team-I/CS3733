package database;

import database.objects.Edge;
import database.objects.Node;
import database.utility.DatabaseEdgeException;
import database.utility.DatabaseException;
import entity.MapEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class EdgeTests {

    private Node n1,n2,n3,n4,n5;
    private Edge e1,e2,e3;
    private MapEntity map;

    @Before
    public void setup() throws DatabaseException{

        map = MapEntity.getInstance();

        n1 = new Node("NODE1");
        n2 = new Node("NODE2");
        n3 = new Node("NODE3");
        n4 = new Node("NODE4");
        n5 = new Node("NODE5");

        map.addNode(n1);
        map.addNode(n2);
        map.addNode(n3);
        map.addNode(n4);
        map.addNode(n5);

        e1 = new Edge("EDGE1",n1.getNodeID(), n2.getNodeID());
        e2 = new Edge("EDGE2",n3.getNodeID(), n2.getNodeID());
        e3 = new Edge("EDGE3",n4.getNodeID(), n5.getNodeID());

        map.addEdge(e1);
        map.addEdge(e2);
        map.addEdge(e3);
    }

    @Test
    public void testE1IsConnectedToE2() {
        boolean connected = e1.isConnectedTo(e2);
        assertTrue(connected);
    }
    @Test
    public void testE2IsConnectedToE1() {
        assertTrue(e2.isConnectedTo(e1));
    }
    @Test
    public void testE1IsNotConnectedToE3() {
        assertFalse(e1.isConnectedTo(e3));
    }
    @Test
    public void testE2IsNotConnectedToE3() {
        assertFalse(e2.isConnectedTo(e3));
    }
    @Test
    public void testE3IsNotConnectedToE1() {
        assertFalse(e3.isConnectedTo(e1));
    }
    @Test
    public void testE3IsNotConnectedToE2() {
        assertFalse(e3.isConnectedTo(e2));
    }

    @After
    public void removeAllFromDB() throws DatabaseException {
        List<Node> nodes = MapEntity.getInstance().getAllNodes();
        for (Node node : nodes) {
            MapEntity.getInstance().removeNode(node);

            ArrayList<Edge> edges = MapEntity.getInstance().getEdges(node);
            for (Edge edge : edges) MapEntity.getInstance().removeEdge(edge);
        }
    }
}
