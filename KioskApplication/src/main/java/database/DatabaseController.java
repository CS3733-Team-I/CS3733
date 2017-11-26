package database;

import database.connection.Connector;
import database.objects.Edge;
import database.objects.Employee;
import database.objects.Node;
import database.template.SQLStrings;
import database.util.DBUtil;
import database.objects.InterpreterRequest;
import utility.KioskPermission;
import utility.Node.NodeFloor;
import utility.Node.NodeType;
import utility.Request.Language;
import utility.Request.RequestProgressStatus;
import utility.Request.RequestType;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;

import static database.template.SQLStrings.*;


//Node and Edge objects should only be made here
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

    public LinkedList<InterpreterRequest> getAllInterpreterRequests() {
        try {
            return Connector.selectAllInterpreters(instanceConnection);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return new LinkedList<InterpreterRequest>();
    }

    public void deleteTestTables() {
        DBUtil.dropAllTables(instanceConnection);
    }

    private static class SingletonHelper {
        private static final DatabaseController instance = new DatabaseController();
        private static final DatabaseController testInstance = new DatabaseController(true);
    }

    /**
     * Employee database management
     */

    // adds an employee to the database
    public int addEmployee(String loginID, String loginName, String password, KioskPermission permission, RequestType serviceAbility){
        try{
            PreparedStatement pstmt = instanceConnection.prepareStatement(EMPLOYEE_INSERT);
            pstmt.setString(1,loginID);
            pstmt.setString(2,loginName);
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
    public int updateEmployee(String loginID, String loginName, String password, KioskPermission permission, RequestType serviceAbility){
        try{
            PreparedStatement pstmt = instanceConnection.prepareStatement(EMPLOYEE_UPDATE);
            pstmt.setString(5,loginID);
            pstmt.setString(1,loginName);
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
                        rs.getString("loginName"),
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
                        rs.getString("loginName"),
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
