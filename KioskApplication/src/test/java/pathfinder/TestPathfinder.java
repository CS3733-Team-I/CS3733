package pathfinder;
import database.DatabaseController;
import database.objects.Edge;
import database.objects.Node;
import database.utility.DatabaseException;
import entity.MapEntity;
import entity.Path;
import org.junit.After;
import org.junit.Before;
import utility.node.NodeBuilding;
import utility.node.NodeFloor;
import org.junit.Test;
import utility.node.NodeType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestPathfinder {

    private static Pathfinder pathfinder;
    private static Node n01, n02, n03, n04, n05, n06, n07, n08, n09, n10, n11, n12,
                 n13, n14, n15, n16, n17, n18, n19, n20, n21, n22, n23;
    private static Edge e01, e02, e03, e04, e05, e06, e07, e08, e09, e10, e11,
                 e12, e13, e14, e15, e16, e17, e18, e19, e20, e21, e22;
    private static MapEntity map;

    /* New Map Structure

          X   10    20    30    40    50    60    70    80
       Y
       10     n01 - n02 - n03 - n04 - - - - n05 - n06 - n07
                     |           |
       20           n08 - n09   n10 - - - - n15
                     |           |        /
       30           n11 - n12 - n13 - n14 - n16
                           |
       40                 n17

       50                 n18   n19

       60                 n20 - n21
                           |     |
       70                 n22 - n23

    */

    @Before //Build map for testing all algorithms
    public void setup() throws DatabaseException {
        DatabaseController.getInstance();
        map = MapEntity.getInstance();
        this.removeAllFromDB();


        n01 = new Node("NODE01",10,10, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE01_LN","NODE01_SN","I");
        n02 = new Node("NODE02",20,10, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE02_LN","NODE02_SN","I");
        n03 = new Node("NODE03",30,10, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE03_LN","NODE03_SN","I");
        n04 = new Node("NODE04",40,10, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE04_LN","NODE04_SN","I");
        n05 = new Node("NODE05",60,10, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE05_LN","NODE05_SN","I");
        n06 = new Node("NODE06",70,10, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE06_LN","NODE06_SN","I");
        n07 = new Node("NODE07",80,10, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE07_LN","NODE07_SN","I");
        n08 = new Node("NODE08",20,20, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE08_LN","NODE08_SN","I");
        n09 = new Node("NODE09",30,20, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE09_LN","NODE09_SN","I");
        n10 = new Node("NODE10",40,20, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE10_LN","NODE10_SN","I");
        n11 = new Node("NODE11",20,30, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE11_LN","NODE11_SN","I");
        n12 = new Node("NODE12",30,30, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE12_LN","NODE12_SN","I");
        n13 = new Node("NODE13",40,30, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE13_LN","NODE13_SN","I");
        n14 = new Node("NODE14",50,30, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE14_LN","NODE14_SN","I");
        n15 = new Node("NODE15",60,20, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE15_LN","NODE15_SN","I");
        n16 = new Node("NODE16",60,30, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE16_LN","NODE16_SN","I");
        n17 = new Node("NODE17",30,40, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE17_LN","NODE17_SN","I");
        n18 = new Node("NODE18",30,50, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE18_LN","NODE18_SN","I");
        n19 = new Node("NODE19",40,50, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE19_LN","NODE19_SN","I");
        n20 = new Node("NODE20",30,60, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE20_LN","NODE20_SN","I");
        n21 = new Node("NODE21",40,60, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE21_LN","NODE21_SN","I");
        n22 = new Node("NODE22",30,70, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE22_LN","NODE22_SN","I");
        n23 = new Node("NODE23",40,70, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE23_LN","NODE23_SN","I");


        map.addNode(n01); map.addNode(n02); map.addNode(n03); map.addNode(n04); map.addNode(n05);
        map.addNode(n06); map.addNode(n07); map.addNode(n08); map.addNode(n09); map.addNode(n10);
        map.addNode(n11); map.addNode(n12); map.addNode(n13); map.addNode(n14); map.addNode(n15);
        map.addNode(n16); map.addNode(n17); map.addNode(n18); map.addNode(n19); map.addNode(n20);
        map.addNode(n21); map.addNode(n22); map.addNode(n23);


        e01 = new Edge("EDGE01", n01.getNodeID(), n02.getNodeID());
        e02 = new Edge("EDGE02", n02.getNodeID(), n03.getNodeID());
        e03 = new Edge("EDGE03", n03.getNodeID(), n04.getNodeID());
        e04 = new Edge("EDGE04", n04.getNodeID(), n05.getNodeID());
        e05 = new Edge("EDGE05", n05.getNodeID(), n06.getNodeID());
        e06 = new Edge("EDGE06", n06.getNodeID(), n07.getNodeID());
        e07 = new Edge("EDGE07", n02.getNodeID(), n08.getNodeID());
        e08 = new Edge("EDGE08", n04.getNodeID(), n10.getNodeID());
        e09 = new Edge("EDGE09", n08.getNodeID(), n09.getNodeID());
        e10 = new Edge("EDGE10", n10.getNodeID(), n15.getNodeID());
        e11 = new Edge("EDGE11", n08.getNodeID(), n11.getNodeID());
        e12 = new Edge("EDGE12", n10.getNodeID(), n13.getNodeID());
        e13 = new Edge("EDGE13", n15.getNodeID(), n14.getNodeID());
        e14 = new Edge("EDGE14", n11.getNodeID(), n12.getNodeID());
        e15 = new Edge("EDGE15", n12.getNodeID(), n13.getNodeID());
        e16 = new Edge("EDGE16", n13.getNodeID(), n14.getNodeID());
        e17 = new Edge("EDGE17", n14.getNodeID(), n16.getNodeID());
        e18 = new Edge("EDGE18", n12.getNodeID(), n17.getNodeID());
        e19 = new Edge("EDGE19", n20.getNodeID(), n21.getNodeID());
        e20 = new Edge("EDGE20", n20.getNodeID(), n22.getNodeID());
        e21 = new Edge("EDGE21", n21.getNodeID(), n23.getNodeID());
        e22 = new Edge("EDGE22", n22.getNodeID(), n23.getNodeID());

        map.addEdge(e01); map.addEdge(e02); map.addEdge(e03); map.addEdge(e04); map.addEdge(e05);
        map.addEdge(e06); map.addEdge(e07); map.addEdge(e08); map.addEdge(e09); map.addEdge(e10);
        map.addEdge(e11); map.addEdge(e12); map.addEdge(e13); map.addEdge(e14); map.addEdge(e15);
        map.addEdge(e16); map.addEdge(e17); map.addEdge(e18); map.addEdge(e19); map.addEdge(e20);
        map.addEdge(e21); map.addEdge(e22);
    }

    @After //remove things from database
    public void removeAllFromDB() throws DatabaseException {
        MapEntity.getInstance().removeAll();
    }

    //A-star algorithm tests

    @Test //Tests the best path from node n01 to n07
    public void testPathAstar() {

        pathfinder = new Pathfinder(); //defaults to A-star

        Path path1 = null;
        try {
            path1 = pathfinder.generatePath(n01, n07);
        } catch (PathfinderException e) {
            e.printStackTrace();
        }

        LinkedList<Edge> testPathSegment = new LinkedList<>();
        testPathSegment.add(map.getConnectingEdge(n01,n02));
        testPathSegment.add(map.getConnectingEdge(n02,n03));
        testPathSegment.add(map.getConnectingEdge(n03,n04));
        testPathSegment.add(map.getConnectingEdge(n04,n05));
        testPathSegment.add(map.getConnectingEdge(n05,n06));
        testPathSegment.add(map.getConnectingEdge(n06,n07));

        LinkedList<Node> testPathWaypoints = new LinkedList<>();
        testPathWaypoints.add(n01);
        testPathWaypoints.add(n07);

        LinkedList<LinkedList<Edge>> testPathEdges = new LinkedList<>();
        testPathEdges.add(testPathSegment);

        Path testPath = new Path(testPathWaypoints, testPathEdges);

        //System.out.println(path1.getEdges().toString());
        //System.out.println(path1.getEdges().size());
        //System.out.println(testPath.getEdges().toString());

        assertTrue(path1.equals(testPath));
    }

    @Test //Tests that a bad path is not used (should not be needed)
    public void testWrongPathAstar() {

        pathfinder = new Pathfinder(); //defaults to A-star

        Path path1 = null;
        try {
            path1 = pathfinder.generatePath(n01, n06);
        } catch (PathfinderException e) {
            e.printStackTrace();
        }

        LinkedList<Edge> testPathSegment = new LinkedList<>();
        testPathSegment.add(map.getConnectingEdge(n01,n02));
        testPathSegment.add(map.getConnectingEdge(n02,n08));
        testPathSegment.add(map.getConnectingEdge(n08,n11));
        testPathSegment.add(map.getConnectingEdge(n11,n12));
        testPathSegment.add(map.getConnectingEdge(n12,n13));
        testPathSegment.add(map.getConnectingEdge(n13,n10));
        testPathSegment.add(map.getConnectingEdge(n10,n04));
        testPathSegment.add(map.getConnectingEdge(n04,n05));
        testPathSegment.add(map.getConnectingEdge(n05,n06));
        testPathSegment.add(map.getConnectingEdge(n06,n07));

        LinkedList<LinkedList<Edge>> testPathEdges = new LinkedList<>();
        testPathEdges.add(testPathSegment);

        LinkedList<Node> testPathWaypoints = new LinkedList<>();
        testPathWaypoints.add(n01);
        testPathWaypoints.add(n07);

        Path testPath = new Path(testPathWaypoints, testPathEdges);

        assertFalse(path1.equals(testPath));
    }

    @Test //Tests a path with multiple waypoints
    public void testMultipleWaypointAstar() {

        pathfinder = new Pathfinder(); //defaults to A-star

        LinkedList<Node> nodes = new LinkedList<>();
        nodes.add(n01);
        nodes.add(n12);
        nodes.add(n07);

        Path multipath = null;
        try {
            multipath = pathfinder.generatePath(nodes);
        } catch (PathfinderException e) {
            e.printStackTrace();
        }

        LinkedList<Edge> testPathSegment1 = new LinkedList<>();
        testPathSegment1.add(map.getConnectingEdge(n01,n02));
        testPathSegment1.add(map.getConnectingEdge(n02,n08));
        testPathSegment1.add(map.getConnectingEdge(n08,n11));
        testPathSegment1.add(map.getConnectingEdge(n11,n12));

        LinkedList<Edge> testPathSegment2 = new LinkedList<>();
        testPathSegment2.add(map.getConnectingEdge(n12,n13));
        testPathSegment2.add(map.getConnectingEdge(n13,n10));
        testPathSegment2.add(map.getConnectingEdge(n10,n04));
        testPathSegment2.add(map.getConnectingEdge(n04,n05));
        testPathSegment2.add(map.getConnectingEdge(n05,n06));
        testPathSegment2.add(map.getConnectingEdge(n06,n07));

        LinkedList<LinkedList<Edge>> testPathEdges = new LinkedList<>();
        testPathEdges.add(testPathSegment1);
        testPathEdges.add(testPathSegment2);

        Path testMultipath = new Path(nodes, testPathEdges);

        /*
        System.out.println("Expected path:");
        for(Edge edge: testMultipath.getEdges())
            System.out.println("\t" + edge.getEdgeID());
        System.out.println("Actual path:");
        for(Edge edge: multipath.getEdges())
            System.out.println("\t" + edge.getEdgeID());
         */

        assertTrue(multipath.equals(testMultipath));

    }

    @Test //TODO
    public void testPrepForFrontierAstar(){
    }

    @Test //TODO
    public void testHeuristicAstar(){
    }

    @Test //Test the FindPath method by testing all possible paths
    public void testFindPathAstar(){

        SearchAlgorithm alg = new A_star();

        testFindPath(alg);
    }

    //Depth first algorithm tests

    @Test //Test the FindPath method by testing all possible paths
    public void testFindPathDF() throws PathfinderException{

        SearchAlgorithm alg = new DepthFirst();

        testFindPath(alg);
    }

    @Test(expected = PathfinderException.class) //test that the exception is thrown when there is no path or connection
    public void testDeadEndException1DF() throws PathfinderException{
        SearchAlgorithm alg = new DepthFirst();
        alg.findPath(n18,n19);
    }

    @Test(expected = PathfinderException.class) //test that the exception is thrown when there is a path but no connection
    public void testDeadEndException2DF() throws PathfinderException{
        SearchAlgorithm alg = new DepthFirst();
        alg.findPath(n01,n19);
    }

    //Breadth first algorithm tests

    @Test
    public void testFindPathBF() {
        SearchAlgorithm alg = new BreadthFirst();

        testFindPath(alg);
    }

    @Test
    public void testBreadthFirstSearch() {
        Pathfinder breadth = new Pathfinder(new BreadthFirst());
        Path path = null;
        try {
            path = breadth.generatePath(n01, n02);
        } catch (PathfinderException e) {
            e.printStackTrace();
        }
        LinkedList<Edge> testPath2Segment = new LinkedList<>();
        testPath2Segment.add(e01);

        LinkedList<LinkedList<Edge>> testPath2Edges = new LinkedList<>();
        testPath2Edges.add(testPath2Segment);

        assertTrue(path.getEdges().equals(testPath2Edges));
    }

    @Test
    public void testBreadthFirstSearch1() {
        Pathfinder breadth = new Pathfinder(new BreadthFirst());
        Path path = null;
        try {
            path = breadth.generatePath(n01, n17);
        } catch (PathfinderException e) {
            e.printStackTrace();
        }
        LinkedList<Edge> testPath2Segment = new LinkedList<>();
        testPath2Segment.add(map.getConnectingEdge(n01,n02));
        testPath2Segment.add(map.getConnectingEdge(n02,n08));
        testPath2Segment.add(map.getConnectingEdge(n08,n11));
        testPath2Segment.add(map.getConnectingEdge(n11,n12));
        testPath2Segment.add(map.getConnectingEdge(n12,n17));

        LinkedList<LinkedList<Edge>> testPath2Edges = new LinkedList<>();
        testPath2Edges.add(testPath2Segment);


        assertTrue(path.getEdges().equals(testPath2Edges));
        // System.out.println(path.getEdges().toString());
        // System.out.println(testPath2.getEdges().toString());
    }

    @Test
    public void testBreadthFirstSearchTestPathEquality() {
        Pathfinder breadth = new Pathfinder(new BreadthFirst());
        Path path = null;
        try {
            path = breadth.generatePath(n02, n05);
        } catch (PathfinderException e) {
            e.printStackTrace();
        }
        LinkedList<Edge> testPath2Segment = new LinkedList<>();
        testPath2Segment.add(map.getConnectingEdge(n02,n03));
        testPath2Segment.add(map.getConnectingEdge(n03,n04));
        testPath2Segment.add(map.getConnectingEdge(n04,n05));
        LinkedList<Node> testPath2Waypoints = new LinkedList<>();
        testPath2Waypoints.add(n02);
        testPath2Waypoints.add(n05);

        LinkedList<LinkedList<Edge>> testPath2Edges = new LinkedList<>();
        testPath2Edges.add(testPath2Segment);

        Path testPath2 = new Path(testPath2Waypoints, testPath2Edges);

        assertTrue(path.equals(testPath2));
        // System.out.println(path2.getEdges().toString());
        // System.out.println(testPath2.getEdges().toString());
    }

    //Dijkstra's algorithm tests

    @Test
    public void testFindPathD() {
        SearchAlgorithm alg = new Dijkstra();

        testFindPath(alg);
    }

    //Test exceptions

    @Test (expected = PathfinderException.class)
    public void testPathfinderException() throws PathfinderException{
        //test that the exception is thrown when there is a path but no connection
             SearchAlgorithm alg = new BreadthFirst();
             alg.findPath(n02,n18);
    }

    //Test text directions

    @Test
    public void testGenerateDirections() throws PathfinderException{
        pathfinder = new Pathfinder();

        Path path = pathfinder.generatePath(n01,n17);

        System.out.println(path.getDirections());
    }

    //Other methods

    //Helper method to check if a path is valid
    private boolean isValidPath(Node startNode, Node endNode, LinkedList<Edge> path) {
        if(path.size() == 0) {
            return (startNode.getNodeID().equals(endNode.getNodeID()));
        }

        if(path.size() == 1) {
            return((path.get(0).getNode1ID().equals(startNode.getNodeID())) && (path.get(0).getNode2ID().equals(endNode.getNodeID())) ||
                    (path.get(0).getNode2ID().equals(startNode.getNodeID())) && (path.get(0).getNode1ID().equals(endNode.getNodeID())));
        }

        if(path.size() == 2) {
            //first edge should contain the start node
            if(!path.get(0).getNode1ID().equals(startNode.getNodeID()) && !path.get(0).getNode2ID().equals(startNode.getNodeID()))
                return false;
            //last edge should contain the end node
            if(!path.get(1).getNode1ID().equals(endNode.getNodeID()) && !path.get(1).getNode2ID().equals(endNode.getNodeID()))
                return false;
            //make sure the paths are connected
            return path.get(0).isConnectedTo(path.get(1));
        }

        for(int i = 0; i < path.size(); i++) {
            if(i==0) { //first edge should contain the start node
                if(!path.get(i).getNode1ID().equals(startNode.getNodeID()) && !path.get(i).getNode2ID().equals(startNode.getNodeID()))
                    return false;
            }
            else if(i==path.size()-1) { //last edge should contain the end node
                if(!path.get(i).getNode1ID().equals(endNode.getNodeID()) && !path.get(i).getNode2ID().equals(endNode.getNodeID()))
                    return false;
            }
            else { // should contain a node in the previous edge and in the next edge
                return path.get(i).isConnectedTo(path.get(i - 1)) && path.get(1).isConnectedTo(path.get(i + 1));
            }
        }
        return false;
    }

    //Helper method to test generating every possible path
    private void testFindPath(SearchAlgorithm alg) {

        LinkedList<Edge> path;

        // Try to test every possible path
        for(Node n1 : map.getAllNodes()) {
            for(Node n2 : map.getAllNodes()) {
                try {
                    path = alg.findPath(n1,n2);

                    if(!isValidPath(n1,n2,path)) {
                        System.out.println("Not Valid Path\nStart: " + n1.getNodeID() + " End: " + n2.getNodeID());
                        for (Edge e : path) {
                            System.out.print(e.getNode1ID() + "-" + e.getNode2ID() + " ");
                        }
                        System.out.println("\n");
                    }
                    assertTrue(isValidPath(n1,n2,path));
                }
                catch(PathfinderException e) {
                    //Don't print an error if it's node 18 or 19 cause they don't connect to anything and the error should be thrown
                    if(n1.getNodeID().equals("NODE18") || n1.getNodeID().equals("NODE19") || n2.getNodeID().equals("NODE18") || n2.getNodeID().equals("NODE19")) {
                        assertTrue(true);
                    }
                    //Don't print an error if it's going across map sections cause the error should be thrown
                    else if(n1.getNodeID().equals("NODE20") && !(n2.getNodeID().equals("NODE21") || n2.getNodeID().equals("NODE22") || n2.getNodeID().equals("NODE23")) ||
                            n1.getNodeID().equals("NODE21") && !(n2.getNodeID().equals("NODE20") || n2.getNodeID().equals("NODE22") || n2.getNodeID().equals("NODE23")) ||
                            n1.getNodeID().equals("NODE22") && !(n2.getNodeID().equals("NODE21") || n2.getNodeID().equals("NODE20") || n2.getNodeID().equals("NODE23")) ||
                            n1.getNodeID().equals("NODE23") && !(n2.getNodeID().equals("NODE21") || n2.getNodeID().equals("NODE22") || n2.getNodeID().equals("NODE20")) ||

                            n2.getNodeID().equals("NODE20") && !(n1.getNodeID().equals("NODE21") || n1.getNodeID().equals("NODE22") || n1.getNodeID().equals("NODE23")) ||
                            n2.getNodeID().equals("NODE21") && !(n1.getNodeID().equals("NODE20") || n1.getNodeID().equals("NODE22") || n1.getNodeID().equals("NODE23")) ||
                            n2.getNodeID().equals("NODE22") && !(n1.getNodeID().equals("NODE21") || n1.getNodeID().equals("NODE20") || n1.getNodeID().equals("NODE23")) ||
                            n2.getNodeID().equals("NODE23") && !(n1.getNodeID().equals("NODE21") || n1.getNodeID().equals("NODE22") || n1.getNodeID().equals("NODE20"))
                            ) {
                        assertTrue(true);
                    }
                    //Otherwise print the error
                    else {
                        System.out.println("ERROR. Start: " + n1.getNodeID() + " End: " + n2.getNodeID() + "\n");
                        assertTrue(false);
                    }
                }
            }
        }
    }
    @Test
    public void testBeamSearchTestPathEquality() {
        Pathfinder beam = new Pathfinder(new Beam());
        Path path = null;
        try {
            path = beam.generatePath(n02, n02);
        } catch (PathfinderException e) {
            e.printStackTrace();
        }
        LinkedList<Edge> testPath2Segment = new LinkedList<>();
       // testPath2Segment.add(map.getConnectingEdge(n02,n02));
        LinkedList<Node> testPath2Waypoints = new LinkedList<>();
        testPath2Waypoints.add(n02);
        testPath2Waypoints.add(n02);

        LinkedList<LinkedList<Edge>> testPath2Edges = new LinkedList<>();
        testPath2Edges.add(testPath2Segment);

        Path testPath2 = new Path(testPath2Waypoints, testPath2Edges);

        assertTrue(path.equals(testPath2));
        // System.out.println(path2.getEdges().toString());
        // System.out.println(testPath2.getEdges().toString());
    }
    @Test
    public void testBeamSearchTestPathEquality2() {
        Pathfinder beam = new Pathfinder(new Beam());
        Path path = null;
        try {
            path = beam.generatePath(n02, n03);
        } catch (PathfinderException e) {
            e.printStackTrace();
        }
        LinkedList<Edge> testPath2Segment = new LinkedList<>();
        testPath2Segment.add(map.getConnectingEdge(n02,n03));
      //  testPath2Segment.add(map.getConnectingEdge(n03,n04));
       // testPath2Segment.add(map.getConnectingEdge(n04,n05));
        LinkedList<Node> testPath2Waypoints = new LinkedList<>();
        testPath2Waypoints.add(n02);
        testPath2Waypoints.add(n03);

        LinkedList<LinkedList<Edge>> testPath2Edges = new LinkedList<>();
        testPath2Edges.add(testPath2Segment);

        Path testPath2 = new Path(testPath2Waypoints, testPath2Edges);

        assertTrue(path.equals(testPath2));
        // System.out.println(path2.getEdges().toString());
        // System.out.println(testPath2.getEdges().toString());
    }

    @Test
    public void testBeamSearchTestPathEquality3() {
        Pathfinder beam = new Pathfinder(new Beam());
        Path path = null;
        try {
            path = beam.generatePath(n02, n05);
        } catch (PathfinderException e) {
            e.printStackTrace();
        }
        LinkedList<Edge> testPath2Segment = new LinkedList<>();
        testPath2Segment.add(map.getConnectingEdge(n02,n03));
        testPath2Segment.add(map.getConnectingEdge(n03,n04));
        testPath2Segment.add(map.getConnectingEdge(n04,n05));
        LinkedList<Node> testPath2Waypoints = new LinkedList<>();
        testPath2Waypoints.add(n02);
        testPath2Waypoints.add(n05);

        LinkedList<LinkedList<Edge>> testPath2Edges = new LinkedList<>();
        testPath2Edges.add(testPath2Segment);

        Path testPath2 = new Path(testPath2Waypoints, testPath2Edges);

        assertTrue(path.equals(testPath2));
        // System.out.println(path2.getEdges().toString());
        // System.out.println(testPath2.getEdges().toString());
    }

    @Test
    public void testFindPathBeam() {
        SearchAlgorithm alg = new Beam();

        testFindPath(alg);
    }

    @Test
    public void testBeam1() {
        Pathfinder beam = new Pathfinder(new Beam());
        Path path = null;
        try {
            path = beam.generatePath(n01, n17);
        } catch (PathfinderException e) {
            e.printStackTrace();
        }
        LinkedList<Edge> testPath2Segment = new LinkedList<>();
        testPath2Segment.add(map.getConnectingEdge(n01,n02));
        testPath2Segment.add(map.getConnectingEdge(n02,n08));
        testPath2Segment.add(map.getConnectingEdge(n08,n11));
        testPath2Segment.add(map.getConnectingEdge(n11,n12));
        testPath2Segment.add(map.getConnectingEdge(n12,n17));

        LinkedList<LinkedList<Edge>> testPath2Edges = new LinkedList<>();
        testPath2Edges.add(testPath2Segment);


        assertTrue(path.getEdges().equals(testPath2Edges));
        // System.out.println(path.getEdges().toString());
        // System.out.println(testPath2.getEdges().toString());
    }
}
