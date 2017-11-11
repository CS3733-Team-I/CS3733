package KioskApplication.tests.entityTests;

import KioskApplication.database.objects.Node;
import KioskApplication.entity.MapFloorEntity;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestMapFloorEntity {

    MapFloorEntity m;
    Node n1;

    @Before
    public void setup() {
        m = new MapFloorEntity();
        n1 = new Node("ID");
    }

    @Test
    public void testAll() {
        m.addNode(n1);
        assertEquals(m.getNode(n1.getNodeID()),n1);
        m.removeNode(n1.getNodeID());
        assertEquals(m.getNode(n1.getNodeID()), null);
    }
}
