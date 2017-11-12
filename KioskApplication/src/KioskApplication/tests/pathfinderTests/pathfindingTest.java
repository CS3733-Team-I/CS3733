package KioskApplication.tests.pathfinderTests;
import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;
import KioskApplication.entity.MapEntity;
import org.junit.Before;
import org.junit.Test;

public class pathfindingTest {

    private Node startnode, endnode;

    @Before
    public void setup() {
        startnode = new Node("NODE1", "3");
        endnode = new Node("NODE2", "3");
    }

    @Test
    public void testforPath(){
        // assert true if we found a path
    }


}
