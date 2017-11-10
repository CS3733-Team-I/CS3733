package database.util;


import database.connection.Connector;
import database.objects.Edge;
import database.objects.EdgeCollection;
import database.objects.Node;
import database.objects.NodeCollection;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static database.template.CSVFormat.*;
import static database.template.SQLStrings.*;

public class CSVFileUtil {


    /*
    Order of events for init :
    readNodeCSV();
     : will initialize nodeCollection

     writeNodeCSV() :
     : Pulls from database and writes to CSV




     */

    public static void readNodesCSV(String path) {
        File file = new File(path);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line;
        NodeCollection nodeCollection = NodeCollection.getInstance();
        Connection conn = null;
        try {
            while((line = br.readLine()) != null) {
                String[] elements = line.split(",");
                Node node = new Node(elements[0].trim(), Integer.parseInt(elements[1].trim()), Integer.parseInt(elements[2].trim()), elements[3].trim(),
                        elements[4].trim(), elements[5].trim(), elements[6].trim(), elements[7].trim(), elements[8].trim());
                Connector.insertNode(conn, node);
                nodeCollection.addNode(node);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally{
            try {
                DBUtil.closeCon(conn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void writeNodesCSV(String path) throws SQLException {
        File csvFile = new File(path);
        FileWriter write = null;
        try {
            write = new FileWriter(csvFile, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedWriter bWriter = new BufferedWriter(write);

        Connection conn = null;
        try {
            bWriter.write(NODE_CSV_HEAD);
            bWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            conn = DBUtil.getCon(); //might need to be in a seperate try/catch
            String sql = NODE_SELECT_ALL;
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                bWriter.write(rs.getString("NodeID") + ",");
                bWriter.write(rs.getInt("xcoord") + ",");
                bWriter.write(rs.getInt("ycoord") + ",");
                bWriter.write(rs.getString("floor") + ",");
                bWriter.write(rs.getString("building") + ",");
                bWriter.write(rs.getString("nodeType") + ",");
                bWriter.write(rs.getString("longName") + ",");
                bWriter.write(rs.getString("shortName") + ",");
                bWriter.write(rs.getString("teamAssigned"));
                bWriter.newLine();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bWriter.flush();
                write.close();
                bWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                DBUtil.closeCon(conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void readEdgesCSV(String path) throws SQLException {
        File file = new File(path);
        BufferedReader bReader = null;
        try {
            bReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            bReader.readLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String line;

        EdgeCollection edgeCollection = EdgeCollection.getInstance();
        Connection conn = null;

        try {
            conn = DBUtil.getCon();
            while ((line = bReader.readLine()) != null) {
                String[] elements = line.split(",");
                Edge edges = new Edge(elements[0].trim(), elements[1].trim(), elements[2].trim());
                Connector.insertEdge(conn, edges);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                DBUtil.closeCon(conn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void writeEdgesCSV(String path) throws SQLException {

        File csvFile = new File(path);
        FileWriter write = null;
        try {
            write = new FileWriter(csvFile, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedWriter bWriter = new BufferedWriter(write);
        Connection con = null;
        try {
            bWriter.write(EDGE_CSV_HEAD);
            bWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            con = DBUtil.getCon();
            String sql = EDGE_SELECT_ALL;
            PreparedStatement pstmt = con.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bWriter.write(rs.getString("edgeID") + ",");
                bWriter.write(rs.getString("startNode") + ",");
                bWriter.write(rs.getString("endNode") + ",");

                bWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bWriter.flush();
                write.close();
                bWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                DBUtil.closeCon(con);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}

