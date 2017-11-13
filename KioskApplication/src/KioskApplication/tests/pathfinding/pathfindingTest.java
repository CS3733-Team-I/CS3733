package KioskApplication.tests.pathfinding;
import KioskApplication.database.objects.Node;
import KioskApplication.utility.NodeFloor;
import org.junit.Before;
import org.junit.Test;

public class pathfindingTest {

    private Node startnode, endnode;

    @Before
    public void setup() {
        startnode = new Node("NODE1", NodeFloor.LOWERLEVEL_1);
        endnode = new Node("NODE2", NodeFloor.LOWERLEVEL_1);
    }

    @Test
    public void testforPath(){
        // assert true if we found a path
    }


}
