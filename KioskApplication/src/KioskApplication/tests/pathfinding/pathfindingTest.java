package KioskApplication.tests.pathfinding;
import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;
import KioskApplication.entity.MapEntity;
import KioskApplication.pathfinder.Pathfinder;
import KioskApplication.utility.NodeFloor;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class pathfindingTest {

    private Node n1, n2, n3, n4, n5, n6, n7;
    private Edge e1, e2, e3, e4, e5, e6;

    @Before
    public void setup() {
        MapEntity map = MapEntity.getInstance();

        /* Map Structure
         n1 - n2 - n3 - n5 - n6
                 |
              n7 - n4
         */

        n1 = new Node("NODE1", NodeFloor.THIRD);
        n1.setXcoord(10);
        n1.setYcoord(10);
        n2 = new Node("NODE2", NodeFloor.THIRD);
        n2.setXcoord(20);
        n2.setYcoord(10);
        n3 = new Node("NODE3", NodeFloor.THIRD);
        n3.setXcoord(30);
        n3.setYcoord(10);
        n4 = new Node("NODE4", NodeFloor.THIRD);
        n4.setXcoord(30);
        n4.setYcoord(20);
        n5 = new Node("NODE5", NodeFloor.THIRD);
        n5.setXcoord(40);
        n5.setYcoord(10);
        n6 = new Node("NODE6", NodeFloor.THIRD);
        n6.setXcoord(50);
        n6.setYcoord(10);
        n7 = new Node("NODE7", NodeFloor.THIRD);
        n7.setXcoord(20);
        n7.setYcoord(20);

        e1 = new Edge("EDGE1", n1, n2);
        e2 = new Edge("EDGE2", n2, n3);
        e3 = new Edge("EDGE2", n2, n4);
        e4 = new Edge("EDGE2", n2, n7);
        e5 = new Edge("EDGE2", n5, n3);
        e6 = new Edge("EDGE2", n5, n6);
    }

    @Test
    public void testPath(){
        LinkedList<Edge> path1 = Pathfinder.GeneratePath(n1,n6);
        LinkedList<Edge> pathtest = new LinkedList<Edge>();
        pathtest.add(e1);
        pathtest.add(e2);
        pathtest.add(e5);
        pathtest.add(e6);

        assertEquals(path1, pathtest);
        System.out.println(path1.toString());
        System.out.println(pathtest.toString());
    }


    @Test
    public void testWrongPath(){
        LinkedList<Edge> path1 = Pathfinder.GeneratePath(n1,n6);
        LinkedList<Edge> pathtest = new LinkedList<Edge>();
        pathtest.add(e2);
        pathtest.add(e2);
        pathtest.add(e5);
        pathtest.add(e6);

        assertNotEquals(path1, pathtest);
        System.out.println(path1.toString());
        System.out.println(pathtest.toString());
    }

    @Test
    public void testAnotherPath(){
        LinkedList<Edge> path2 = Pathfinder.GeneratePath(n1,n4);
        LinkedList<Edge> pathtest2 = new LinkedList<Edge>();
        pathtest2.add(e1);
        pathtest2.add(e3);

        assertEquals(path2, pathtest2);
        System.out.println(path2.toString());
        System.out.println(pathtest2.toString());
    }


}
