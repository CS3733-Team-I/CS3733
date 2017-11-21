package database;

import database.connection.Connector;
import database.objects.Edge;
import database.objects.Node;
import database.util.DBUtil;
import entity.InterpreterRequest;
import entity.Request;
import utility.NodeFloor;
import utility.NodeType;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;


//Node and Edge objects should only be made here
public class DatabaseController {

    private  Connection instanceConnection = null;

    protected DatabaseController() {
        try {
            instanceConnection = DBUtil.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected DatabaseController(boolean test) {
        try {
            if (test) {
                instanceConnection = DBUtil.getTestConnection();
            } else {
                instanceConnection = DBUtil.getConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //returns null if node does not exist
    public  Node getNode(String id) {
        try {
            return Connector.selectNode(instanceConnection, id);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }

        return null;
    }

    public  ArrayList<Node> getAllNodes() {
        try{
            return Connector.selectAllNodes(instanceConnection);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return null;
    }

    public  int getNodeTypeCount(NodeType nodeType, NodeFloor floor, String teamAssigned){
        int result = 0;
        try{
            return Connector.selectCountNodeType(instanceConnection,nodeType, floor, teamAssigned);
        } catch (SQLException e){
            e.printStackTrace();
        }
        return result;
    }

    public  boolean removeNode(Node node) {
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

    public  int addNode(Node node) {
        try {
            return Connector.insertNode(instanceConnection, node);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public  int updateNode(Node node) {
        try {
            return Connector.updateNode(instanceConnection, node);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public  Edge getEdge(String edgeID) {
        try {
            return Connector.selectEdge(instanceConnection, edgeID);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return null;
    }

    public  boolean removeEdge(Edge edge) {
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

    public  int addEdge(Edge edge) {
        try {
            return Connector.insertEdge(instanceConnection, edge);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public  int updateEdge(Edge edge) {
        try{
            return Connector.updateEdge(instanceConnection, edge);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }

        return 0;
    }

    public  ArrayList<Edge> getAllEdges() {
        try{
            return Connector.selectAllEdges(instanceConnection);
        } catch(SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static DatabaseController getInstance() {
        try {
            DBUtil.createTables(DBUtil.getConnection());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return SingletonHelper.instance;
    }

    public static DatabaseController getTestInstance() {
        Connection conn;
            try {
                DBUtil.createTables(DBUtil.getTestConnection());

            } catch (SQLException e) {
                if(e.getSQLState() != "23505") {
                    e.printStackTrace();
                }
            }
            return SingletonHelper.testInstance;
    }
  
    public  Request addRequest(int requestID, String nodeID, String employee) {
        try {
            return Connector.insertRequest(instanceConnection, requestID, nodeID, employee);
        } catch(SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return null;
    }

    public  int updateRequest(int requestID, String nodeID, String employee) {
        try {
            return Connector.updateRequest(instanceConnection, requestID, nodeID, employee);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public  Request getRequest(int requestID) {
        try {
            return Connector.selectRequest(instanceConnection, requestID);
        } catch(SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return null;
    }

    public  boolean deleteRequest(int requestID) {
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

    public  ArrayList<Request> getAllRequests() {
        try {
            return Connector.selectAllRequests(instanceConnection);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return new ArrayList<Request>();
    }

    public  InterpreterRequest addIntepreterRequest(String language, int interpreterID, int requestID) {
        try {
            return Connector.insetInterpreter(instanceConnection, interpreterID, language, requestID);
        } catch(SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return null;
    }

    public  int updateInterpreterRequest(String language, int interpreterID, int requestID) {
        try {
            return Connector.updateIntepreter(instanceConnection, interpreterID, language, requestID);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public  InterpreterRequest getInterpreterRequest(int interpreterID) {
        try {
            return Connector.selectInterpreter(instanceConnection, interpreterID);
        } catch(SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return null;
    }

    public  boolean deleteInterpreterRequest(int interpreterID) {
        try {
            Connector.deleteInterpreter(instanceConnection, interpreterID);
            return true;
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return false;
    }

    public  ArrayList<InterpreterRequest> getAllInterpreterRequests() {
        try {
            return Connector.selectAllInterpeters(instanceConnection);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return new ArrayList<InterpreterRequest>();
    }

    public  void deleteTestTables() {
        DBUtil.dropAllTables(instanceConnection);
    }

    private static class SingletonHelper {
        private static final DatabaseController instance = new DatabaseController();
        private static final DatabaseController testInstance = new DatabaseController(true);
    }
}
