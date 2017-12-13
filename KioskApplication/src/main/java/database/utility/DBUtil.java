package database.utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static database.template.ConnectionDetails.*;
import static database.template.SQLStrings.*;

public class DBUtil {
    private static Connection getConnection(String database) throws SQLException {
        try {
            Class.forName(DERBYEMBEDDED);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection con = DriverManager.getConnection(database, DBUSERNAME, getPassword());
        return con;
    }

    public static Connection getConnection() throws SQLException {
        return getConnection(DBURL + DBCREATE);
    }

    public static Connection getTestConnection() throws SQLException {
        try {
            return getConnection(DBTESTURL + DBCREATE);
        } catch (SQLException e){
            return getConnection(DBTESTURL);
        }
    }

    public static void closeConnection(Connection con) throws SQLException {
        if(con != null){
            con.close();
        }
    }

    public static Connection closeTestConnection() throws SQLException{
        DriverManager.getConnection(DBTESTURL+DBSHUTDOWN);
        return null;
    }

    public static void createTables(Connection conn) throws SQLException {
        // Schema
        PreparedStatement createSchema = conn.prepareStatement(CREATE_SCHEMA);
        createSchema.execute();

        // Nodes
        PreparedStatement createNodeTable = conn.prepareStatement(CREATE_NODE_TABLE);
        createNodeTable.execute();
        PreparedStatement createNodeUIndex = conn.prepareStatement(CREATE_NODE_UINDEX);
        createNodeUIndex.execute();

        // Edges
        PreparedStatement createEdgeTable = conn.prepareStatement(CREATE_EDGE_TABLE);
        createEdgeTable.execute();
        PreparedStatement createEdgeUIndex = conn.prepareStatement(CREATE_EDGE_UINDEX);
        createEdgeUIndex.execute();

        // Employees
        PreparedStatement createEmployeeTable = conn.prepareStatement(CREATE_EMPLOYEE_TABLE);
        createEmployeeTable.execute();

        // Other
        PreparedStatement createInterpreterTable = conn.prepareStatement(CREATE_INTERPRETER_TABLE+
                WITH_SHARED_REQUEST_ATTRIBUTES);
        createInterpreterTable.execute();
        PreparedStatement createFoodTable = conn.prepareStatement(CREATE_FOOD_TABLE+
                WITH_SHARED_REQUEST_ATTRIBUTES);
        createFoodTable.execute();
        PreparedStatement createSecurityTable = conn.prepareStatement(CREATE_SECURITY_TABLE+
                WITH_SHARED_REQUEST_ATTRIBUTES);
        createSecurityTable.execute();
        PreparedStatement createJanitorTable = conn.prepareStatement(CREATE_JANITOR_TABLE+
                WITH_SHARED_REQUEST_ATTRIBUTES);
        createJanitorTable.execute();

        // Activity logging
        PreparedStatement createActivityTable = conn.prepareStatement(CREATE_ACTIVITY_TABLE);
        createActivityTable.execute();

        PreparedStatement createITTable = conn.prepareStatement(CREATE_IT_TABLE+
                WITH_SHARED_REQUEST_ATTRIBUTES);
        createITTable.execute();
        PreparedStatement createMaintenanceTable = conn.prepareStatement(CREATE_MAINTENANCE_TABLE+
                WITH_SHARED_REQUEST_ATTRIBUTES);
        createMaintenanceTable.execute();
    }

    public static void dropAllTables(Connection conn) {
        String drop1 = DROP_EDGE_TABLE;
        String drop2 = DROP_INTERPRETER_TABLE;
        String dropSecReq = DROP_SECURITY_TABLE;
        String dropJanReq = DROP_JANITOR_TABLE;
        String dropITReq = DROP_IT_TABLE;
        String dropMTReq = DROP_MAINTENANCE_TABLE;
        String drop3 = DROP_NODE_TABLE;
        String drop4 = DROP_EMPLOYEE_TABLE;

        PreparedStatement preparedStatement1 = null;
        try {
            preparedStatement1 = conn.prepareStatement(drop1);
            preparedStatement1.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        PreparedStatement preparedStatement2 = null;
        try {
            preparedStatement2 = conn.prepareStatement(drop2);
            preparedStatement2.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        PreparedStatement preparedStatement5 = null;
        try {
            preparedStatement5 = conn.prepareStatement(dropSecReq);
            preparedStatement5.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            PreparedStatement dropJanStmt = conn.prepareStatement(dropJanReq);
            dropJanStmt.execute();
        } catch (SQLException e){
            e.printStackTrace();
        }

        try {
            PreparedStatement dropITStmt = conn.prepareStatement(dropITReq);
            dropITStmt.execute();
        } catch (SQLException e){
            e.printStackTrace();
        }

        try {
            PreparedStatement dropMTStmt = conn.prepareStatement(dropMTReq);
            dropMTStmt.execute();
        } catch (SQLException e){
            e.printStackTrace();
        }

        PreparedStatement dropITTable = null;
        try{
            dropITTable = conn.prepareStatement(DROP_IT_TABLE);
            dropITTable.execute();
        } catch (SQLException e){
            e.printStackTrace();
        }

        PreparedStatement preparedStatement3 = null;
        try {
            preparedStatement3 = conn.prepareStatement(drop3);
            preparedStatement3.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        PreparedStatement preparedStatement4 = null;
        try {
            preparedStatement4 = conn.prepareStatement(drop4);
            preparedStatement4.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try{
            PreparedStatement dropActivityStmt = conn.prepareStatement(DROP_ACTIVITY_TABLE);
            dropActivityStmt.execute();
        } catch (SQLException e){
            e.printStackTrace();
        }

    }
}
