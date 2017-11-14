package KioskApplication.database;

import KioskApplication.database.connection.Connector;
import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;
import KioskApplication.database.template.ConnectionDetails;
import KioskApplication.database.util.DBUtil;
import KioskApplication.entity.InterpreterRequest;
import KioskApplication.entity.Request;
import com.sun.org.apache.regexp.internal.RE;

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

    public static void initTests() {
        if(instanceConnection == null) {
            try {
                instanceConnection = DBUtil.getTestConnection();
                DBUtil.createTables(instanceConnection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    //Commented out generic request methods for now
    /*
    public static Request addRequest(int requestID, String nodeID, String employee) {
        try {
            return Connector.insertRequest(instanceConnection, requestID, nodeID, employee);
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int updateRequest(int requestID, String nodeID, String employee) {
        try {
            return Connector.updateRequest(instanceConnection, requestID, nodeID, employee);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static Request getRequest(int requestID) {
        try {
            return Connector.selectRequest(instanceConnection, requestID);
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean deleteRequest(int requestID) {
        try {
            Connector.deleteRequest(instanceConnection, requestID);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static ArrayList<Request> getAllRequests() {
        try {
            return Connector.selectAllRequests(instanceConnection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<Request>();
    }
    */

    //Changed this method to void, don't know if we need it to return an interpreterRequest
    public static void addInterpreterRequest(int interpreterID, String nodeID, String language, String employee) {
        try {
            Connector.insertInterpreter(instanceConnection, interpreterID, nodeID, language, employee);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    //Lazy commenting out: method caused errors
    /*
    public static int updateInterpreterRequest(String language, int interpreterID, int requestID) {
        try {
            return Connector.updateInterpreter(instanceConnection, interpreterID, language, requestID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    */

    public static InterpreterRequest getInterpreterRequest(int interpreterID) {
        try {
            return Connector.selectInterpreter(instanceConnection, interpreterID);
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean deleteInterpreterRequest(int interpreterID) {
        try {
            Connector.deleteInterpreter(instanceConnection, interpreterID);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static ArrayList<InterpreterRequest> getAllInterpreterRequests() {
        try {
            return Connector.selectAllInterpreters(instanceConnection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<InterpreterRequest>();
    }
}
