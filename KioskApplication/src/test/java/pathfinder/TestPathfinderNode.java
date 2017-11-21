package KioskApplication.tests.pathfinderTests;

import database.DatabaseController;
import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;
import pathfinder.A_star;
import pathfinder.Pathfinder;
import pathfinder.PathfinderNode;
import pathfinder.StartNode;
import utility.Node.NodeBuilding;
import utility.Node.NodeFloor;
import utility.Node.NodeType;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestPathfinderNode {

    private Node n1, n2, n3, n4, n5, n6;
    private Edge e1, e2, e3, e4, e5, e6;
    private PathfinderNode pn1, pn2, pn3, pn4, pn5, pn6;
    private MapEntity map;
    private Pathfinder aStarPathfinder;
    private LinkedList<Edge> path;

    @Before
    public void setup() {
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //Build map
        DatabaseController.initTests();

        map = MapEntity.getInstance();


        n1  = new Node("NODE1A",10,10, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE1_LN","NODE1_SN","I");
        n2  = new Node("NODE2A",20,20, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE1_LN","NODE1_SN","I");
        n3  = new Node("NODE3A",30,30, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE1_LN","NODE1_SN","I");
        n4  = new Node("NODE4A",100,10, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE1_LN","NODE1_SN","I");
        n5  = new Node("NODE5A",2,20, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE1_LN","NODE1_SN","I");
        n6  = new Node("NODE6A",11,30, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE1_LN","NODE1_SN","I");

        e1 = new Edge("EDGE1A", n1.getNodeID(), n2.getNodeID());
        e2 = new Edge("EDGE2A", n2.getNodeID(), n3.getNodeID());
        e3 = new Edge("EDGE3A", n1.getNodeID(), n4.getNodeID());
        e4 = new Edge("EDGE4A", n4.getNodeID(), n3.getNodeID());
        e5 = new Edge("EDGE5A", n3.getNodeID(), n5.getNodeID());
        e6 = new Edge("EDGE6A", n5.getNodeID(), n6.getNodeID());

        map.addNode(n1);
        map.addNode(n2);
        map.addNode(n3);
        map.addNode(n4);
        map.addNode(n5);
        map.addNode(n6);

        map.addEdge(e1);
        map.addEdge(e2);
        map.addEdge(e3);
        map.addEdge(e4);
        map.addEdge(e5);
        map.addEdge(e6);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //Set up path
        aStarPathfinder = new Pathfinder(new A_star());

        pn1 = new StartNode(n1);
        pn2 = new PathfinderNode(n2);
        pn3 = new PathfinderNode(n3);
        pn4 = new PathfinderNode(n4);
        pn5 = new PathfinderNode(n5);
        pn6 = new PathfinderNode(n6);

        pn2.setParentNode(pn1);
        pn3.setParentNode(pn2);
        pn4.setParentNode(pn1);
        pn5.setParentNode(pn3);
        pn6.setParentNode(pn5);

        pn1.setPreviousCost(pn1.calculatePreviousCost(null));
        pn2.setPreviousCost(pn2.calculatePreviousCost(pn2.getParentNode()));
        pn3.setPreviousCost(pn3.calculatePreviousCost(pn3.getParentNode()));
        pn4.setPreviousCost(pn4.calculatePreviousCost(pn4.getParentNode()));
        pn5.setPreviousCost(pn5.calculatePreviousCost(pn5.getParentNode()));
        pn6.setPreviousCost(pn6.calculatePreviousCost(pn6.getParentNode()));

        path = pn3.buildPath();
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    }

    @Test
    public void testSetParentNode(){
        assertEquals(pn2, pn3.getParentNode());
        assertEquals(pn1, pn2.getParentNode());

        assertTrue(pn2.getChildNodes().contains(pn3));
        assertTrue(pn1.getChildNodes().contains(pn2));
    }

    @Test
    public void testCalculatePreviousCost() {
        assertEquals(0, pn1.getPreviousCost());
        assertEquals(14, pn2.getPreviousCost());
        assertEquals(28, pn3.getPreviousCost());
        assertEquals(90, pn4.getPreviousCost());
        assertEquals(57, pn5.getPreviousCost());
        assertEquals(70, pn6.getPreviousCost());
    }

    @Test
    public void testRecalculateCosts(){
        assertEquals(0, pn1.getPreviousCost());
        assertEquals(14, pn2.getPreviousCost());
        assertEquals(28, pn3.getPreviousCost());
        assertEquals(90, pn4.getPreviousCost());
        assertEquals(57, pn5.getPreviousCost());
        assertEquals(70, pn6.getPreviousCost());

        pn3.setParentNode(pn4);
        pn3.recalculateCosts();

        assertEquals(0, pn1.getPreviousCost());
        assertEquals(14, pn2.getPreviousCost());
        assertEquals(162, pn3.getPreviousCost());
        assertEquals(90, pn4.getPreviousCost());
        assertEquals(191, pn5.getPreviousCost());
        assertEquals(204, pn6.getPreviousCost());
    }

    @Test
    public void testBuildPath() {
        assertEquals(path.get(0), e1);
        assertEquals(path.get(1), e2);
    }
}
