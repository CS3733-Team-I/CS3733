package KioskApplication.database.util;


import KioskApplication.database.connection.Connector;
import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static KioskApplication.database.template.SQLStrings.*;
import static KioskApplication.database.template.CSVFormat.*;
import static KioskApplication.database.template.EnumConverter.*;

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
    //    NodeCollection nodeCollection = NodeCollection.getInstance();
        Connection conn = null;
        try {
            while((line = br.readLine()) != null) {
                String[] elements = line.split(",");

                String floor = elements[3].trim();
                String building = elements[4].trim();
                String nodeType = elements[5].trim();
                int fl = -1;
                int bu = -1;
                int nt = -1;

                if(floor.equals("L2")) {
                    fl = LOWERLEVEL2;
                }
                else if(floor.equals("L1")) {
                    fl = LOWERLEVEL1;
                }
                else if(floor.equals("0")) { //CHECK THIS
                    fl = GROUNDFLOOR;
                }
                else if (floor.equals("1")) {
                    fl = FIRSTFLOOR;
                }
                else if (floor.equals("2")) {
                    fl = SECONDFLOOR;
                }
                else if (floor.equals("3")) {
                    fl = THIRDFLOOR;
                }

                if(building.equals("45 Francis")) {
                    bu = FRANCIS45;
                }
                else if(building.equals("Tower")) {
                    bu = TOWER;
                }
                else if(building.equals("Shapiro")) {
                    bu = SHAPIRO;
                }
                else if(building.equals("BTM")) {
                    bu = BTM;
                }


                if(nodeType.equals("ELEV")) {
                    nt = ELEVATOR;
                }
                else if(nodeType.equals("HALL")) {
                    nt= HALL;
                }
                else if(nodeType.equals("DEPT")) {
                    nt = DEPT;
                }
                else if(nodeType.equals("STAI")) {
                    nt = STAI;
                }

                Node node = new Node(elements[0].trim(), Integer.parseInt(elements[1].trim()), Integer.parseInt(elements[2].trim()), fl,
                        bu, nt, elements[6].trim(), elements[7].trim(), elements[8].trim());
                Connector.insertNode(conn, Integer.parseInt(elements[1].trim()),Integer.parseInt(elements[2].trim()),
                        fl, bu, nt, elements[6].trim(), elements[7].trim(), elements[8].trim(), elements[0].trim());
          //      nodeCollection.addNode(node);
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

                int fl = rs.getInt("floor");
                String floor = "";
                int bu = rs.getInt("building");
                String building = "";
                int nt = rs.getInt("nodeType");
                String nodeType = "";

                switch(fl) {
                    case LOWERLEVEL2:
                        floor = "L2";
                        break;
                    case LOWERLEVEL1:
                        floor = "L1";
                        break;
                    case GROUNDFLOOR:
                        floor = "0";
                        break;
                    case FIRSTFLOOR:
                        floor = "1";
                        break;
                    case SECONDFLOOR:
                        floor = "2";
                        break;
                    case THIRDFLOOR:
                        floor = "3";
                        break;
                }

                switch(bu) {
                    case FRANCIS45:
                        building = "45 Francis";
                        break;
                    case TOWER:
                        building = "Tower";
                        break;
                    case SHAPIRO:
                        building = "Shapiro";
                        break;
                    case BTM:
                        building = "BTM";
                }

                switch(nt) {
                    case ELEVATOR:
                        nodeType = "ELEV";
                        break;
                    case HALL:
                        nodeType = "HALL";
                        break;
                    case REST:
                        nodeType = "REST";
                        break;
                    case DEPT:
                        nodeType = "DEPT";
                        break;
                    case STAI:
                        nodeType = "STAI";
                        break;
                }


                bWriter.write(rs.getString("NodeID") + ",");
                bWriter.write(rs.getInt("xcoord") + ",");
                bWriter.write(rs.getInt("ycoord") + ",");
                bWriter.write(floor + ",");
                bWriter.write(building + ",");
                bWriter.write(nodeType + ",");
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

    //    EdgeCollection edgeCollection = EdgeCollection.getInstance();
        Connection conn = null;

        try {
            conn = DBUtil.getCon();
            while ((line = bReader.readLine()) != null) {
                String[] elements = line.split(",");
                Edge edges = new Edge(elements[0].trim(), elements[1].trim(), elements[2].trim());
                Connector.insertEdge(conn, elements[1].trim(), elements[2].trim(), elements[0].trim());
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

