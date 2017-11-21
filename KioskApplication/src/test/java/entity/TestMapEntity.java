package entity;

import database.DatabaseController;
import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;
import utility.NodeFloor;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestMapEntity {

    private MapEntity m;
    private Node n1,n2,n3,n4;
    private Edge e1,e2,e3;

    private DatabaseController dbController;

    @Before
    public void setup() {
        dbController = DatabaseController.getTestInstance();
        m = MapEntity.getInstance();

        n1 = new Node("NODE1", NodeFloor.GROUND);
        n2 = new Node("NODE2", NodeFloor.LOWERLEVEL_1);
        n3 = new Node("NODE3", NodeFloor.LOWERLEVEL_2);
        n4 = new Node("NODE4", NodeFloor.FIRST);

        e1 = new Edge("EDGE1", "NODE1", "NODE2");
        e2 = new Edge("EDGE2", "NODE3", "NODE4");
        e3 = new Edge("EDGE3", "NODE1", "NODE4");
    }

    @Test
    public void testAddRemoveNode() {
        //Add node to map
        m.addNode(n1);
        //Test that the node exists in the map
        Node n1_actual = m.getNode(n1.getNodeID());
        assertEquals(n1_actual.getNodeID(), n1.getNodeID());
        //Remove the node
        m.removeNode(n1.getNodeID());
        //Test that the node is not in the map
        n1_actual = m.getNode(n1.getNodeID());
        assertEquals(n1_actual, null);
    }

    @Test
    public void testGetAllNodes() {
        // Add nodes to MapEntity
        m.addNode(n1);
        m.addNode(n2);
        m.addNode(n3);
        m.addNode(n4);

        // Get nodes
        LinkedList<Node> nodes = m.getAllNodes();

        assertEquals(nodes.size(), 4);

        assertTrue(nodes.contains(n1));
        assertTrue(nodes.contains(n2));
        assertTrue(nodes.contains(n3));
        assertTrue(nodes.contains(n4));

        // Clean up, remove nodes
        m.removeNode(n1.getNodeID());
        m.removeNode(n2.getNodeID());
        m.removeNode(n3.getNodeID());
        m.removeNode(n4.getNodeID());
    }

    @Test
    public void testAddRemoveEdge() {
        //Add edge to map
        m.addEdge(e1);
        //Test that the edge exists in the map
        assertEquals(m.getEdge(e1.getEdgeID()), e1);
        //Remove the edge
        m.removeEdge(e1.getEdgeID());
        //Test that the edge is not in the map
        assertEquals(m.getEdge(e1.getEdgeID()), null);
    }

    @Test
    public void testGetEdges() {
        // Add nodes and edges to map
        m.addNode(n1);
        m.addNode(n2);
        m.addNode(n3);
        m.addNode(n4);

        m.addEdge(e1);
        m.addEdge(e2);
        m.addEdge(e3);

        //Test to see which edges are connected to node 1
        ArrayList<Edge> edgesOfNode1 = m.getEdges(n1);
        assertTrue(edgesOfNode1.contains(e1) && edgesOfNode1.contains(e3));

        //Test to see which edges are connected to node 2
        ArrayList<Edge> edgesOfNode2 = m.getEdges(n2);
        assertTrue(edgesOfNode2.contains(e1));

        //Test to see which edges are connected to node 3
        ArrayList<Edge> edgesOfNode3 = m.getEdges(n3);
        assertTrue(edgesOfNode3.contains(e2));

        //Test to see which edges are connected to node 4
        ArrayList<Edge> edgesOfNode4 = m.getEdges(n4);
        assertTrue(edgesOfNode4.contains(e2) && edgesOfNode4.contains(e3));

        m.removeEdge(e1.getEdgeID());
        m.removeEdge(e2.getEdgeID());
        m.removeEdge(e3.getEdgeID());

        m.removeNode(n1.getNodeID());
        m.removeNode(n2.getNodeID());
        m.removeNode(n3.getNodeID());
        m.removeNode(n4.getNodeID());
    }

    @Test
    public void testGetConnectingEdge() {
        m.addNode(n1);
        m.addNode(n2);
        m.addEdge(e1);
        assertEquals(m.getConnectingEdge(n1,n2),e1);
    }
}
