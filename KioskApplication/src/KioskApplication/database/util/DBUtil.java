package KioskApplication.database.util;

import KioskApplication.database.template.ConnectionDetails;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static KioskApplication.database.template.ConnectionDetails.*;
import static KioskApplication.database.template.SQLStrings.*;

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
        return getConnection(DBURL);
    }

    public static Connection getTestConnection() throws SQLException {
        return getConnection(DBTESTURL);
    }

    public static void closeConnection(Connection con) throws SQLException {
        if(con != null){
            con.close();
        }
    }

    public static void createTables(Connection conn) throws SQLException {
        String schema = CREATE_SCHEMA;

        String sql1 = CREATE_NODE_TABLE;

        String sql2 = CREATE_EDGE_TABLE;

        String sql3 = CREATE_REQUESTS_TABLE;

        String sql4 = CREATE_INTERPRETERS_TABLE;

        PreparedStatement pstmt0 = conn.prepareStatement(schema);
        pstmt0.execute();

        PreparedStatement pstmt1 = conn.prepareStatement(sql1);
        pstmt1.execute();
        PreparedStatement pstmt2 = conn.prepareStatement(sql2);
        pstmt2.execute();
        PreparedStatement pstmt3 = conn.prepareStatement(sql3);
        pstmt3.execute();
        PreparedStatement pstmt4 = conn.prepareStatement(sql4);
        pstmt4.execute();
    }
}
