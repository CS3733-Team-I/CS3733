package pathfinder;

import database.DatabaseController;
import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;
import org.junit.Before;
import org.junit.Test;
import utility.NodeBuilding;
import utility.NodeFloor;
import utility.NodeType;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

public class TestA_star {
    private Node n1, n2, n3, n4, n5, n6;
    private Edge e1, e2, e3, e4, e5, e6;
    private PathfinderNode pn1, pn2, pn3, pn4, pn5, pn6;
    private MapEntity map;
    private Pathfinder aStarPathfinder;
    private LinkedList<Edge> path;

    @Before
    public void setup() {
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //Build map
        DatabaseController.initTests();

        map = MapEntity.getInstance();


        n1  = new Node("NODE1",10,10, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE1_LN","NODE1_SN","I");
        n2  = new Node("NODE2",20,20, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE1_LN","NODE1_SN","I");
        n3  = new Node("NODE3",30,30, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE1_LN","NODE1_SN","I");
        n4  = new Node("NODE4",100,10, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE1_LN","NODE1_SN","I");
        n5  = new Node("NODE5",2,20, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE1_LN","NODE1_SN","I");
        n6  = new Node("NODE6",11,30, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE1_LN","NODE1_SN","I");

        e1 = new Edge("EDGE1", n1.getNodeID(), n2.getNodeID());
        e2 = new Edge("EDGE2", n2.getNodeID(), n3.getNodeID());
        e3 = new Edge("EDGE3", n1.getNodeID(), n4.getNodeID());
        e4 = new Edge("EDGE4", n4.getNodeID(), n3.getNodeID());
        e5 = new Edge("EDGE5", n3.getNodeID(), n5.getNodeID());
        e6 = new Edge("EDGE6", n5.getNodeID(), n6.getNodeID());

        map.addNode(n1);
        map.addNode(n2);
        map.addNode(n3);
        map.addNode(n4);
        map.addNode(n5);
        map.addNode(n6);

        map.addEdge(e1);
        map.addEdge(e2);
        map.addEdge(e3);
        map.addEdge(e4);
        map.addEdge(e5);
        map.addEdge(e6);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //Set up path
        A_star aStar = new A_star();
        aStarPathfinder = new Pathfinder(aStar);

        pn1 = new StartNode(n1);
        pn2 = new PathfinderNode(n2);
        pn3 = new PathfinderNode(n3);
        pn4 = new PathfinderNode(n4);
        pn5 = new PathfinderNode(n5);
        pn6 = new PathfinderNode(n6);

        aStar.prepForFrontier(pn1, null, pn6);
        aStar.prepForFrontier(pn2, null, pn6);
        aStar.prepForFrontier(pn3, null, pn6);
        aStar.prepForFrontier(pn4, null, pn6);
        aStar.prepForFrontier(pn5, null, pn6);
        aStar.prepForFrontier(pn6, null, pn6);

        path = pn3.buildPath();
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    }

//    @Test
//    public void testPrepForFrontier(){
//        //TODO: add tests
//    }
//
//    @Test
//    public void testHeuristic(){
//        //TODO: add tests
//    }
//
//    @Test
//    public void testFindPath(){
//        //TODO: add tests
//    }
}
