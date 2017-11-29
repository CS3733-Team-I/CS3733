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
        return getConnection(DBURL+DBCREATE);
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
        PreparedStatement pstmt0 = conn.prepareStatement(CREATE_SCHEMA);
        pstmt0.execute();
        PreparedStatement pstmt1 = conn.prepareStatement(CREATE_NODE_TABLE);
        pstmt1.execute();
        PreparedStatement pstmt2 = conn.prepareStatement(CREATE_EDGE_TABLE);
        pstmt2.execute();
        PreparedStatement pstmt3 = conn.prepareStatement(CREATE_EMPLOYEE_TABLE);
        pstmt3.execute();
        PreparedStatement pstmt4 = conn.prepareStatement(CREATE_INTERPRETER_TABLE+
                WITH_SHARED_REQUEST_ATTRIBUTES);
        pstmt4.execute();
        PreparedStatement pstmt5 = conn.prepareStatement(CREATE_SECURITY_TABLE+
                WITH_SHARED_REQUEST_ATTRIBUTES);
        pstmt5.execute();
    }

    public static void dropAllTables(Connection conn) {
        String drop1 = DROP_EDGE_TABLE;
        String drop2 = DROP_INTERPRETER_TABLE;
        String dropSecReq = DROP_SECURITY_TABLE;
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

    }
}
