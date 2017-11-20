package database;

import database.connection.Connector;
import database.objects.Edge;
import database.objects.Node;
import database.util.DBUtil;
import database.objects.InterpreterRequest;
import database.objects.Request;
import utility.NodeFloor;
import utility.NodeType;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;


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
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static ArrayList<Node> getAllNodes() {
        try{
            return Connector.selectAllNodes(instanceConnection);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static int getNodeTypeCount(NodeType nodeType, NodeFloor floor, String teamAssigned){
        int result = 0;
        try{
            return Connector.selectCountNodeType(instanceConnection,nodeType, floor, teamAssigned);
        } catch (SQLException e){
            e.printStackTrace();
        }
        return result;
    }

    public static boolean removeNode(Node node) {
        try {
            Connector.deleteNode(instanceConnection, node);

            return true;
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }

        return false;
    }

    public static int addNode(Node node) {
        try {
            return Connector.insertNode(instanceConnection, node);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public static int updateNode(Node node) {
        try {
            return Connector.updateNode(instanceConnection, node);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public static Edge getEdge(String edgeID) {
        try {
            return Connector.selectEdge(instanceConnection, edgeID);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean removeEdge(Edge edge) {
        try {
            Connector.deleteEdge(instanceConnection, edge);

            return true;
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }

        return false;
    }

    public static int addEdge(Edge edge) {
        try {
            return Connector.insertEdge(instanceConnection, edge);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public static int updateEdge(Edge edge) {
        try{
            return Connector.updateEdge(instanceConnection, edge);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }

        return 0;
    }

    public static ArrayList<Edge> getAllEdges() {
        try{
            return Connector.selectAllEdges(instanceConnection);
        } catch(SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void init() {
        if(instanceConnection == null) {
            try {
                instanceConnection = DBUtil.getConnection();
                DBUtil.createTables(instanceConnection);
            } catch (SQLException e) {
                if(e.getSQLState() != "23505") {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void initTests() {
        if(instanceConnection == null) {
            try {
                instanceConnection = DBUtil.getTestConnection();
                DBUtil.createTables(instanceConnection);
            } catch (SQLException e) {
                if(e.getSQLState() != "23505") {
                    e.printStackTrace();
                }
            }
        }
    }
  
    /*public static Request addRequest(int requestID, String nodeID, String employee) {
        try {
            return Connector.insertRequest(instanceConnection, requestID, nodeID, employee);
        } catch(SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static int updateRequest(int requestID, String nodeID, String employee) {
        try {
            return Connector.updateRequest(instanceConnection, requestID, nodeID, employee);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public static Request getRequest(int requestID) {
        try {
            return Connector.selectRequest(instanceConnection, requestID);
        } catch(SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean deleteRequest(int requestID) {
        try {
            Connector.deleteRequest(instanceConnection, requestID);
            return true;
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static ArrayList<Request> getAllRequests() {
        try {
            return Connector.selectAllRequests(instanceConnection);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return new ArrayList<Request>();
    }*/

    public static void addInterpreterRequest(InterpreterRequest iR) {
        try {
            Connector.insertInterpreter(instanceConnection, iR);
        } catch(SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
    }
    //TODO: Update this method
    public static int updateInterpreterRequest(InterpreterRequest iR) {
        try {
            return Connector.updateInterpreter(instanceConnection, iR);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public static InterpreterRequest getInterpreterRequest(String requestID) {
        try {
            return Connector.selectInterpreter(instanceConnection, requestID);
        } catch(SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean deleteInterpreterRequest(String requestID) {
        try {
            Connector.deleteInterpreter(instanceConnection, requestID);
            return true;
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static LinkedList<InterpreterRequest> getAllInterpreterRequests() {
        try {
            return Connector.selectAllInterpreters(instanceConnection);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return new LinkedList<InterpreterRequest>();
    }

    public static void deleteTestTables() {
        DBUtil.dropAllTables(instanceConnection);
    }
}
