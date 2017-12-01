package utility.csv;

import com.csvreader.CsvReader;
import database.objects.Edge;
import database.objects.Node;
import database.utility.DatabaseException;
import database.utility.DatabaseExceptionType;
import entity.MapEntity;
import javafx.scene.control.Alert;
import utility.node.NodeBuilding;
import utility.node.NodeFloor;
import utility.node.NodeType;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CsvFileUtil {

    private CsvFileUtil() {

    }

    private static class SingletonHelper {
        private static CsvFileUtil _instance = new CsvFileUtil();
    }

    public static CsvFileUtil getInstance() { return SingletonHelper._instance; }

    public static final String NODE_CSV_HEAD = "nodeID,xcoord,ycoord,floor,building,nodeType,longName,shortName,teamAssigned";
    public static final String EDGE_CSV_HEAD = "edgeID,startNode,endNode";

    public void readAllCSVs() {
        readNodesCSV("/csv/MapAnodes.csv");
        readNodesCSV("/csv/MapBnodes.csv");
        readNodesCSV("/csv/MapCnodes.csv");
        readNodesCSV("/csv/MapDnodes.csv");
        readNodesCSV("/csv/MapEnodes.csv");
        readNodesCSV("/csv/MapFnodes.csv");
        readNodesCSV("/csv/MapGnodes.csv");
        readNodesCSV("/csv/MapHnodes.csv");
        readNodesCSV("/csv/MapInodes.csv");
        readNodesCSV("/csv/MapWnodes.csv");
        readEdgesCSV("/csv/MapAedges.csv");
        readEdgesCSV("/csv/MapBedges.csv");
        readEdgesCSV("/csv/MapCedges.csv");
        readEdgesCSV("/csv/MapDedges.csv");
        readEdgesCSV("/csv/MapEedges.csv");
        readEdgesCSV("/csv/MapFedges.csv");
        readEdgesCSV("/csv/MapGedges.csv");
        readEdgesCSV("/csv/MapHedges.csv");
        readEdgesCSV("/csv/MapIedges.csv");
        readEdgesCSV("/csv/MapWedges.csv");
    }

    public void readNodesCSV(String path, MapEntity map) {
        CsvReader reader = null;
        InputStream input = getClass().getResourceAsStream(path);
        InputStreamReader isr = new InputStreamReader(input);

        try {
            reader = new CsvReader(isr);
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
                    case "G":
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

                try {
                    map.addNode(node);
                } catch (DatabaseException e) {
                    if (e.getType() == DatabaseExceptionType.ID_ALREADY_EXISTS || e.getType() == DatabaseExceptionType.DUPLICATE_ENTRY) {
                        try {
                            map.editNode(node);
                        } catch (DatabaseException ex) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error editing node to DB");
                            alert.setHeaderText("Error occurred while editing node read from CSV.");
                            alert.setContentText(e.toString());
                            alert.showAndWait();
                        }
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error adding node to DB");
                        alert.setHeaderText("Error occurred while adding node to database from CSV.");
                        alert.setContentText(e.toString());
                        alert.showAndWait();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
                try {
                    isr.close();
                    input.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void readNodesCSV(String path) {
        MapEntity map = MapEntity.getInstance();
        readNodesCSV(path, map);
    }

    public void writeNodesCSV(String path, boolean global) {
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

    public void readEdgesCSV(String path, MapEntity map) {
        CsvReader reader = null;
        InputStream input = getClass().getResourceAsStream(path);
        InputStreamReader isr = new InputStreamReader(input);

        try {
            reader = new CsvReader(isr);
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

                try {
                    map.addEdge(edge);
                } catch (DatabaseException e) {
                    if (e.getType() == DatabaseExceptionType.ID_ALREADY_EXISTS || e.getType() == DatabaseExceptionType.DUPLICATE_ENTRY) {
                        try {
                            map.editEdge(edge);
                        } catch (DatabaseException ex) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error editing edge to DB");
                            alert.setHeaderText("Error occurred while editing edge read from CSV.");
                            alert.setContentText(e.toString());
                            alert.showAndWait();
                        }
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error adding edge to DB");
                        alert.setHeaderText("Error occurred while adding edge to database from CSV.");
                        alert.setContentText(e.toString());
                        alert.showAndWait();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
                try {
                    isr.close();
                    input.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void readEdgesCSV(String path) {
        MapEntity map = MapEntity.getInstance();
        readEdgesCSV(path, map);
    }

    public void writeEdgesCSV(String path, boolean global) {
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
