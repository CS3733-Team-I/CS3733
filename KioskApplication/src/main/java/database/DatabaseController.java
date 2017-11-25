package database;

import database.connection.Connector;
import database.objects.Edge;
import database.objects.Node;
import database.util.DBUtil;
import database.objects.InterpreterRequest;
import utility.node.NodeFloor;
import utility.node.NodeType;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;


//node and Edge objects should only be made here
public class DatabaseController {

    private  Connection instanceConnection = null;

    protected DatabaseController() {
        try {
            instanceConnection = DBUtil.getConnection();

            DBUtil.createTables(instanceConnection);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    protected DatabaseController(boolean test) {
        try {
            if(test) {
                instanceConnection = DBUtil.getTestConnection();

            } else {
                instanceConnection = DBUtil.getConnection();
            }

            DBUtil.createTables(instanceConnection);
        } catch(SQLException e) {
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

    public int addEdge(Edge edge) {
        try {
            return Connector.insertEdge(instanceConnection, edge);
        } catch (SQLException e) {
            if (e.getSQLState() == "23503") {
                System.err.println(String.format("ERROR Addding Edge: (%s, %s, %s)", edge.getEdgeID(), edge.getNode1ID(), edge.getNode2ID()));
                System.err.println(e.getMessage());
            } else if(e.getSQLState() != "23505") {
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
        return SingletonHelper.instance;
    }

    public static DatabaseController getTestInstance() {
        return SingletonHelper.testInstance;
    }



    public int addInterpreterRequest(InterpreterRequest iR) {
        try {
            return Connector.insertInterpreter(instanceConnection, iR);
        } catch(SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return 0;
    }
    //TODO: Update this method
    public int updateInterpreterRequest(InterpreterRequest iR) {
        try {
            return Connector.updateInterpreter(instanceConnection, iR);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public  InterpreterRequest getInterpreterRequest(String requestID) {
        try {
            return Connector.selectInterpreter(instanceConnection, requestID);
        } catch(SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return null;
    }

    public  boolean deleteInterpreterRequest(String requestID) {
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

    public  LinkedList<InterpreterRequest> getAllInterpreterRequests() {
        try {
            return Connector.selectAllInterpreters(instanceConnection);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return new LinkedList<InterpreterRequest>();
    }

    public  void deleteTestTables() {
        DBUtil.dropAllTables(instanceConnection);
    }

    private static class SingletonHelper {
        private static final DatabaseController instance = new DatabaseController();
        private static final DatabaseController testInstance = new DatabaseController(true);
    }
}
