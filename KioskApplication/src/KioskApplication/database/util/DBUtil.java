package KioskApplication.database.util;

import KioskApplication.database.template.ConnectionDetails;
import KioskApplication.database.template.SQLStrings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBUtil {
    private static Connection getConnection(String database) throws SQLException {
        try {
            Class.forName(ConnectionDetails.DERBYEMBEDDED);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection con = DriverManager.getConnection(database, ConnectionDetails.DBUSERNAME, ConnectionDetails.getPassword());
        return con;
    }

    public static Connection getConnection() throws SQLException {
        return getConnection(ConnectionDetails.DBURL);
    }

    public static Connection getTestConnection() throws SQLException {
        return getConnection(ConnectionDetails.DBTESTURL);
    }

    public static void closeConnection(Connection con) throws SQLException {
        if(con != null){
            con.close();
        }
    }

    public static void createTables(Connection conn) throws SQLException {
        PreparedStatement pstmt1 = conn.prepareStatement(SQLStrings.CREATE_NODE_TABLE);
        PreparedStatement pstmt2 = conn.prepareStatement(SQLStrings.CREATE_EDGE_TABLE);

        pstmt1.execute();
        pstmt2.execute();
    }
}
