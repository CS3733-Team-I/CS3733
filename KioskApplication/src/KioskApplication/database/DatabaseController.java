package KioskApplication.database;

import KioskApplication.database.connection.Connector;
import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;
import KioskApplication.database.util.DBUtil;
import KioskApplication.utility.NodeBuilding;
import KioskApplication.utility.NodeFloor;
import KioskApplication.utility.NodeType;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;


//Node and Edge objects should only be made here
public class DatabaseController {

    private static DatabaseController instance = null;
    private static Connection instanceConnection = null;

    protected DatabaseController() {

    }

    //returns null if node does not exist
    public static Node getNode(String id) {
        try {
            return Connector.selectNode(instanceConnection, id);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return null;
    }

    public static ArrayList<Node> getAllNodes() {
        try{
            return Connector.selectAllNodes(instanceConnection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean removeNode(String id) {
        try {
            Connector.deleteNode(instanceConnection, id);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    //will probably want to change this to have less parameter
    public static Node addNode(String nodeID, int xc, int yc, NodeFloor fl, NodeBuilding bu, NodeType nt,
                               String ln, String sn, String assigned) {
        try {
            return Connector.insertNode(instanceConnection, xc, yc, fl, bu, nt, ln, sn, assigned, nodeID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean updateNode(Node node) {
        try {
            Connector.updateNode(instanceConnection, node);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static Edge getEdge(String edgeID) {
        try {
            return Connector.selectEdge(instanceConnection, edgeID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean removeEdge(String edgeID) {
        try {
            Connector.deleteEdge(instanceConnection, edgeID);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static Edge addEdge(String edgeID, String node1, String node2) {
        try {
            return Connector.insertEdge(instanceConnection, node1, node2, edgeID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean updateEdge(Edge edge) {
        try{
            Connector.updateEdge(instanceConnection, edge);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static ArrayList<Edge> getAllEdges() {
        try{
            return Connector.selectAllEdges(instanceConnection);
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }




    public static void init() {
        if(instanceConnection == null) {
            try {
                instanceConnection = DBUtil.getCon();
                DBUtil.createTables(instanceConnection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
