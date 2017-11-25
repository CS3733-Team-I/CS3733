package utility.csv;

import com.csvreader.CsvReader;
import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;
import utility.node.NodeBuilding;
import utility.node.NodeFloor;
import utility.node.NodeType;

import java.io.IOException;
import java.net.URISyntaxException;

public class CsvFileUtil {

    public static final String NODE_CSV_HEAD = "nodeID,xcoord,ycoord,floor,building,nodeType,longName,shortName,teamAssigned";
    public static final String EDGE_CSV_HEAD = "edgeID,startNode,endNode";

    public static void readAllCSVs() {
        try {
            readNodesCSV(CsvFileUtil.class.getResource("/csv/MapAnodes.csv").toURI().getPath());
            readNodesCSV(CsvFileUtil.class.getResource("/csv/MapBnodes.csv").toURI().getPath());
            readNodesCSV(CsvFileUtil.class.getResource("/csv/MapCnodes.csv").toURI().getPath());
            readNodesCSV(CsvFileUtil.class.getResource("/csv/MapDnodes.csv").toURI().getPath());
            readNodesCSV(CsvFileUtil.class.getResource("/csv/MapEnodes.csv").toURI().getPath());
            readNodesCSV(CsvFileUtil.class.getResource("/csv/MapFnodes.csv").toURI().getPath());
            readNodesCSV(CsvFileUtil.class.getResource("/csv/MapGnodes.csv").toURI().getPath());
            readNodesCSV(CsvFileUtil.class.getResource("/csv/MapHnodes.csv").toURI().getPath());
            readNodesCSV(CsvFileUtil.class.getResource("/csv/MapInodes.csv").toURI().getPath());
            readNodesCSV(CsvFileUtil.class.getResource("/csv/MapWnodes.csv").toURI().getPath());

            readEdgesCSV(CsvFileUtil.class.getResource("/csv/MapAedges.csv").toURI().getPath());
            readEdgesCSV(CsvFileUtil.class.getResource("/csv/MapBedges.csv").toURI().getPath());
            readEdgesCSV(CsvFileUtil.class.getResource("/csv/MapCedges.csv").toURI().getPath());
            readEdgesCSV(CsvFileUtil.class.getResource("/csv/MapDedges.csv").toURI().getPath());
            readEdgesCSV(CsvFileUtil.class.getResource("/csv/MapEedges.csv").toURI().getPath());
            readEdgesCSV(CsvFileUtil.class.getResource("/csv/MapFedges.csv").toURI().getPath());
            readEdgesCSV(CsvFileUtil.class.getResource("/csv/MapGedges.csv").toURI().getPath());
            readEdgesCSV(CsvFileUtil.class.getResource("/csv/MapHedges.csv").toURI().getPath());
            readEdgesCSV(CsvFileUtil.class.getResource("/csv/MapIedges.csv").toURI().getPath());
            readEdgesCSV(CsvFileUtil.class.getResource("/csv/MapWedges.csv").toURI().getPath());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static void readNodesCSV(String path) {
        MapEntity map = MapEntity.getInstance();
        CsvReader reader = null;

        try {
            reader = new CsvReader(path);
            reader.readHeaders();

            String fields[] = NODE_CSV_HEAD.split(",");
            String headers[] = reader.getHeaders();
            int index = 0;
            for (String header : headers) {
                if (!header.equals(fields[index])) {
                    throw new CsvException("Input file was not of type EDGES_CSV.");
                }

                index++;
            }

            while (reader.readRecord()) {
                String id = reader.get(fields[0]).trim();
                Integer xcoord = Integer.parseInt(reader.get(fields[1]).trim());
                Integer ycoord = Integer.parseInt(reader.get(fields[2]).trim());
                String longName = reader.get(fields[6]).trim();
                String shortName = reader.get(fields[7]).trim();
                String teamAssigned = reader.get(fields[8]).trim();

                String floor = reader.get(fields[3]).trim();
                String building = reader.get(fields[4]).trim();
                String type = reader.get(fields[5]).trim();
                NodeFloor nodeFloor = NodeFloor.LOWERLEVEL_2;
                NodeBuilding nodeBuilding = NodeBuilding.FRANCIS45;
                NodeType nodeType = NodeType.HALL;

                switch (floor) {
                    case "L2":
                        nodeFloor = NodeFloor.LOWERLEVEL_2;
                        break;
                    case "L1":
                        nodeFloor = NodeFloor.LOWERLEVEL_1;
                        break;
                    case "0":
                        nodeFloor = NodeFloor.GROUND;
                        break;
                    case "1":
                        nodeFloor = NodeFloor.FIRST;
                        break;
                    case "2":
                        nodeFloor = NodeFloor.SECOND;
                        break;
                    case "3":
                        nodeFloor = NodeFloor.THIRD;
                        break;
                }

                switch (building) {
                    case "45 Francis":
                        nodeBuilding = NodeBuilding.FRANCIS45;
                        break;
                    case "15 Francis":
                        nodeBuilding = NodeBuilding.FRANCIS15;
                        break;
                    case "Tower":
                        nodeBuilding = NodeBuilding.TOWER;
                        break;
                    case "Sharpiro":
                        nodeBuilding = NodeBuilding.SHAPIRO;
                        break;
                    case "BTM":
                        nodeBuilding = NodeBuilding.BTM;
                        break;
                }

                switch (type) {
                    case "HALL":
                        nodeType = NodeType.HALL;
                        break;
                    case "ELEV":
                        nodeType = NodeType.ELEV;
                        break;
                    case "REST":
                        nodeType = NodeType.REST;
                        break;
                    case "STAI":
                        nodeType = NodeType.STAI;
                        break;
                    case "DEPT":
                        nodeType = NodeType.DEPT;
                        break;
                    case "LABS":
                        nodeType = NodeType.LABS;
                        break;
                    case "INFO":
                        nodeType = NodeType.INFO;
                        break;
                    case "CONF":
                        nodeType = NodeType.CONF;
                        break;
                    case "EXIT":
                        nodeType = NodeType.EXIT;
                        break;
                    case "RETL":
                        nodeType = NodeType.RETL;
                        break;
                    case "SERV":
                        nodeType = NodeType.SERV;
                        break;
                }

                Node node = new Node(id, xcoord, ycoord, nodeFloor, nodeBuilding, nodeType, longName, shortName, teamAssigned);

                // TODO implement proper exception throwing from MapEntity
                // Attempt to add node to map. If that fails, two situations could exit:
                // 1) Node exists in map, in which case we need to edit it instead of updating it
                // 2) Something else went terribly wrong
                try {
                    map.addNode(node);
                } catch (Exception e) {
                    map.editNode(node);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) reader.close();
        }
    }

    public static void writeNodesCSV(String path, boolean global) {
        // TODO implement this
        /*
                NodeFloor fl = NodeFloor.values()[rs.getInt("floor")];
                String floor = "";
                NodeBuilding bu = NodeBuilding.values()[rs.getInt("building")];
                String building = "";
                NodeType nt = NodeType.values()[rs.getInt("nodeType")];
                String nodeType = "";

                if(global) {
                    if(!(rs.getString("NodeID").startsWith("W"))) {
                        continue;
                    }
                } else {
                    if(rs.getString("NodeID").startsWith("W")) {
                        continue;
                    }
                }

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
            }*/
    }

    public static void readEdgesCSV(String path) {
        MapEntity map = MapEntity.getInstance();
        CsvReader reader = null;

        try {
            reader = new CsvReader(path);
            reader.readHeaders();

            String fields[] = EDGE_CSV_HEAD.split(",");
            String headers[] = reader.getHeaders();
            int index = 0;
            for (String header : headers) {
                if (!header.equals(fields[index])) {
                    throw new CsvException("Input file was not of type EDGES_CSV.");
                }

                index++;
            }

            while (reader.readRecord()) {
                String id = reader.get(fields[0]).trim();
                String firstNodeID = reader.get(fields[1]).trim();
                String secondNodeID = reader.get(fields[2]).trim();

                Edge edge = new Edge(id, firstNodeID, secondNodeID);

                // TODO implement proper exception throwing from MapEntity
                // Attempt to add edge to map. If that fails, two situations could exit:
                // 1) Edge exists in map, in which case we need to edit it instead of updating it
                // 2) Something else went terribly wrong
                try {
                    map.addEdge(edge);
                } catch (Exception e) {
                    map.editEdge(edge);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) reader.close();
        }
    }

    public static void writeEdgesCSV(String path, boolean global) {
        // TODO implement this
        /*File csvFile = new File(path);
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

                if(global) {
                    if(!((rs.getString("startNode").startsWith("W")) && (rs.getString("endNode").startsWith("W")))) {
                        continue;
                    }
                } else {
                    if((rs.getString("startNode").startsWith("W")) && (rs.getString("endNode").startsWith("W"))) {
                        continue;
                    }
                }

                bWriter.write(rs.getString("edgeID") + ",");
                bWriter.write(rs.getString("startNode") + ",");
                bWriter.write(rs.getString("endNode") + ",");

                bWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
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
        }*/
    }
}
