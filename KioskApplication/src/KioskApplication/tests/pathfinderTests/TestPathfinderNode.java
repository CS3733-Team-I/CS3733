package KioskApplication.tests.pathfinderTests;

import KioskApplication.database.DatabaseController;
import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;
import KioskApplication.entity.MapEntity;
import KioskApplication.pathfinder.PathfinderNode;
import KioskApplication.pathfinder.StartNode;
import KioskApplication.utility.NodeBuilding;
import KioskApplication.utility.NodeFloor;
import KioskApplication.utility.NodeType;
import org.junit.Before;
import org.junit.Test;
import sun.awt.image.ImageWatched;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestPathfinderNode {

    private Node n1,n2,n3;
    private PathfinderNode pn1,pn2,pn3;

    @Before
    public void setup() {
        DatabaseController.initTests();

        n1  = new Node("NODE1",10,10, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE1_LONGNAME","NODE1_SN","I");
        n2  = new Node("NODE1",20,20, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE1_LONGNAME","NODE1_SN","I");
        n3  = new Node("NODE1",30,30, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE1_LONGNAME","NODE1_SN","I");

        pn1 = new StartNode(n1);
        pn2 = new PathfinderNode(n2);
        pn3 = new PathfinderNode(n3);
    }

    @Test
    public void testPrepForFrontier() {

        pn2.prepForFrontier(pn1,pn3);
        assertEquals(pn2.getParentNode(),pn1);
        //Straight line distance from here to previous is sqrt(100)
        assertEquals(pn2.getPreviousCost(), (int)Math.sqrt(200));
        assertEquals(pn2.getTotalCost(), (int)(Math.sqrt(200)+Math.sqrt(200)));
    }

    @Test
    public void testBuildPath() {
        MapEntity map = MapEntity.getInstance();

        map.addNode(n1);
        map.addNode(n2);
        map.addNode(n3);

        Edge e1 = new Edge("EDGE1",n1.getNodeID(),n2.getNodeID());
        Edge e2 = new Edge("EDGE2",n2.getNodeID(),n3.getNodeID());

        map.addEdge(e1);
        map.addEdge(e2);

        pn1.prepForFrontier(null, pn2);
        pn2.prepForFrontier(pn1,pn3);
        pn3.prepForFrontier(pn2,pn3);

        LinkedList<Edge> path = pn3.buildPath();
        assertTrue(path.get(0).equals(e1));
        assertTrue(path.get(1).equals(e2));
    }

    @Test
    public void testHeuristic() {
        //tested in testPrepForFrontier
    }

    @Test
    public void testCalculateCost() {
        //Tested in testPrepForFrontier
    }
}



