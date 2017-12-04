package entity;

import database.DatabaseController;
import database.connection.NotFoundException;
import database.objects.Edge;
import database.objects.Node;
import database.utility.DatabaseException;
import org.junit.After;
import org.junit.BeforeClass;
import utility.node.NodeFloor;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestMapEntity {

    private MapEntity m;
    private Node n1,n2,n3,n4,n5;
    private Edge e1,e2,e3;

    public TestMapEntity() {
        m = MapEntity.getInstance();

        n1 = new Node("NODE1", NodeFloor.GROUND);
        n2 = new Node("NODE2", NodeFloor.LOWERLEVEL_1);
        n3 = new Node("NODE3", NodeFloor.LOWERLEVEL_2);
        n4 = new Node("NODE4", NodeFloor.FIRST);
        n5 = new Node("Node5", NodeFloor.FIRST);

        e1 = new Edge("EDGE1", "NODE1", "NODE2");
        e2 = new Edge("EDGE2", "NODE3", "NODE4");
        e3 = new Edge("EDGE3", "NODE1", "NODE4");
    }

    @Test(expected = NotFoundException.class)
    public void testAddRemoveNode() throws DatabaseException, NotFoundException {
        try {
            //Add node to map
            m.addNode(n1);
            //Test that the node exists in the map
            Node n1_actual = m.getNode(n1.getNodeID());
            assertEquals(n1.getNodeID(), n1_actual.getNodeID());
            //Remove the node
            m.removeNode(n1);
        }
        catch(NotFoundException exception){
            exception.printStackTrace();
            //TODO: add actual handling
        }
        //Test that the node is not in the map; should throw NotFoundException.
        m.getNode(n1.getNodeID());
    }

    @Test
    public void testGetAllNodes() throws DatabaseException {
        // Add nodes to MapEntity
        m.addNode(n1);
        m.addNode(n2);
        m.addNode(n3);
        m.addNode(n4);

        // Get nodes
        LinkedList<Node> nodes = m.getAllNodes();

        assertEquals(4, nodes.size());

        assertTrue(nodes.contains(n1));
        assertTrue(nodes.contains(n2));
        assertTrue(nodes.contains(n3));
        assertTrue(nodes.contains(n4));
    }

    @Test
    public void testAddRemoveEdge() throws DatabaseException {
        //Add nodes
        m.addNode(n1);
        m.addNode(n2);

        //Add edge to map
        m.addEdge(e1);
        //Test that the edge exists in the map
        assertEquals(e1, m.getEdge(e1.getEdgeID()));

        //Remove the edge
        m.removeEdge(e1);

        //Test that the edge is not in the map
        assertEquals(null, m.getEdge(e1.getEdgeID()));
    }

    @Test
    public void testGetEdges() throws DatabaseException {
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

        //Test that node 5 has no edges
        assertTrue(m.getEdges(n5).size() == 0);
    }

    @Test
    public void testGetConnectingEdge() throws DatabaseException {
        m.addNode(n1);
        m.addNode(n2);
        m.addEdge(e1);
        assertEquals(e1, m.getConnectingEdge(n1,n2));
    }

    @Before
    @After
    public void cleanup() throws DatabaseException {
        m.removeAll();
    }
}
