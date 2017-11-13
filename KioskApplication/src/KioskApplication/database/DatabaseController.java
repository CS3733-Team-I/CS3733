package KioskApplication.database;

import KioskApplication.database.connection.Connector;
import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;
import KioskApplication.database.util.DBUtil;

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

    public static boolean removeNode(Node node) {
        try {
            Connector.deleteNode(instanceConnection, node);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static int addNode(Node node) {
        try {
            return Connector.insertNode(instanceConnection, node);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int updateNode(Node node) {
        try {
            return Connector.updateNode(instanceConnection, node);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static Edge getEdge(String edgeID) {
        try {
            return Connector.selectEdge(instanceConnection, edgeID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean removeEdge(Edge edge) {
        try {
            Connector.deleteEdge(instanceConnection, edge);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static int addEdge(Edge edge) {
        try {
            return Connector.insertEdge(instanceConnection, edge);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int updateEdge(Edge edge) {
        try{
            return Connector.updateEdge(instanceConnection, edge);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
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
                instanceConnection = DBUtil.getConnection();
                DBUtil.createTables(instanceConnection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
