package KioskApplication.tests.entity;

import KioskApplication.database.DatabaseController;
import KioskApplication.database.objects.Node;
import KioskApplication.entity.MapFloorEntity;
import KioskApplication.utility.NodeFloor;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestMapFloorEntity {

    private MapFloorEntity m;
    private Node n1;

    public TestMapFloorEntity() {
        DatabaseController.initTests();
        m = new MapFloorEntity();
        n1 = new Node("NODE1", NodeFloor.LOWERLEVEL_1);
    }

    @Test
    public void testAddRemoveNode() {
        //Add node to map
        m.addNode(n1);
        //Test that the node exists in the map
        assertEquals(m.getNode(n1.getNodeID()), n1);
        //Remove the node
        m.removeNode(n1.getNodeID());
        //Test that the node is not in the map
        assertEquals(m.getNode(n1.getNodeID()), null);
    }
}
