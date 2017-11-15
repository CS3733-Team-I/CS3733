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
    public Node getNode(String id) {
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

    public boolean removeNode(Node node) {
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

    public int addNode(Node node) {
        try {
            return Connector.insertNode(instanceConnection, node);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public int updateNode(Node node) {
        try {
            return Connector.updateNode(instanceConnection, node);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public Edge getEdge(String edgeID) {
        try {
            return Connector.selectEdge(instanceConnection, edgeID);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean removeEdge(Edge edge) {
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

    public int addEdge(Edge edge) {
        try {
            return Connector.insertEdge(instanceConnection, edge);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public int updateEdge(Edge edge) {
        try{
            return Connector.updateEdge(instanceConnection, edge);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }

        return 0;
    }

    public ArrayList<Edge> getAllEdges() {
        try{
            return Connector.selectAllEdges(instanceConnection);
        } catch(SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void getInstance() {
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

    public void initTests() {
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
  
    public Request addRequest(int requestID, String nodeID, String employee) {
        try {
            return Connector.insertRequest(instanceConnection, requestID, nodeID, employee);
        } catch(SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return null;
    }

    public int updateRequest(int requestID, String nodeID, String employee) {
        try {
            return Connector.updateRequest(instanceConnection, requestID, nodeID, employee);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public Request getRequest(int requestID) {
        try {
            return Connector.selectRequest(instanceConnection, requestID);
        } catch(SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean deleteRequest(int requestID) {
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

    public ArrayList<Request> getAllRequests() {
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
}
