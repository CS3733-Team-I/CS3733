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
                        n13, n14, n15, n16, n17, n18, n19, n20, n21, n22, n23,
                        n24, n25, n26, n27, n28, n29, n30, n31, n32, n33, n34, n35,
                        n36, n37, n38, n39, n40, n41, n42, n43, n44, n45, n46,
                        n47, n48, n49, n50, n51, n52, n53, n54, n55, n56, n57, n58,
                        n59, n60, n61, n62, n63, n64, n65, n66, n67, n68, n69;

    private static Edge e01, e02, e03, e04, e05, e06, e07, e08, e09, e10, e11,
                        e12, e13, e14, e15, e16, e17, e18, e19, e20, e21, e22,
                        e23, e24, e25, e26, e27, e28, e29, e30, e31, e32, e33,
                        e34, e35, e36, e37, e38, e39, e40, e41, e42, e43, e44,
                        e45, e46, e47, e48, e49, e50, e51, e52, e53, e54, e55,
                        e56, e57, e58, e59, e60, e61, e62, e63, e64, e65, e66,
                        e67, e68, e69, e70, e71, e72, e73, e74, e75, e76;
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

       50                 n41   n42

       60                 n43 - n44
                           |     |
       70                 n45 - n46


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

       50                 n64   n65

       60                 n66 - n67
                           |     |
       70                 n68 - n69
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
        n01 = new Node("NODE01",10,10, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.ELEV, "Elevator A","Elev A","I");
        n02 = new Node("NODE02",20,10, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE02_LN","NODE02_SN","I");
        n03 = new Node("NODE03",30,10, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE03_LN","NODE03_SN","I");
        n04 = new Node("NODE04",40,10, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE04_LN","NODE04_SN","I");
        n05 = new Node("NODE05",60,10, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE05_LN","NODE05_SN","I");
        n06 = new Node("NODE06",70,10, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE06_LN","NODE06_SN","I");
        n07 = new Node("NODE07",80,10, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.STAI, "Stair A","St A","I");
        n08 = new Node("NODE08",20,20, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE08_LN","NODE08_SN","I");
        n09 = new Node("NODE09",30,20, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.REST, "Restroom - Non Handicapped","RR - nHC","I");
        n10 = new Node("NODE10",40,20, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE10_LN","NODE10_SN","I");
        n11 = new Node("NODE11",60,20, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE11_LN","NODE11_SN","I");
        n12 = new Node("NODE12",20,30, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE12_LN","NODE12_SN","I");
        n13 = new Node("NODE13",30,30, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE13_LN","NODE13_SN","I");
        n14 = new Node("NODE14",40,30, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE14_LN","NODE14_SN","I");
        n15 = new Node("NODE15",50,30, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE15_LN","NODE15_SN","I");
        n16 = new Node("NODE16",60,30, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.ELEV, "Elevator B","Elev B","I");
        n17 = new Node("NODE17",30,40, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.STAI, "Stair B","St B","I");
        n18 = new Node("NODE18",30,50, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE18_LN","NODE18_SN","I");
        n19 = new Node("NODE19",40,50, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE19_LN","NODE19_SN","I");
        n20 = new Node("NODE20",30,60, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE20_LN","NODE20_SN","I");
        n21 = new Node("NODE21",40,60, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE21_LN","NODE21_SN","I");
        n22 = new Node("NODE22",30,70, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE22_LN","NODE22_SN","I");
        n23 = new Node("NODE23",40,70, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE23_LN","NODE23_SN","I");
        //Nodes for F2
        n24 = new Node("NODE24",10,10, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.ELEV, "Elevator A","Elev A","I");
        n25 = new Node("NODE25",20,10, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE25_LN","NODE25_SN","I");
        n26 = new Node("NODE26",30,10, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE26_LN","NODE26_SN","I");
        n27 = new Node("NODE27",40,10, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE27_LN","NODE27_SN","I");
        n28 = new Node("NODE28",60,10, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE28_LN","NODE28_SN","I");
        n29 = new Node("NODE29",70,10, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE29_LN","NODE29_SN","I");
        n30 = new Node("NODE30",80,10, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.STAI, "Stair A","St A","I");
        n31 = new Node("NODE31",20,20, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE31_LN","NODE31_SN","I");
        n32 = new Node("NODE32",30,20, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.REST, "Restroom - Handicapped","RR - HC","I");
        n33 = new Node("NODE33",40,20, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE33_LN","NODE33_SN","I");
        n34 = new Node("NODE34",60,20, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE34_LN","NODE34_SN","I");
        n35 = new Node("NODE35",20,30, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE35_LN","NODE35_SN","I");
        n36 = new Node("NODE36",30,30, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE36_LN","NODE36_SN","I");
        n37 = new Node("NODE37",40,30, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE37_LN","NODE37_SN","I");
        n38 = new Node("NODE38",50,30, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE38_LN","NODE38_SN","I");
        n39 = new Node("NODE39",60,30, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.ELEV, "Elevator B","Elev B","I");
        n40 = new Node("NODE40",30,40, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.STAI, "Stair B","St B","I");
        n41 = new Node("NODE41",30,50, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE41_LN","NODE41_SN","I");
        n42 = new Node("NODE42",40,50, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE42_LN","NODE42_SN","I");
        n43 = new Node("NODE43",30,60, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE43_LN","NODE43_SN","I");
        n44 = new Node("NODE44",40,60, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE44_LN","NODE44_SN","I");
        n45 = new Node("NODE45",30,70, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE45_LN","NODE45_SN","I");
        n46 = new Node("NODE46",40,70, NodeFloor.SECOND, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE46_LN","NODE46_SN","I");
        //Nodes for F1
        n47 = new Node("NODE47",10,10, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.ELEV, "Elevator A","Elev A","I");
        n48 = new Node("NODE48",20,10, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE48_LN","NODE48_SN","I");
        n49 = new Node("NODE49",30,10, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE49_LN","NODE49_SN","I");
        n50 = new Node("NODE50",40,10, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE50_LN","NODE50_SN","I");
        n51 = new Node("NODE51",60,10, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE51_LN","NODE51_SN","I");
        n52 = new Node("NODE52",70,10, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE52_LN","NODE52_SN","I");
        n53 = new Node("NODE53",80,10, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.STAI, "Stair A","St A","I");
        n54 = new Node("NODE54",20,20, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE54_LN","NODE54_SN","I");
        n55 = new Node("NODE55",30,20, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.REST, "Restroom - Non Handicapped","RR - nHC","I");
        n56 = new Node("NODE56",40,20, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE56_LN","NODE56_SN","I");
        n57 = new Node("NODE57",60,20, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE57_LN","NODE57_SN","I");
        n58 = new Node("NODE58",20,30, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE58_LN","NODE58_SN","I");
        n59 = new Node("NODE59",30,30, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE59_LN","NODE59_SN","I");
        n60 = new Node("NODE60",40,30, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE60_LN","NODE60_SN","I");
        n61 = new Node("NODE61",50,30, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE61_LN","NODE61_SN","I");
        n62 = new Node("NODE62",60,30, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.ELEV, "Elevator B","Elev B","I");
        n63 = new Node("NODE63",30,40, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.STAI, "Stair B","St B","I");
        n64 = new Node("NODE64",30,50, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE64_LN","NODE64_SN","I");
        n65 = new Node("NODE65",40,50, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE65_LN","NODE65_SN","I");
        n66 = new Node("NODE66",30,60, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE66_LN","NODE66_SN","I");
        n67 = new Node("NODE67",40,60, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE67_LN","NODE67_SN","I");
        n68 = new Node("NODE68",30,70, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE68_LN","NODE68_SN","I");
        n69 = new Node("NODE69",40,70, NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.HALL, "NODE69_LN","NODE69_SN","I");

        //Add all nodes to the map
        map.addNode(n01); map.addNode(n02); map.addNode(n03); map.addNode(n04); map.addNode(n05);
        map.addNode(n06); map.addNode(n07); map.addNode(n08); map.addNode(n09); map.addNode(n10);
        map.addNode(n11); map.addNode(n12); map.addNode(n13); map.addNode(n14); map.addNode(n15);
        map.addNode(n16); map.addNode(n17); map.addNode(n18); map.addNode(n19); map.addNode(n20);
        map.addNode(n21); map.addNode(n22); map.addNode(n23);
        map.addNode(n24); map.addNode(n25); map.addNode(n26); map.addNode(n27); map.addNode(n28);
        map.addNode(n29); map.addNode(n30); map.addNode(n31); map.addNode(n32); map.addNode(n33);
        map.addNode(n34); map.addNode(n35); map.addNode(n36); map.addNode(n37); map.addNode(n38);
        map.addNode(n39); map.addNode(n40); map.addNode(n41); map.addNode(n42); map.addNode(n43);
        map.addNode(n44); map.addNode(n45); map.addNode(n46);
        map.addNode(n47); map.addNode(n48); map.addNode(n49); map.addNode(n50); map.addNode(n51);
        map.addNode(n52); map.addNode(n53); map.addNode(n54); map.addNode(n55); map.addNode(n56);
        map.addNode(n57); map.addNode(n58); map.addNode(n59); map.addNode(n60); map.addNode(n61);
        map.addNode(n62); map.addNode(n63); map.addNode(n64); map.addNode(n65); map.addNode(n66);
        map.addNode(n67); map.addNode(n68); map.addNode(n69);

        //Edges for F3
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
        //Edges for F2
        e23 = new Edge("EDGE23", n01.getNodeID(), n02.getNodeID());
        e24 = new Edge("EDGE24", n02.getNodeID(), n03.getNodeID());
        e25 = new Edge("EDGE25", n03.getNodeID(), n04.getNodeID());
        e26 = new Edge("EDGE26", n04.getNodeID(), n05.getNodeID());
        e27 = new Edge("EDGE27", n05.getNodeID(), n06.getNodeID());
        e28 = new Edge("EDGE28", n06.getNodeID(), n07.getNodeID());
        e29 = new Edge("EDGE29", n02.getNodeID(), n08.getNodeID());
        e30 = new Edge("EDGE30", n04.getNodeID(), n10.getNodeID());
        e31 = new Edge("EDGE31", n08.getNodeID(), n09.getNodeID());
        e32 = new Edge("EDGE32", n10.getNodeID(), n15.getNodeID());
        e33 = new Edge("EDGE33", n08.getNodeID(), n11.getNodeID());
        e34 = new Edge("EDGE34", n10.getNodeID(), n13.getNodeID());
        e35 = new Edge("EDGE35", n15.getNodeID(), n14.getNodeID());
        e36 = new Edge("EDGE36", n11.getNodeID(), n12.getNodeID());
        e37 = new Edge("EDGE37", n12.getNodeID(), n13.getNodeID());
        e38 = new Edge("EDGE38", n13.getNodeID(), n14.getNodeID());
        e39 = new Edge("EDGE39", n14.getNodeID(), n16.getNodeID());
        e40 = new Edge("EDGE40", n12.getNodeID(), n17.getNodeID());
        e41 = new Edge("EDGE41", n20.getNodeID(), n21.getNodeID());
        e42 = new Edge("EDGE42", n20.getNodeID(), n22.getNodeID());
        e43 = new Edge("EDGE43", n21.getNodeID(), n23.getNodeID());
        e44 = new Edge("EDGE44", n22.getNodeID(), n23.getNodeID());
        //Edges for F1
        e45 = new Edge("EDGE45", n01.getNodeID(), n02.getNodeID());
        e46 = new Edge("EDGE46", n02.getNodeID(), n03.getNodeID());
        e47 = new Edge("EDGE47", n03.getNodeID(), n04.getNodeID());
        e48 = new Edge("EDGE48", n04.getNodeID(), n05.getNodeID());
        e49 = new Edge("EDGE49", n05.getNodeID(), n06.getNodeID());
        e50 = new Edge("EDGE50", n06.getNodeID(), n07.getNodeID());
        e51 = new Edge("EDGE51", n02.getNodeID(), n08.getNodeID());
        e52 = new Edge("EDGE52", n04.getNodeID(), n10.getNodeID());
        e53 = new Edge("EDGE53", n08.getNodeID(), n09.getNodeID());
        e54 = new Edge("EDGE54", n10.getNodeID(), n15.getNodeID());
        e55 = new Edge("EDGE55", n08.getNodeID(), n11.getNodeID());
        e56 = new Edge("EDGE56", n10.getNodeID(), n13.getNodeID());
        e57 = new Edge("EDGE57", n15.getNodeID(), n14.getNodeID());
        e58 = new Edge("EDGE58", n11.getNodeID(), n12.getNodeID());
        e59 = new Edge("EDGE59", n12.getNodeID(), n13.getNodeID());
        e60 = new Edge("EDGE60", n13.getNodeID(), n14.getNodeID());
        e61 = new Edge("EDGE61", n14.getNodeID(), n16.getNodeID());
        e62 = new Edge("EDGE62", n12.getNodeID(), n17.getNodeID());
        e63 = new Edge("EDGE63", n20.getNodeID(), n21.getNodeID());
        e64 = new Edge("EDGE64", n20.getNodeID(), n22.getNodeID());
        e65 = new Edge("EDGE65", n21.getNodeID(), n23.getNodeID());
        e66 = new Edge("EDGE66", n22.getNodeID(), n23.getNodeID());
        //Edges for elevators
        e67 = new Edge("EDGE67", n01.getNodeID(), n24.getNodeID());
        e68 = new Edge("EDGE68", n01.getNodeID(), n47.getNodeID());
        e69 = new Edge("EDGE69", n24.getNodeID(), n47.getNodeID());
        e70 = new Edge("EDGE70", n16.getNodeID(), n39.getNodeID());
        e71 = new Edge("EDGE71", n16.getNodeID(), n62.getNodeID());
        e72 = new Edge("EDGE72", n39.getNodeID(), n62.getNodeID());
        //Edges for stairs
        e73 = new Edge("EDGE73", n07.getNodeID(), n30.getNodeID());
        e74 = new Edge("EDGE74", n30.getNodeID(), n53.getNodeID());
        e75 = new Edge("EDGE75", n17.getNodeID(), n40.getNodeID());
        e76 = new Edge("EDGE76", n40.getNodeID(), n63.getNodeID());

        //Add all edges to the map
        map.addEdge(e01); map.addEdge(e02); map.addEdge(e03); map.addEdge(e04); map.addEdge(e05);
        map.addEdge(e06); map.addEdge(e07); map.addEdge(e08); map.addEdge(e09); map.addEdge(e10);
        map.addEdge(e11); map.addEdge(e12); map.addEdge(e13); map.addEdge(e14); map.addEdge(e15);
        map.addEdge(e16); map.addEdge(e17); map.addEdge(e18); map.addEdge(e19); map.addEdge(e20);
        map.addEdge(e21); map.addEdge(e22);
        map.addEdge(e23); map.addEdge(e24); map.addEdge(e25); map.addEdge(e26); map.addEdge(e27);
        map.addEdge(e28); map.addEdge(e29); map.addEdge(e30); map.addEdge(e31); map.addEdge(e32);
        map.addEdge(e33); map.addEdge(e34); map.addEdge(e35); map.addEdge(e36); map.addEdge(e37);
        map.addEdge(e38); map.addEdge(e39); map.addEdge(e40); map.addEdge(e41); map.addEdge(e42);
        map.addEdge(e43); map.addEdge(e44);
        map.addEdge(e45); map.addEdge(e46); map.addEdge(e47); map.addEdge(e48); map.addEdge(e49);
        map.addEdge(e50); map.addEdge(e51); map.addEdge(e52); map.addEdge(e53); map.addEdge(e54);
        map.addEdge(e55); map.addEdge(e56); map.addEdge(e57); map.addEdge(e58); map.addEdge(e59);
        map.addEdge(e60); map.addEdge(e61); map.addEdge(e62); map.addEdge(e63); map.addEdge(e64);
        map.addEdge(e65); map.addEdge(e66);
        map.addEdge(e67); map.addEdge(e68); map.addEdge(e69); map.addEdge(e70); map.addEdge(e71);
        map.addEdge(e72); map.addEdge(e73); map.addEdge(e74); map.addEdge(e75); map.addEdge(e76);
    }

    @After //remove things from database
    public void removeAllFromDB() throws DatabaseException {
        MapEntity.getInstance().removeAll();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////         A_star Tests         /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

    @Test //Test the FindPath method by testing all possible paths
    public void testFindPathAstar(){

        SearchAlgorithm alg = new A_star();

        testFindPath(alg);
    }

    @Test(expected = PathfinderException.class) //test that the exception is thrown when there is no path or connection
    public void testDeadEndException1Astar() throws PathfinderException{
        SearchAlgorithm alg = new A_star();
        alg.findPath(n18,n19);
    }

    @Test(expected = PathfinderException.class) //test that the exception is thrown when there is a path but no connection
    public void testDeadEndException2Astar() throws PathfinderException{
        SearchAlgorithm alg = new A_star();
        alg.findPath(n01,n19);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////       DepthFirst Tests       /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //TODO //Tests a path with multiple waypoints

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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////      BreadthFirst Tests      /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testFindPathBF() {
        SearchAlgorithm alg = new BreadthFirst();

        testFindPath(alg);
    }

    @Test
    public void testBreadthFirstSearch() {
        Pathfinder breadth = new Pathfinder(new BreadthFirst());
        Path path = null;

        LinkedList<Node> nodes = new LinkedList<>();
        nodes.add(n01);
        nodes.add(n02);

        try {
            path = breadth.generatePath(nodes);
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

        LinkedList<Node> nodes = new LinkedList<>();
        nodes.add(n01);
        nodes.add(n17);

        try {
            path = breadth.generatePath(nodes);
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

        LinkedList<Node> nodes = new LinkedList<>();
        nodes.add(n02);
        nodes.add(n05);

        try {
            path = breadth.generatePath(nodes);
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////        Dijkstra Tests        /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testFindPathD() {
        SearchAlgorithm alg = new Dijkstra();

        testFindPath(alg);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////          Beam Tests          /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //TODO

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////        Exception Tests       /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test (expected = PathfinderException.class)
    public void testPathfinderException() throws PathfinderException{
        //test that the exception is thrown when there is a path but no connection
             SearchAlgorithm alg = new BreadthFirst();
             alg.findPath(n02,n18);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////     Text Direction Tests     /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testGenerateDirections() throws PathfinderException{
        pathfinder = new Pathfinder();

        LinkedList<Node> nodes = new LinkedList<>();
        nodes.add(n01);
        nodes.add(n17);

        Path path = pathfinder.generatePath(nodes);

        System.out.println(path.getDirections());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////         Other Tests          /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
}
