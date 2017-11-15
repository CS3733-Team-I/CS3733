package KioskApplication.tests.pathfinderTests;

import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;
import KioskApplication.entity.MapEntity;
import KioskApplication.pathfinder.StartNode;
import KioskApplication.utility.NodeFloor;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestStartNode {

    private StartNode startNode;

    @Before
    public void setup(){
        MapEntity map = MapEntity.getInstance();
        startNode = new StartNode(new Node("Node1", NodeFloor.THIRD));
        startNode.getNode().setXcoord(1);
        startNode.getNode().setYcoord(9);
    }

    @Test
    public void testLinkedListisEmpty(){
        LinkedList<Edge> startnodeedges = startNode.buildPath();
        assertTrue(startnodeedges.isEmpty());
    }

    @Test
    public void testCalculateCost(){
        startNode.calculateCost(startNode) ;
        assertEquals(0, startNode.getTotalCost());
    }

    // maybe make a test that checks if connection to Pathfinding Node is working
    //@Test
    //public void testlinktoPathfindingNode(){

    //}

}
