package database;

import database.connection.Connector;
import database.objects.Edge;
import database.objects.Employee;
import database.objects.Node;
import database.utility.*;
import database.objects.SecurityRequest;
import database.objects.InterpreterRequest;
import utility.node.NodeFloor;
import utility.node.NodeType;

import utility.KioskPermission;
import utility.node.TeamAssigned;
import utility.request.RequestType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;

import static database.template.SQLStrings.*;


//node and Edge objects should only be made here
public class DatabaseController {
    private Connection instanceConnection = null;

    public static boolean isTestRunning = false;

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

    private static class SingletonHelper {
        private static final DatabaseController instance = new DatabaseController(false);
        private static final DatabaseController testInstance = new DatabaseController(true);
    }

    public static DatabaseController getInstance() {
        return (isTestRunning ? SingletonHelper.instance : SingletonHelper.testInstance);
    }

    //returns null if node does not exist
    public Node getNode(String id) throws DatabaseException {
        try {
            return Connector.selectNode(instanceConnection, id);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException(DatabaseExceptionType.MISC_ERROR);
        }
    }

    public ArrayList<Node> getAllNodes() throws DatabaseException {
        try{
            return Connector.selectAllNodes(instanceConnection);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException(DatabaseExceptionType.MISC_ERROR);
        }
    }

    public int getNodeTypeCount(NodeType nodeType, NodeFloor floor, TeamAssigned teamAssigned) throws DatabaseException {
        try{
            return Integer.parseInt(Connector.selectCountNodeType(instanceConnection,nodeType, floor, teamAssigned));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException(DatabaseExceptionType.MISC_ERROR);
        }
    }

    public String getAllElevName(NodeFloor floor, TeamAssigned teamAssigned) throws DatabaseException{
        String result = "";
        try{
            result = Connector.selectCountNodeType(instanceConnection, NodeType.ELEV, floor, teamAssigned);
            return result;
        }catch(Exception e){
            e.printStackTrace();
        }
        return "EXC";
    }

