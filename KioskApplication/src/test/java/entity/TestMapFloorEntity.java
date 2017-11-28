package entity;

import database.DatabaseController;
import database.objects.Node;
import database.utility.DatabaseException;
import utility.node.NodeFloor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestMapFloorEntity {

    private MapFloorEntity m;
    private Node n1;

    public TestMapFloorEntity() {
        DatabaseController.getInstance();
        m = new MapFloorEntity(NodeFloor.LOWERLEVEL_1);
        n1 = new Node("NODE1", NodeFloor.LOWERLEVEL_1);
    }

    @Test
    public void testAddRemoveNode() throws DatabaseException {
        //Add node to map
        m.addNode(n1);
        //Test that the node exists in the map
        assertEquals(m.getNode(n1.getNodeID()), n1);
        //Remove the node
        m.removeNode(n1);
        //Test that the node is not in the map
        assertEquals(m.getNode(n1.getNodeID()), null);
    }
}
