package KioskApplication.tests.pathfinderTests;

import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;
import pathfinder.StartNode;
import utility.Node.NodeFloor;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestStartNode {

    private StartNode startNode;

    @Before
    public void setup(){
        startNode = new StartNode(new Node("Node1", NodeFloor.THIRD));
        startNode.getNode().setXcoord(1);
        startNode.getNode().setYcoord(9);
    }

    @Test
    public void testLinkedListIsEmpty(){
        LinkedList<Edge> startNodeEdges = startNode.buildPath();
        assertTrue(startNodeEdges.isEmpty());
    }

    @Test
    public void testCalculatePreviousCost(){
        startNode.calculatePreviousCost(startNode) ;
        assertEquals(0, startNode.getTotalCost());
    }
}
