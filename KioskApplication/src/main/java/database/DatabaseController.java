package database;

import database.connection.Connector;
import database.connection.NotFoundException;
import database.objects.*;
import database.utility.*;
import utility.KioskPermission;
import utility.node.NodeFloor;
import utility.node.NodeType;
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
    public Node getNode(String id) throws DatabaseException, NotFoundException {
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
            if (e.getSQLState() == "23505") {
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

    /**
     * update node with uniqueID
     * @param node
     * @return
     * @throws DatabaseException
     */
    public int updateNodeWithID(Node node) throws DatabaseException {
        try {
            return Connector.updateNodeWithID(instanceConnection, node);
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

    public int addFoodRequest(FoodRequest fR){
        try{
            return Connector.insertFood(instanceConnection, fR);
        }catch(SQLException e){
            if(e.getSQLState() != "23505"){
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

    public int updateFoodRequest(FoodRequest fR){
        try{
            return Connector.updateFood(instanceConnection, fR);
        }catch (SQLException e){
            if(e.getSQLState() != "23505"){
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

    public FoodRequest getFoodRequest(String requestID){
        try {
            return Connector.selectFood(instanceConnection, requestID);
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

    public boolean deleteFoodRequest(String requestID){
        try {
            Connector.deleteFood(instanceConnection, requestID);
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

    public LinkedList<SecurityRequest> getAllSecurityRequests() {
        try {
            return Connector.selectAllSecurity(instanceConnection);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return new LinkedList<SecurityRequest>();
    }

    public LinkedList<FoodRequest> getAllFoodRequests() {
        try {
            return Connector.selectAllFood(instanceConnection);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return new LinkedList<FoodRequest>();
    }

    public LinkedList<FoodRequest> getAllFoodRequest(){
        try {
            return Connector.selectAllFood(instanceConnection);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return new LinkedList<FoodRequest>();
    }

    public void deleteTestTables() {
        DBUtil.dropAllTables(instanceConnection);
    }

    //Employee section
    /**
     * adds an employee to the database currently more expensive because it needs to return the ID as an identifier
     * @param employee
     * @param password
     * @return their ID
     */
    public int addEmployee(Employee employee, String password) throws DatabaseException{
        try{
            PreparedStatement pstmt = instanceConnection.prepareStatement(EMPLOYEE_INSERT, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1,employee.getUsername());
            pstmt.setString(2,employee.getLastName());
            pstmt.setString(3,employee.getFirstName());
            pstmt.setString(4,employee.getPassword(password));
            pstmt.setString(5, employee.getOptionsForDatabase());
            pstmt.setInt(6,employee.getPermission().ordinal());
            pstmt.setInt(7,employee.getServiceAbility().ordinal());
            int result = pstmt.executeUpdate();
            if (result == 1) {
                ResultSet rs = pstmt.getGeneratedKeys();
                rs.next();
                return rs.getInt(1);
            } else {
                return 0;
            }
        } catch (SQLException e){
            DatabaseExceptionType type;
            if(e.getSQLState() != "23505"){
                type = DatabaseExceptionType.DUPLICATE_ENTRY;
            } else{
                e.printStackTrace();
                type = DatabaseExceptionType.MISC_ERROR;
            }
            throw new DatabaseEmployeeException(employee,type);
        }
    }

    // updates all stored information on the employee except their loginID
    public int updateEmployee(Employee employee, String password) throws DatabaseException{
        try{
            PreparedStatement pstmt = instanceConnection.prepareStatement(EMPLOYEE_UPDATE);
            pstmt.setInt(8,employee.getID());
            pstmt.setString(1,employee.getUsername());
            pstmt.setString(2,employee.getLastName());
            pstmt.setString(3,employee.getFirstName());
            pstmt.setString(4,employee.getPassword(password));
            pstmt.setString(5, employee.getOptionsForDatabase());
            pstmt.setInt(6,employee.getPermission().ordinal());
            pstmt.setInt(7,employee.getServiceAbility().ordinal());
            return pstmt.executeUpdate();
        } catch (SQLException e){
            DatabaseExceptionType type;
            if(e.getSQLState() != "23505"){
                type = DatabaseExceptionType.ID_ALREADY_EXISTS;
            } else{
                e.printStackTrace();
                type = DatabaseExceptionType.MISC_ERROR;
            }
            throw new DatabaseEmployeeException(employee, type);
        }
    }

    // Removes the employee from the database
    public boolean removeEmployee(int ID) throws DatabaseException{
        try{
            PreparedStatement pstmt = instanceConnection.prepareStatement(EMPLOYEE_DELETE);
            pstmt.setInt(1, ID);
            return pstmt.execute();
        } catch (SQLException e){
            e.printStackTrace();
            throw new DatabaseException(DatabaseExceptionType.INVALID_ENTRY);
        }
    }

    // Gets a specific employee from the database
    public Employee getEmployee(int loginID) throws DatabaseException{
        Employee employee = null;
        try{
            PreparedStatement pstmt = instanceConnection.prepareStatement(EMPLOYEE_SELECT);
            pstmt.setInt(1,loginID);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                employee = new Employee(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("lastName"),
                        rs.getString("firstName"),
                        rs.getString("password"),
                        rs.getString("options"),
                        KioskPermission.values()[rs.getInt("permission")],
                        RequestType.values()[rs.getInt("serviceAbility")]);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException(DatabaseExceptionType.MISC_ERROR);
        }
        return employee;
    }

    // Gets all employees for the LoginEntity
    public LinkedList<Employee> getAllEmployees() throws DatabaseException{
        try {
            PreparedStatement pstmt = instanceConnection.prepareStatement(EMPLOYEE_SELECT_ALL);
            ResultSet rs = pstmt.executeQuery();

            LinkedList<Employee> employees = new LinkedList<>();
            while(rs.next()) {
                Employee employee = null;
                employee = new Employee(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("lastName"),
                        rs.getString("firstName"),
                        rs.getString("password"),
                        rs.getString("options"),
                        KioskPermission.values()[rs.getInt("permission")],
                        RequestType.values()[rs.getInt("serviceAbility")]);
                employees.add(employee);
            }
            return employees;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException(DatabaseExceptionType.MISC_ERROR);
        }
    }

    /*
    public void insertEmployeeIntoView(IEmployee employee) {
        try {
            Connector.addEmployeeToRequestTable(instanceConnection, employee.getUsername());
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
    }

    public void deleteEmployeeFromView(IEmployee employee) {
        try {
            Connector.deleteEmployeeFromView(instanceConnection, employee.getUsername());
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
    }

    public void insertRequestIntoView(Request request) {
        try {
            Connector.addRequestToTable(instanceConnection, request);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
    }

    public void insertRequestIntoView(String requestID) {
        int uRequestID;
        try {
            uRequestID = Connector.getURequestIDFromRequestID(instanceConnection, requestID);

            Connector.addRequestToTable(instanceConnection, uRequestID);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
    }

    public void deleteRequestFromView(Request request) {
        try {
            Connector.deleteRequestFromView(instanceConnection, request.getuRequestID());
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
    }

    public void deleteRequestFromView(int uRequestID) {
        try {
            Connector.deleteRequestFromView(instanceConnection, uRequestID);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
    }

    public boolean isRequestViewed(IEmployee employee, Request request) {
        try {
            return Connector.selectRequestViewEmployee(instanceConnection, request.getuRequestID(), employee.getUsername());
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean isRequestViewed(IEmployee employee, int uRequestID) {
        try {
            return Connector.selectRequestViewEmployee(instanceConnection, uRequestID, employee.getUsername());
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void setRequestViewed(IEmployee employee, Request request, boolean isViewed) {
        try {
            Connector.updateRequestViewTable(instanceConnection, request.getuRequestID(), employee.getUsername(), isViewed);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
    }

    public void setRequestViewed(IEmployee employee, int uRequestID, boolean isViewed) {
        try {
            Connector.updateRequestViewTable(instanceConnection, uRequestID, employee.getUsername(), isViewed);
        } catch (SQLException e) {
            if(e.getSQLState() != "23505") {
                e.printStackTrace();
            }
        }
    }*/
}
