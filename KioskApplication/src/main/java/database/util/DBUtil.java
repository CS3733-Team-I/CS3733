package database.util;

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
        String schema = CREATE_SCHEMA;

        String sql1 = CREATE_NODE_TABLE;

        String sql2 = CREATE_EDGE_TABLE;

        String sql3 = CREATE_INTERPRETER_TABLE+WITH_SHARED_REQUEST_ATTRIBUTES+WITH_INTERPRETER_ATTRIBUTES;

        PreparedStatement pstmt0 = conn.prepareStatement(schema);
        pstmt0.execute();
        PreparedStatement pstmt1 = conn.prepareStatement(sql1);
        pstmt1.execute();
        PreparedStatement pstmt2 = conn.prepareStatement(sql2);
        pstmt2.execute();
        PreparedStatement pstmt3 = conn.prepareStatement(sql3);
        pstmt3.execute();
    }

    public static void dropAllTables(Connection conn) {
        String drop1 = DROP_EDGE_TABLE;
        String drop2 = DROP_INTERPRETERS_TABLE;
        String drop3 = DROP_NODE_TABLE;
        String drop4 = DROP_SCHEMA;

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
