package pathfinder;
import database.DatabaseController;
import database.connection.NotFoundException;
import database.objects.Edge;
import database.objects.Node;
import database.utility.DatabaseException;
import entity.MapEntity;
import entity.Path;
import entity.SystemSettings;
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
    private static MapEntity map;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////             Map              /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
                        NodeFloor.THIRD
          X   10    20    30    40    50    60    70    80              n01 & n16 are elevators
       Y                                                                n07 & n17 are stairs
       10     n01 - n02 - n03 - n04 - - - - n05 - n06 - n07             n09 is a bathroom (not handicapped)
                     |           |
       20           n08 - n09   n10 - - - - n11
                     |           |        /
       30           n12 - n13 - n14 - n15 - n16
                           |
       40                 n17

       50                 n18   n19

       60                 n20 - n21
                           |     |
       70                 n22 - n23


                        NodeFloor.SECOND
          X   10    20    30    40    50    60    70    80              n24 & n39 are elevators
       Y                                                                n30 & n40 are stairs
       10     n24 - n25 - n26 - n27 - - - - n28 - n29 - n30             n32 is a bathroom (handicapped)
                     |           |
       20           n31 - n32   n33 - - - - n34
                     |           |        /
       30           n35 - n36 - n37 - n38 - n39
                           |
       40                 n40


                        NodeFloor.FIRST
          X   10    20    30    40    50    60    70    80              n47 & n62 are elevators
       Y                                                                n53 & n63 are stairs
       10     n47 - n48 - n49 - n50 - - - - n51 - n52 - n53             n55 is a bathroom (not handicapped)
                     |           |
       20           n54 - n55   n56 - - - - n57
                     |           |        /
       30           n58 - n59 - n60 - n61 - n62
                           |
       40                 n63

    */

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////     BEFORE/AFTER METHODS     /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Before //Build map for testing all algorithms
    public void setup() throws DatabaseException {
        DatabaseController.getInstance();
        map = MapEntity.getInstance();
        this.removeAllFromDB();

        //Nodes for F3
        map.addNode(new Node("NODE01",10,10, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.ELEV, "Elevator A","Elev A","I"));
        map.addNode(new Node("NODE02",20,10, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE02_LN","NODE02_SN","I"));
        map.addNode(new Node("NODE03",30,10, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE03_LN","NODE03_SN","I"));
        map.addNode(new Node("NODE04",40,10, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE04_LN","NODE04_SN","I"));
        map.addNode(new Node("NODE05",60,10, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE05_LN","NODE05_SN","I"));
        map.addNode(new Node("NODE06",70,10, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE06_LN","NODE06_SN","I"));
        map.addNode(new Node("NODE07",80,10, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.STAI, "Stair A","St A","I"));
        map.addNode(new Node("NODE08",20,20, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE08_LN","NODE08_SN","I"));
        map.addNode(new Node("NODE09",30,20, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.REST, "Restroom - Non Handicapped","RR - nHC","I"));
        map.addNode(new Node("NODE10",40,20, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE10_LN","NODE10_SN","I"));
        map.addNode(new Node("NODE11",60,20, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE11_LN","NODE11_SN","I"));
        map.addNode(new Node("NODE12",20,30, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE12_LN","NODE12_SN","I"));
        map.addNode(new Node("NODE13",30,30, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE13_LN","NODE13_SN","I"));
        map.addNode(new Node("NODE14",40,30, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE14_LN","NODE14_SN","I"));
        map.addNode(new Node("NODE15",50,30, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE15_LN","NODE15_SN","I"));
        map.addNode(new Node("NODE16",60,30, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.ELEV, "Elevator B","Elev B","I"));
        map.addNode(new Node("NODE17",30,40, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.STAI, "Stair B","St B","I"));
        map.addNode(new Node("NODE18",30,50, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE18_LN","NODE18_SN","I"));
        map.addNode(new Node("NODE19",40,50, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE19_LN","NODE19_SN","I"));
        map.addNode(new Node("NODE20",30,60, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE20_LN","NODE20_SN","I"));
        map.addNode(new Node("NODE21",40,60, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE21_LN","NODE21_SN","I"));
        map.addNode(new Node("NODE22",30,70, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE22_LN","NODE22_SN","I"));
        map.addNode(new Node("NODE23",40,70, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE23_LN","NODE23_SN","I"));
        //Nodes for F2
        map.addNode(new Node("NODE24",10,10, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.ELEV, "Elevator A","Elev A","I"));
        map.addNode(new Node("NODE25",20,10, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE25_LN","NODE25_SN","I"));
        map.addNode(new Node("NODE26",30,10, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE26_LN","NODE26_SN","I"));
        map.addNode(new Node("NODE27",40,10, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE27_LN","NODE27_SN","I"));
        map.addNode(new Node("NODE28",60,10, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE28_LN","NODE28_SN","I"));
        map.addNode(new Node("NODE29",70,10, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE29_LN","NODE29_SN","I"));
        map.addNode(new Node("NODE30",80,10, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.STAI, "Stair A","St A","I"));
        map.addNode(new Node("NODE31",20,20, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE31_LN","NODE31_SN","I"));
        map.addNode(new Node("NODE32",30,20, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.REST, "Restroom - Handicapped","RR - HC","I"));
        map.addNode(new Node("NODE33",40,20, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE33_LN","NODE33_SN","I"));
        map.addNode(new Node("NODE34",60,20, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE34_LN","NODE34_SN","I"));
        map.addNode(new Node("NODE35",20,30, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE35_LN","NODE35_SN","I"));
        map.addNode(new Node("NODE36",30,30, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE36_LN","NODE36_SN","I"));
        map.addNode(new Node("NODE37",40,30, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE37_LN","NODE37_SN","I"));
        map.addNode(new Node("NODE38",50,30, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE38_LN","NODE38_SN","I"));
        map.addNode(new Node("NODE39",60,30, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.ELEV, "Elevator B","Elev B","I"));
        map.addNode(new Node("NODE40",30,40, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.STAI, "Stair B","St B","I"));
        //Nodes for F1
        map.addNode(new Node("NODE47",10,10, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.ELEV, "Elevator A","Elev A","I"));
        map.addNode(new Node("NODE48",20,10, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE48_LN","NODE48_SN","I"));
        map.addNode(new Node("NODE49",30,10, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE49_LN","NODE49_SN","I"));
        map.addNode(new Node("NODE50",40,10, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE50_LN","NODE50_SN","I"));
        map.addNode(new Node("NODE51",60,10, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE51_LN","NODE51_SN","I"));
        map.addNode(new Node("NODE52",70,10, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE52_LN","NODE52_SN","I"));
        map.addNode(new Node("NODE53",80,10, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.STAI, "Stair A","St A","I"));
        map.addNode(new Node("NODE54",20,20, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE54_LN","NODE54_SN","I"));
        map.addNode(new Node("NODE55",30,20, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.REST, "Restroom - Non Handicapped","RR - nHC","I"));
        map.addNode(new Node("NODE56",40,20, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE56_LN","NODE56_SN","I"));
        map.addNode(new Node("NODE57",60,20, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE57_LN","NODE57_SN","I"));
        map.addNode(new Node("NODE58",20,30, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE58_LN","NODE58_SN","I"));
        map.addNode(new Node("NODE59",30,30, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE59_LN","NODE59_SN","I"));
        map.addNode(new Node("NODE60",40,30, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE60_LN","NODE60_SN","I"));
        map.addNode(new Node("NODE61",50,30, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE61_LN","NODE61_SN","I"));
        map.addNode(new Node("NODE62",60,30, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.ELEV, "Elevator B","Elev B","I"));
        map.addNode(new Node("NODE63",30,40, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.STAI, "Stair B","St B","I"));

        //Edges for F3
        map.addEdge(new Edge("EDGE01", "NODE01", "NODE02"));
        map.addEdge(new Edge("EDGE02", "NODE02", "NODE03"));
        map.addEdge(new Edge("EDGE03", "NODE03", "NODE04"));
        map.addEdge(new Edge("EDGE04", "NODE04", "NODE05"));
        map.addEdge(new Edge("EDGE05", "NODE05", "NODE06"));
        map.addEdge(new Edge("EDGE06", "NODE06", "NODE07"));
        map.addEdge(new Edge("EDGE07", "NODE02", "NODE08"));
        map.addEdge(new Edge("EDGE08", "NODE04", "NODE10"));
        map.addEdge(new Edge("EDGE09", "NODE08", "NODE09"));
        map.addEdge(new Edge("EDGE10", "NODE10", "NODE11"));
        map.addEdge(new Edge("EDGE11", "NODE08", "NODE12"));
        map.addEdge(new Edge("EDGE12", "NODE10", "NODE14"));
        map.addEdge(new Edge("EDGE13", "NODE11", "NODE15"));
        map.addEdge(new Edge("EDGE14", "NODE12", "NODE13"));
        map.addEdge(new Edge("EDGE15", "NODE13", "NODE14"));
        map.addEdge(new Edge("EDGE16", "NODE14", "NODE15"));
        map.addEdge(new Edge("EDGE17", "NODE15", "NODE16"));
        map.addEdge(new Edge("EDGE18", "NODE13", "NODE17"));
        map.addEdge(new Edge("EDGE19", "NODE20", "NODE21"));
        map.addEdge(new Edge("EDGE20", "NODE20", "NODE22"));
        map.addEdge(new Edge("EDGE21", "NODE21", "NODE23"));
        map.addEdge(new Edge("EDGE22", "NODE22", "NODE23"));
        //Edges for F2
        map.addEdge(new Edge("EDGE23", "NODE24", "NODE25"));
        map.addEdge(new Edge("EDGE24", "NODE25", "NODE26"));
        map.addEdge(new Edge("EDGE25", "NODE26", "NODE27"));
        map.addEdge(new Edge("EDGE26", "NODE27", "NODE28"));
        map.addEdge(new Edge("EDGE27", "NODE28", "NODE29"));
        map.addEdge(new Edge("EDGE28", "NODE29", "NODE30"));
        map.addEdge(new Edge("EDGE29", "NODE25", "NODE31"));
        map.addEdge(new Edge("EDGE30", "NODE27", "NODE33"));
        map.addEdge(new Edge("EDGE31", "NODE31", "NODE32"));
        map.addEdge(new Edge("EDGE32", "NODE33", "NODE34"));
        map.addEdge(new Edge("EDGE33", "NODE31", "NODE35"));
        map.addEdge(new Edge("EDGE34", "NODE33", "NODE37"));
        map.addEdge(new Edge("EDGE35", "NODE34", "NODE38"));
        map.addEdge(new Edge("EDGE36", "NODE35", "NODE36"));
        map.addEdge(new Edge("EDGE37", "NODE36", "NODE37"));
        map.addEdge(new Edge("EDGE38", "NODE37", "NODE38"));
        map.addEdge(new Edge("EDGE39", "NODE38", "NODE39"));
        map.addEdge(new Edge("EDGE40", "NODE36", "NODE40"));
        //Edges for F1
        map.addEdge(new Edge("EDGE45", "NODE47", "NODE48"));
        map.addEdge(new Edge("EDGE46", "NODE48", "NODE49"));
        map.addEdge(new Edge("EDGE47", "NODE49", "NODE50"));
        map.addEdge(new Edge("EDGE48", "NODE50", "NODE51"));
        map.addEdge(new Edge("EDGE49", "NODE51", "NODE52"));
        map.addEdge(new Edge("EDGE50", "NODE52", "NODE53"));
        map.addEdge(new Edge("EDGE51", "NODE48", "NODE54"));
        map.addEdge(new Edge("EDGE52", "NODE50", "NODE56"));
        map.addEdge(new Edge("EDGE53", "NODE54", "NODE55"));
        map.addEdge(new Edge("EDGE54", "NODE56", "NODE57"));
        map.addEdge(new Edge("EDGE55", "NODE54", "NODE58"));
        map.addEdge(new Edge("EDGE56", "NODE56", "NODE60"));
        map.addEdge(new Edge("EDGE57", "NODE57", "NODE61"));
        map.addEdge(new Edge("EDGE58", "NODE58", "NODE59"));
        map.addEdge(new Edge("EDGE59", "NODE59", "NODE60"));
        map.addEdge(new Edge("EDGE60", "NODE60", "NODE61"));
        map.addEdge(new Edge("EDGE61", "NODE61", "NODE62"));
        map.addEdge(new Edge("EDGE62", "NODE59", "NODE63"));
        //Edges for elevators
        map.addEdge(new Edge("EDGE67", "NODE01", "NODE24"));
        map.addEdge(new Edge("EDGE68", "NODE01", "NODE47"));
        map.addEdge(new Edge("EDGE69", "NODE24", "NODE47"));
        map.addEdge(new Edge("EDGE70", "NODE16", "NODE39"));
        map.addEdge(new Edge("EDGE71", "NODE16", "NODE62"));
        map.addEdge(new Edge("EDGE72", "NODE39", "NODE62"));
        //Edges for stairs
        map.addEdge(new Edge("EDGE73", "NODE07", "NODE30"));
        map.addEdge(new Edge("EDGE74", "NODE30", "NODE53"));
        map.addEdge(new Edge("EDGE75", "NODE17", "NODE40"));
        map.addEdge(new Edge("EDGE76", "NODE40", "NODE63"));
    }

    @After //remove things from database
    public void removeAllFromDB() throws DatabaseException {
        MapEntity.getInstance().removeAll();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////         A_star Tests         /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test //Test the FindPath method by testing all possible paths
    public void testFindPathAstar(){

        SearchAlgorithm alg = new A_star();

        testFindPath(alg,false);
    }

    @Test //Test the FindPath method by testing all possible paths
    public void testFindPathAstarWC(){

        SearchAlgorithm alg = new A_star();

        testFindPath(alg,true);
    }

    @Test(expected = PathfinderException.class) //test that the exception is thrown when there is no path or connection
    public void testPathfinderException1Astar() throws PathfinderException,NotFoundException{
        SearchAlgorithm alg = new A_star();
        alg.findPath(map.getNode("NODE18"), map.getNode("NODE19"), false);
    }

    @Test(expected = PathfinderException.class) //test that the exception is thrown when there is a path but no connection
    public void testPathfinderException2Astar() throws PathfinderException,NotFoundException{
        SearchAlgorithm alg = new A_star();
        alg.findPath(map.getNode("NODE01"), map.getNode("NODE19"), false);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////       DepthFirst Tests       /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test //Test the FindPath method by testing all possible paths
    public void testFindPathDF() throws PathfinderException{

        SearchAlgorithm alg = new DepthFirst();

        testFindPath(alg,false);
    }

    @Test //Test the FindPath method by testing all possible paths
    public void testFindPathDFWC() throws PathfinderException{

        SearchAlgorithm alg = new DepthFirst();

        testFindPath(alg,true);
    }

    @Test(expected = PathfinderException.class) //test that the exception is thrown when there is no path or connection
    public void testPathfinderException1DF() throws PathfinderException,NotFoundException{
        SearchAlgorithm alg = new DepthFirst();
        alg.findPath(map.getNode("NODE18"),map.getNode("NODE19"), false);
    }

    @Test(expected = PathfinderException.class) //test that the exception is thrown when there is a path but no connection
    public void testPathfinderException2DF() throws PathfinderException,NotFoundException{
        SearchAlgorithm alg = new DepthFirst();
        alg.findPath(map.getNode("NODE01"),map.getNode("NODE19"), false);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////      BreadthFirst Tests      /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test //Test the FindPath method by testing all possible paths
    public void testFindPathBF() {
        SearchAlgorithm alg = new BreadthFirst();

        testFindPath(alg,false);
    }

    @Test //Test the FindPath method by testing all possible paths
    public void testFindPathBFWC() {
        SearchAlgorithm alg = new BreadthFirst();

        testFindPath(alg,true);
    }

    @Test(expected = PathfinderException.class) //test that the exception is thrown when there is no path or connection
    public void testPathfinderException1BF() throws PathfinderException,NotFoundException{
        SearchAlgorithm alg = new BreadthFirst();
        alg.findPath(map.getNode("NODE18"),map.getNode("NODE19"),false);
    }

    @Test(expected = PathfinderException.class) //test that the exception is thrown when there is a path but no connection
    public void testPathfinderException2BF() throws PathfinderException,NotFoundException{
        SearchAlgorithm alg = new BreadthFirst();
        alg.findPath(map.getNode("NODE01"),map.getNode("NODE19"),false);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////        Dijkstra Tests        /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test //Test the FindPath method by testing all possible paths
    public void testFindPathD() {
        SearchAlgorithm alg = new Dijkstra();

        testFindPath(alg, false);
    }

    @Test
    public void testFindPathDWC() {
        SearchAlgorithm alg = new Dijkstra();

        testFindPath(alg, true);
    }

    @Test(expected = PathfinderException.class) //test that the exception is thrown when there is no path or connection
    public void testPathfinderException1D() throws PathfinderException,NotFoundException{
        SearchAlgorithm alg = new Dijkstra();
        alg.findPath(map.getNode("NODE18"),map.getNode("NODE19"),false);
    }

    @Test(expected = PathfinderException.class) //test that the exception is thrown when there is a path but no connection
    public void testPathfinderException2D() throws PathfinderException,NotFoundException{
        SearchAlgorithm alg = new Dijkstra();
        alg.findPath(map.getNode("NODE01"),map.getNode("NODE19"),false);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////          Beam Tests          /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testFindPathBeam() {
        SearchAlgorithm alg = new Beam();
        SystemSettings.getInstance().setBeamWidth("4");
        testFindPath(alg, false);
    }

    @Test
    public void testFindPathBeamWC() {
        SearchAlgorithm alg = new Beam();
        SystemSettings.getInstance().setBeamWidth("4");
        testFindPath(alg, true);
    }

    @Test(expected = PathfinderException.class) //test that the exception is thrown when there is no path or connection
    public void testPathfinderException1Beam() throws PathfinderException,NotFoundException{
        SearchAlgorithm alg = new Beam();
        SystemSettings.getInstance().setBeamWidth("4");
        alg.findPath(map.getNode("NODE18"),map.getNode("NODE19"),false);
    }

    @Test(expected = PathfinderException.class) //test that the exception is thrown when there is a path but no connection
    public void testPathfinderException2Beam() throws PathfinderException,NotFoundException{
        SearchAlgorithm alg = new Beam();
        SystemSettings.getInstance().setBeamWidth("4");
        alg.findPath(map.getNode("NODE01"),map.getNode("NODE19"),false);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////       Best First Tests       /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testFindPathBestFirst(){
        SearchAlgorithm alg = new BestFirst();
        testFindPath(alg,false);
    }

    @Test
    public void testFindPathBestFirstWC(){
        SearchAlgorithm alg = new BestFirst();
        testFindPath(alg,true);
    }

    @Test(expected = PathfinderException.class) //test that the exception is thrown when there is no path or connection
    public void testPathfinderException1BestFirst() throws PathfinderException,NotFoundException{
        SearchAlgorithm alg = new BestFirst();
        alg.findPath(map.getNode("NODE18"),map.getNode("NODE19"),false);
    }

    @Test(expected = PathfinderException.class) //test that the exception is thrown when there is a path but no connection
    public void testPathfinderException2BestFirst() throws PathfinderException,NotFoundException{
        SearchAlgorithm alg = new BestFirst();
        alg.findPath(map.getNode("NODE01"),map.getNode("NODE19"),false);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////     Text Direction Tests     /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testGenerateDirections() throws PathfinderException,NotFoundException{
        pathfinder = new Pathfinder();

        LinkedList<Node> nodes = new LinkedList<>();
        nodes.add(map.getNode("NODE01"));
        nodes.add(map.getNode("NODE17"));

        Path path = pathfinder.generatePath(nodes);

        System.out.println(path.getDirections());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////   Multiple Waypoint Tests    /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test //Tests a path with multiple waypoints
    public void testMultipleWaypoint() throws NotFoundException{

        pathfinder = new Pathfinder();

        LinkedList<Node> nodes = new LinkedList<>();
        nodes.add(map.getNode("NODE01"));
        nodes.add(map.getNode("NODE12"));
        nodes.add(map.getNode("NODE06"));

        Path multipath = null;
        try {
            multipath = pathfinder.generatePath(nodes);
        } catch (PathfinderException e) {
            e.printStackTrace();
        }

        //TODO use isValidPath() instead of building a valid path here

        LinkedList<Edge> testPathSegment1 = new LinkedList<>();
        testPathSegment1.add(map.getConnectingEdge(map.getNode("NODE01"),map.getNode("NODE02")));
        testPathSegment1.add(map.getConnectingEdge(map.getNode("NODE02"),map.getNode("NODE08")));
        testPathSegment1.add(map.getConnectingEdge(map.getNode("NODE08"),map.getNode("NODE12")));

        LinkedList<Edge> testPathSegment2 = new LinkedList<>();
        testPathSegment2.add(map.getConnectingEdge(map.getNode("NODE12"),map.getNode("NODE13")));
        testPathSegment2.add(map.getConnectingEdge(map.getNode("NODE13"),map.getNode("NODE14")));
        testPathSegment2.add(map.getConnectingEdge(map.getNode("NODE14"),map.getNode("NODE10")));
        testPathSegment2.add(map.getConnectingEdge(map.getNode("NODE10"),map.getNode("NODE04")));
        testPathSegment2.add(map.getConnectingEdge(map.getNode("NODE04"),map.getNode("NODE05")));
        testPathSegment2.add(map.getConnectingEdge(map.getNode("NODE05"),map.getNode("NODE06")));

        LinkedList<LinkedList<Edge>> testPathEdges = new LinkedList<>();
        testPathEdges.add(testPathSegment1);
        testPathEdges.add(testPathSegment2);

        Path testMultipath = new Path(nodes, testPathEdges);

        assertTrue(multipath.equals(testMultipath));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////         Other Tests          /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //Helper method to check if a path is valid for multi waypoint navigation
    private boolean isValidPath(LinkedList<Node> nodes, LinkedList<Edge> path) {
        //TODO implement this
        return false;
    }

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
    private void testFindPath(SearchAlgorithm alg, boolean wheelchairAccessible) {

        LinkedList<Edge> path;

        // Try to test every possible path
        for(Node n1 : map.getAllNodes()) {
            for(Node n2 : map.getAllNodes()) {
                try {
                    path = alg.findPath(n1,n2, wheelchairAccessible);

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
}
