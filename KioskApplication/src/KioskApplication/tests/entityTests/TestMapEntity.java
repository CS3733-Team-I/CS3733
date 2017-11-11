package KioskApplication.tests.entityTests;

import KioskApplication.database.objects.Node;
import KioskApplication.entity.MapEntity;
import KioskApplication.entity.MapFloorEntity;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestMapEntity {

    MapEntity m;
    Node n1;

    @Before
    public void setup() {
        m = new MapEntity();
        n1 = new Node("ID");
    }

    @Test
    public void testAll() {
        //TODO
    }
}
