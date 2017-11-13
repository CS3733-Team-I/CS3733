package KioskApplication.database.util;

import KioskApplication.database.connection.Connector;
import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;
import KioskApplication.database.template.CSVFormat;
import KioskApplication.database.template.SQLStrings;
import KioskApplication.utility.NodeBuilding;
import KioskApplication.utility.NodeFloor;
import KioskApplication.utility.NodeType;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CSVFileUtil {
    public static void readNodesCSV(String path) {
        File file = new File(path);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String line;
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();

            while((line = reader.readLine()) != null) {
                String[] elements = line.split(",");

                String floor = elements[3].trim();
                String building = elements[4].trim();
                String type = elements[5].trim();
                NodeFloor nodeFloor = NodeFloor.LOWERLEVEL_2;
                NodeBuilding nodeBuilding = NodeBuilding.FRANCIS45;
                NodeType nodeType = NodeType.HALL;

                switch (floor) {
                    case "L2": nodeFloor = NodeFloor.LOWERLEVEL_2; break;
                    case "L1": nodeFloor = NodeFloor.LOWERLEVEL_1; break;
                    case "0": nodeFloor = NodeFloor.GROUND; break;
                    case "1": nodeFloor = NodeFloor.FIRST; break;
                    case "2": nodeFloor = NodeFloor.SECOND; break;
                    case "3": nodeFloor = NodeFloor.THIRD; break;
                }

                switch (building) {
                    case "45 Francis": nodeBuilding = NodeBuilding.FRANCIS45; break;
                    case "15 Francis": nodeBuilding = NodeBuilding.FRANCIS15; break;
                    case "Tower": nodeBuilding = NodeBuilding.TOWER; break;
                    case "Sharpiro": nodeBuilding = NodeBuilding.SHAPIRO; break;
                    case "BTM": nodeBuilding = NodeBuilding.BTM; break;
                }

                switch (type) {
                    case "HALL": nodeType = NodeType.HALL; break;
                    case "ELEV": nodeType = NodeType.ELEV; break;
                    case "REST": nodeType = NodeType.REST; break;
                    case "STAI": nodeType = NodeType.STAI; break;
                    case "DEPT": nodeType = NodeType.DEPT; break;
                    case "LABS": nodeType = NodeType.LABS; break;
                    case "INFO": nodeType = NodeType.INFO; break;
                    case "CONF": nodeType = NodeType.CONF; break;
                    case "EXIT": nodeType = NodeType.EXIT; break;
                    case "RETL": nodeType = NodeType.RETL; break;
                    case "SERV": nodeType = NodeType.SERV; break;
                }

                Node node = new Node(elements[0].trim(),
                                     Integer.parseInt(elements[1].trim()),
                                     Integer.parseInt(elements[2].trim()),
                                     nodeFloor,
                                     nodeBuilding,
                                     nodeType,
                                     elements[6].trim(),
                                     elements[7].trim(),
                                     elements[8].trim());

                Connector.insertNode(conn, node);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally{
            try {
                DBUtil.closeConnection(conn);
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
            bWriter.write(CSVFormat.NODE_CSV_HEAD);
            bWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(SQLStrings.NODE_SELECT_ALL);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                NodeFloor fl = NodeFloor.values()[rs.getInt("floor")];
                String floor = "";
                NodeBuilding bu = NodeBuilding.values()[rs.getInt("building")];
                String building = "";
                NodeType nt = NodeType.values()[rs.getInt("nodeType")];
                String nodeType = "";

                switch(fl) {
                    case LOWERLEVEL_2: floor = "L2"; break;
                    case LOWERLEVEL_1: floor = "L1"; break;
                    case GROUND: floor = "0"; break;
                    case FIRST: floor = "1"; break;
                    case SECOND: floor = "2"; break;
                    case THIRD: floor = "3"; break;
                }

                switch(bu) {
                    case FRANCIS45: building = "45 Francis"; break;
                    case FRANCIS15: building = "15 Francis"; break;
                    case TOWER: building = "Tower"; break;
                    case SHAPIRO: building = "Shapiro"; break;
                    case BTM: building = "BTM"; break;
                }

                switch(nt) {
                    case ELEV: nodeType = "ELEV"; break;
                    case HALL: nodeType = "HALL"; break;
                    case REST: nodeType = "REST"; break;
                    case DEPT: nodeType = "DEPT"; break;
                    case STAI: nodeType = "STAI"; break;
                    case LABS: nodeType = "LABS"; break;
                    case INFO: nodeType = "INFO"; break;
                    case CONF: nodeType = "CONF"; break;
                    case EXIT: nodeType = "EXIT"; break;
                    case RETL: nodeType = "RETL"; break;
                    case SERV: nodeType = "SERV"; break;
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
                DBUtil.closeConnection(conn);
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
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            while ((line = bReader.readLine()) != null) {
                String[] elements = line.split(",");
                Edge edge = new Edge(elements[0].trim(), elements[1].trim(), elements[2].trim());
                Connector.insertEdge(conn, edge);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                DBUtil.closeConnection(conn);
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
            bWriter.write(CSVFormat.EDGE_CSV_HEAD);
            bWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            con = DBUtil.getConnection();
            PreparedStatement pstmt = con.prepareStatement(SQLStrings.EDGE_SELECT_ALL);
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
                DBUtil.closeConnection(con);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

