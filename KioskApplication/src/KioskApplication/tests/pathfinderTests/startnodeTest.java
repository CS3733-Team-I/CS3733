package KioskApplication.tests.pathfinderTests;

import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;
import KioskApplication.entity.MapEntity;
import KioskApplication.pathfinder.StartNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class startnodeTest {

    private StartNode startNode;

    @Before
    public void setup(){
        MapEntity map = MapEntity.getInstance();
        startNode = new StartNode(new Node("Node1", "3"));
        startNode.getNode().setXcoord(1);
        startNode.getNode().setYcoord(9);
    }

    @Test
    public void testLinkedListisEmpty(){
        LinkedList<Edge> startnodeedges = startNode.buildPath();
        assertTrue(startnodeedges.isEmpty());
    }

    @Test
    public void testPreviousCost(){
       assertEquals(0, startNode.calculatePreviousCost());
    }

    // maybe make a test that checks if connection to Pathfinding Node is working
    //@Test
    //public void testlinktoPathfindingNode(){

    //}

}
