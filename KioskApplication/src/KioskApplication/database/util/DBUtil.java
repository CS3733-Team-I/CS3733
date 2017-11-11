package KioskApplication.database.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static database.template.ConnectionDetails.*;
import static database.template.SQLStrings.*;

public class DBUtil {


    public static Connection getCon()throws SQLException{
        try {
            Class.forName(DERBYEMBEDDED);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection con = DriverManager.getConnection(DBURL, DBUSERNAME, getPassword());
        return con;
    }

    public static void closeCon(Connection con) throws SQLException{
        if(con != null){
            con.close();
        }
    }

    public static void createTables(Connection conn) throws SQLException {
        String sql1 = CREATE_NODE_TABLE;

        String sql2 = CREATE_EDGE_TABLE;

        PreparedStatement pstmt1 = conn.prepareStatement(sql1);
        PreparedStatement pstmt2 = conn.prepareStatement(sql2);

        pstmt1.execute();
        pstmt2.execute();
    }
}
