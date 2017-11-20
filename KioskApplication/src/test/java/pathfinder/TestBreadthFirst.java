package pathfinder;

import database.DatabaseController;
import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;
import entity.Path;
import org.junit.Before;
import org.junit.Test;
import utility.NodeFloor;

import java.util.LinkedList;

public class TestBreadthFirst {

    private Pathfinder pathfinder;
    private Node n1, n2, n3, n4, n5, n6, n7;
    private Edge e1, e2, e3, e4, e5, e6;
    private MapEntity map = MapEntity.getInstance();

    @Before
    public void setup() {

        pathfinder = new Pathfinder();

        /* Map Structure
         n1 - n2 - n3 - n5 - n6
               |
              / \
            n7   n4
        */

        DatabaseController.initTests();
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

        map.addNode(n1);
        map.addNode(n2);
        map.addNode(n3);
        map.addNode(n4);
        map.addNode(n5);
        map.addNode(n6);
        map.addNode(n7);

        e1 = new Edge("EDGE1", n1.getNodeID(), n2.getNodeID());
        e2 = new Edge("EDGE2", n2.getNodeID(), n3.getNodeID());
        e3 = new Edge("EDGE3", n2.getNodeID(), n4.getNodeID());
        e4 = new Edge("EDGE4", n2.getNodeID(), n7.getNodeID());
        e5 = new Edge("EDGE5", n5.getNodeID(), n3.getNodeID());
        e6 = new Edge("EDGE6", n5.getNodeID(), n6.getNodeID());

        map.addEdge(e1);
        map.addEdge(e2);
        map.addEdge(e3);
        map.addEdge(e4);
        map.addEdge(e5);
        map.addEdge(e6);
    }

    //TODO this doesnt test anything yet will test breadth first search soon
    @Test
    public void testBreathFirstSearch() {
        Pathfinder breadth = new Pathfinder(new BreadthFirst());
        Path path = breadth.generatePath(n1, n4);
        LinkedList<Edge> testPath2Edges = new LinkedList<>();
        testPath2Edges.add(e1);
        testPath2Edges.add(e3);

        // LinkedList<Node> testPath2Waypoints = new LinkedList<>();
        //testPath2Waypoints.add(n1);
        // testPath2Waypoints.add(n4);

        // Path testPath2 = new Path(testPath2Waypoints, testPath2Edges);

        // assertTrue(path.equals(testPath2Edges));
        // System.out.println(path2.getEdges().toString());
        // System.out.println(testPath2.getEdges().toString());
    }
}