    public String selectNodeName(int xcoord, int ycoord, NodeFloor floor, NodeType nodeType) throws DatabaseException{
        String result = "";
        try {
            result = Connector.selectNodeID(instanceConnection, xcoord, ycoord, floor, nodeType);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean removeNode(Node node) throws DatabaseException {
        try {
            Connector.deleteNode(instanceConnection, node);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseNodeException(node, DatabaseExceptionType.INVALID_ENTRY);
        }
    }

    public int addNode(Node node) throws DatabaseException {
        try {
            return Connector.insertNode(instanceConnection, node);
        } catch (SQLException e) {
            DatabaseExceptionType type;
            if (e.getSQLState() != "23505") {
                type = DatabaseExceptionType.DUPLICATE_ENTRY;
            } else {
                e.printStackTrace();
                type = DatabaseExceptionType.MISC_ERROR;
            }
            throw new DatabaseNodeException(node, type);
        }
    }

    public  int updateNode(Node node) throws DatabaseException {
        try {
            return Connector.updateNode(instanceConnection, node);
        } catch (SQLException e) {
            DatabaseExceptionType type;
            if (e.getSQLState() != "23505") {
                type = DatabaseExceptionType.ID_ALREADY_EXISTS;
            } else {
                e.printStackTrace();
                type = DatabaseExceptionType.MISC_ERROR;
            }
            throw new DatabaseNodeException(node, type);
        }
    }

    public  Edge getEdge(String edgeID) throws DatabaseException {
        try {
            return Connector.selectEdge(instanceConnection, edgeID);
        } catch (SQLException e) {
            e.printStackTrace();

            throw new DatabaseException(DatabaseExceptionType.MISC_ERROR);
        }
    }

    public  boolean removeEdge(Edge edge) throws DatabaseException {
        try {
            Connector.deleteEdge(instanceConnection, edge);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();

            throw new DatabaseEdgeException(edge, DatabaseExceptionType.INVALID_ENTRY);
        }
    }

    public int addEdge(Edge edge) throws DatabaseException {
        try {
            return Connector.insertEdge(instanceConnection, edge);
        } catch (SQLException e) {
            if (e.getSQLState() == "23503") {
                throw new DatabaseEdgeException(edge, DatabaseExceptionType.INVALID_ENTRY);
            } else if(e.getSQLState() != "23505") {
                e.printStackTrace();
                throw new DatabaseEdgeException(edge, DatabaseExceptionType.DUPLICATE_ENTRY);
            } else {
                e.printStackTrace();
                throw new DatabaseEdgeException(edge, DatabaseExceptionType.MISC_ERROR);
            }
        }
    }

    public int updateEdge(Edge edge) throws DatabaseException {
        try{
            return Connector.updateEdge(instanceConnection, edge);
        } catch (SQLException e) {
            if (e.getSQLState() == "23503") {
                throw new DatabaseEdgeException(edge, DatabaseExceptionType.INVALID_ENTRY);
            } else if(e.getSQLState() != "23505") {
                e.printStackTrace();
                throw new DatabaseEdgeException(edge, DatabaseExceptionType.DUPLICATE_ENTRY);
            } else {
                e.printStackTrace();
                throw new DatabaseEdgeException(edge, DatabaseExceptionType.MISC_ERROR);
            }
        }
    }

    public  ArrayList<Edge> getAllEdges() throws DatabaseException {
        try{
            return Connector.selectAllEdges(instanceConnection);
        } catch(SQLException e) {
            e.printStackTrace();
            throw new DatabaseException(DatabaseExceptionType.MISC_ERROR);
        }
    }

    // TODO add exceptions for these methods
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

    public int addSecurityRequest(SecurityRequest sR){
        try{
            return Connector.insertSecurity(instanceConnection, sR);
        }catch(SQLException e) {
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

    //TODO: Update this method
    public int updateSecurityRequest(SecurityRequest sR) {
        try {
            return Connector.updateSecurity(instanceConnection, sR);
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

    public  SecurityRequest getSecurityRequest(String requestID) {
        try {
            return Connector.selectSecurity(instanceConnection, requestID);
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

    public  boolean deleteSecurityRequest(String requestID) {
        try {
            Connector.deleteSecurity(instanceConnection, requestID);
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

    public  LinkedList<SecurityRequest> getAllSecurityRequests() {
        try {
            return Connector.selectAllSecurity(instanceConnection);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return new LinkedList<SecurityRequest>();
    }

    public void deleteTestTables() {
        DBUtil.dropAllTables(instanceConnection);
    }

    /**
     * Employee database management
     */

    // adds an employee to the database
    public int addEmployee(String loginID, String userName, String password, KioskPermission permission, RequestType serviceAbility){
        try{
            PreparedStatement pstmt = instanceConnection.prepareStatement(EMPLOYEE_INSERT);
            pstmt.setString(1,loginID);
            pstmt.setString(2,userName);
            pstmt.setString(3,password);
            pstmt.setInt(4,permission.ordinal());
            pstmt.setInt(5,serviceAbility.ordinal());
            return pstmt.executeUpdate();
        } catch (SQLException e){
            if(e.getSQLState() != "23505"){
                e.printStackTrace();
            }
        }
        return 0;
    }

    // updates all stored information on the employee except their loginID
    public int updateEmployee(String loginID, String userName, String password, KioskPermission permission, RequestType serviceAbility){
        try{
            PreparedStatement pstmt = instanceConnection.prepareStatement(EMPLOYEE_UPDATE);
            pstmt.setString(5,loginID);
            pstmt.setString(1,userName);
            pstmt.setString(2,password);
            pstmt.setInt(3,permission.ordinal());
            pstmt.setInt(4,serviceAbility.ordinal());
            return pstmt.executeUpdate();
        } catch (SQLException e){
            if(e.getSQLState() != "23505"){
                e.printStackTrace();
            }
        }
        return 0;
    }

    // Removes the employee from the database
    public boolean removeEmployee(String loginID){
        try{
            PreparedStatement pstmt = instanceConnection.prepareStatement(EMPLOYEE_DELETE);
            pstmt.setString(1, loginID);
            return pstmt.execute();
        } catch (SQLException e){
            if(e.getSQLState() != "23505"){
                e.printStackTrace();
            }
        }
        return false;
    }

    // Gets a specific employee from the database
    public Employee getEmployee(String loginID){
        Employee employee = null;
        try{
            PreparedStatement pstmt = instanceConnection.prepareStatement(EMPLOYEE_SELECT);
            pstmt.setString(1,loginID);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                employee = new Employee(
                        loginID,
                        rs.getString("userName"),
                        rs.getString("password"),
                        KioskPermission.values()[rs.getInt("permission")],
                        RequestType.values()[rs.getInt("serviceAbility")],
                        true);
            }
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return employee;
    }

    // Gets all employees for the LoginEntity
    public LinkedList<Employee> getAllEmployees(){
        try {
            PreparedStatement pstmt = instanceConnection.prepareStatement(EMPLOYEE_SELECT_ALL);
            ResultSet rs = pstmt.executeQuery();

            LinkedList<Employee> employees = new LinkedList<>();
            while(rs.next()) {
                Employee employee = null;
                //for completed InterpreterRequests
                employee = new Employee(
                        rs.getString("loginID"),
                        rs.getString("userName"),
                        rs.getString("password"),
                        KioskPermission.values()[rs.getInt("permission")],
                        RequestType.values()[rs.getInt("serviceAbility")],
                        true);
                employees.add(employee);
            }
            return employees;
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return new LinkedList<Employee>();
    }
}
